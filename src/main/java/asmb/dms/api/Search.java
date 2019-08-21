package asmb.dms.api;

import java.util.Map;

import asmb.dms.Response;
import asmb.dms.http.Session;

public class Search extends Api {

	public Search(Session session, String url) {
		super(session, url);
	}

	@Override
	public Response get(Map<String, String> body) {
		throw new UnsupportedOperationException();
	}

	public Response post(Map<String, Object> body) {
		return session.post(url, body, 200);
	}

}
