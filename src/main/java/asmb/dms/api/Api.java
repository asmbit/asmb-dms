package asmb.dms.api;

import java.util.Map;

import asmb.dms.Response;
import asmb.dms.http.Session;

abstract class Api {

	protected final Session session;
	protected final String path;
	protected final String query;
	protected final String url;

	public Api(Session session, String path) {
		this(session, path, null);
	}

	public Api(Session session, String path, String query) {
		this(session, path, query, false);
	}

	public Api(Session session, String path, String query, boolean strict) {
		if (strict && query != null)
			illegalPath();
		this.session = session;
		this.path = path;
		this.query = query;
		this.url = path + (query == null ? "" : query);
	}

	public Response get() {
		return get(null);
	}

	public Response get(Map<String, String> body) {
		return session.get(url, body);
	}

	protected void illegalPath() {
		throw new IllegalArgumentException("use node ID instead of PATH");
	}

}
