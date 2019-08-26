package asmb.dms.http;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import asmb.dms.Response;
import asmb.dms.Utils;
import groovy.json.JsonOutput;
import groovy.json.JsonSlurper;
import okhttp3.Credentials;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okio.ByteString;

public class Session {

	private final String user;
	private final String admin;
	private final String server;
	private final JsonSlurper parser;
	private final OkHttpClient client;

	private String ticket;

	public Session(String user, String server, String admin, String password, Long timeout) {

		if (server == null)
			server = Config.SERVER;
		if (admin == null)
			admin = Config.ADMIN;
		if (password == null)
			password = Config.PASSWORD;
		if (timeout == null)
			timeout = Config.TIMEOUT;

		this.user = user;
		this.admin = Credentials.basic(admin, password);
		this.server = "http://" + server;
		this.parser = new JsonSlurper();
		this.client = new OkHttpClient.Builder().connectTimeout(timeout, TimeUnit.MILLISECONDS).readTimeout(timeout, TimeUnit.MILLISECONDS)
				.writeTimeout(timeout, TimeUnit.MILLISECONDS).build();
	}

	public Response get(String url, Map<String, String> query) {
		return call(request(url, query), 200);
	}

	public Response delete(String url) {
		return call(request(url).delete(), 204);
	}

	public Response put(String url, Map<String, Object> body) {
		RequestBody post = body(body);
		return call(request(url).put(post), 200);
	}

	public Response post(String url, Map<String, Object> body) {
		RequestBody post = body(body);
		return call(request(url).post(post), 201);
	}

	public Response post(String url, Map<String, Object> body, int successStatus) {
		RequestBody post = body(body);
		return call(request(url).post(post), successStatus);
	}

	public Response post(String url, File file, String name, String nodeType, Map<String, String> properties) {
		MultipartBody body = body(file, name, nodeType, properties);
		return call(request(url).post(body), 201);
	}

	public Response put(String url, File file) {
		RequestBody body = body(file);
		return call(request(url).put(body), 200);
	}

	public Response file(String url, Map<String, String> query) {
		return call(request(url, query), null);
	}

	private Response call(Request.Builder builder, Integer successCode) {
		Response response = null;
		boolean retry = true;
		boolean renew = false;
		while (retry) {
			Request request = builder.addHeader("Authorization", getTicket(renew)).build();
			response = successCode == null ? new ByteResponse(request, client) : new JsonResponse(request, client, parser, successCode);
			if (response.getCode() == 401 && !renew) {
				renew = true;
			} else {
				retry = false;
			}
		}
		return response;

	}

	private Request.Builder request(String path) {
		return request(path, null);
	}

	private Request.Builder request(String path, Map<String, String> params) {
		HttpUrl.Builder url = HttpUrl.parse(server + path).newBuilder();
		if (params != null)
			for (String param : params.keySet())
				url.addQueryParameter(param, params.get(param));
		return new Request.Builder().url(url.build());
	}

	private RequestBody body(Map<String, Object> body) {
		return RequestBody.create(Config.MEDIATYPE_JSON, JsonOutput.toJson(body));
	}

	private RequestBody body(File file) {
		return RequestBody.create(Config.MEDIATYPE_OCTET_STREAM, file);
	}

	private MultipartBody body(File file, String name, String nodeType, Map<String, String> properties) {
		MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
		builder.addFormDataPart("name", name);
		builder.addFormDataPart("nodeType", nodeType);
		if (file != null)
			builder.addFormDataPart("filedata", name, RequestBody.create(Config.MEDIATYPE_OCTET_STREAM, file));
		if (properties != null)
			for (String property : properties.keySet())
				builder.addFormDataPart(property, properties.get(property));
		return builder.build();
	}

	private String getTicket(boolean renew) {
		if (ticket == null || renew) {
			Request request = request(Config.TICKET + user).addHeader("Authorization", admin).build();
			Response response = new JsonResponse(request, client, parser, 200);
			if (response.isSuccess())
				ticket = "Basic " + ByteString.of(Utils.subValue(Utils.subMap(response.getBody(), "data"), "ticket").getBytes()).base64();
			response.close();
			if (!response.isSuccess())
				throw new RuntimeException("invalid ticket request " + response.getCode());
		}
		return ticket;
	}

	private static class Config {

		private final static String[] files = { "asmb/dms/test-config.properties", "asmb/dms/config.properties" };

		protected final static String SERVER;
		protected final static String ADMIN;
		protected final static String PASSWORD;
		protected final static String TICKET;
		protected final static Long TIMEOUT;

		protected final static MediaType MEDIATYPE_OCTET_STREAM = MediaType.parse("application/octet-stream");
		protected static final MediaType MEDIATYPE_JSON = MediaType.parse("application/json; charset=utf-8");

		static {
			try {
				Properties props = load();
				SERVER = props.getProperty("server");
				ADMIN = props.getProperty("admin");
				PASSWORD = props.getProperty("pw");
				TICKET = props.getProperty("ticket");
				TIMEOUT = new Long(props.getProperty("timeout"));
			} catch (Exception e) {
				throw new RuntimeException("missing config.properties");
			}
		}

		private static Properties load() throws IOException {
			Properties props = new Properties();
			InputStream is = null;
			int i = 0;
			while (is == null) {
				is = Config.class.getClassLoader().getResourceAsStream(files[i++]);
			}
			props.load(is);
			is.close();
			return props;
		}

	}

}
