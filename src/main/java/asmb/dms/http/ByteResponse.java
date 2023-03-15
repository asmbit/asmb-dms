package asmb.dms.http;

import java.io.InputStream;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import asmb.dms.Response;

public class ByteResponse implements Response {

	private final Logger log = LoggerFactory.getLogger(ByteResponse.class);

	private int code;
	private boolean success;
	private okhttp3.ResponseBody body;
	private String status;

	public ByteResponse(okhttp3.Request request, okhttp3.OkHttpClient client) {
		okhttp3.Response response = null;
		try {
			response = client.newCall(request).execute();
			status = response.toString();
			code = response.code();
			if (response.isSuccessful() && response.body().contentLength() > 0)
				body = response.body();
			success = (response.code() == 200);
		} catch (Exception e) {
			log.error(e.getMessage());
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
		return null;
	}

	@Override
	public InputStream getByteStream() {
		if (body != null) {
			return body.byteStream();
		} else {
			return null;
		}
	}

	@Override
	public void close() {
		try {
			body.close();
		} catch (Exception e) {
			log.error(e.getMessage());
		}
	}

	@Override
	public String toString() {
		return status;
	}

}
