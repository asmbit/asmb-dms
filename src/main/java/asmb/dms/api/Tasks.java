package asmb.dms.api;

import java.util.HashMap;
import java.util.Map;

import asmb.dms.Response;
import asmb.dms.http.Session;

public class Tasks extends Api {

	public Tasks(Session session, String path) {
		super(session, path);
	}

	public Response put(State state) {
		Map<String, Object> body = new HashMap<String, Object>();
		body.put("state", state.value);
		return session.put(url + "?select=state", body);
	}

	public Items items() {
		return new Items(session, path + "/items");
	}

	public Variables variables() {
		return new Variables(session, path + "/variables");
	}

	public Variables variables(String variableId) {
		return new Variables(session, path + "/variables/" + variableId);
	}

	public class Items extends Api {

		public Items(Session session, String path) {
			super(session, path);
		}

	}

	public class Variables extends Api {

		public Variables(Session session, String url) {
			super(session, url);
		}

		public Response post(String scope, String name, String value, String type) {
			Map<String, Object> body = new HashMap<String, Object>();
			body.put("scope", scope);
			body.put("name", name);
			body.put("value", value);
			body.put("type", type);
			return session.post(url, body);
		}

		public Response delete() {
			return session.delete(url);
		}

	}

	public enum State {
		UNCLAIMED("unclaimed"), CLAIMED("claimed"), DELEGATED("delegated"), COMPLETED("completed"), RESOLVED("resolved");

		public final String value;

		private State(String value) {
			this.value = value;
		}

	}

}
