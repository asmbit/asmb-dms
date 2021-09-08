package asmb.dms.http;

import groovy.json.JsonSlurper;

import java.io.InputStream;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import asmb.dms.Response;

public class JsonResponse implements Response {

	private final Logger log = LoggerFactory.getLogger(JsonResponse.class);

	private int code;
	private boolean success;
	private Map<String, Object> body;
	private String status;

	@SuppressWarnings("unchecked")
	public JsonResponse(okhttp3.Request request, okhttp3.OkHttpClient client, JsonSlurper parser, int successCode) {
		okhttp3.Response response = null;
		try {
			response = client.newCall(request).execute();
			status = response.toString();
			code = response.code();
			if (response.isSuccessful() && response.body().contentLength() != 0 && response.code() != 204) {
				body = (Map<String, Object>) parser.parse(response.body().byteStream());
			}
			response.body().close();
			success = (response.code() == successCode);
		} catch (Exception e) {
			log.error(e.getMessage());
		} finally {
			response.close();
		}
	}

	@Override
	public int getCode() {
		return code;
	}

	@Override
	public boolean isSuccess() {
		return success;
	}

	@Override
	public Map<String, Object> getBody() {
		return body;
	}

	@Override
	public InputStream getByteStream() {
		return null;
	}

	@Override
	public void close() {
	}

	@Override
	public String toString() {
		return status;
	}

}
