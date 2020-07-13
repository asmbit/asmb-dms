package asmb.dms.api;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import asmb.dms.Response;
import asmb.dms.http.Session;

public class Processes extends Api {

	public Processes(Session session, String url) {
		super(session, url);
	}

	public Response post(String processDefinitionKey, String assignee) {
		return post(processDefinitionKey, assignee, null, null, null, null);
	}

	public Response post(String processDefinitionKey, String assignee, String description, Date dueDate, Integer priority, Boolean sendEMailNotifications) {
		Map<String, Object> body = new HashMap<String, Object>();
		body.put("processDefinitionKey", processDefinitionKey);
		Map<String, Object> variables = new HashMap<String, Object>();
		if (assignee != null)
			variables.put("bpm_assignee", assignee);
		if (description != null)
			variables.put("bpm_workflowDescription", description);
		if (dueDate != null)
			variables.put("bpm_workflowDueDate", dueDate);
		if (priority != null)
			variables.put("bpm_workflowPriority", priority);
		if (sendEMailNotifications != null)
			variables.put("bpm_sendEMailNotifications", sendEMailNotifications);
		if (variables.size() > 0)
			body.put("variables", variables);
		return post(body);
	}

	public Response post(Map<String, Object> body) {
		return session.post(url, body);
	}

	public Response delete() {
		return session.delete(url);
	}

	public Items items() {
		return new Items(session, path + "/items");
	}

	public Variables variables() {
		return new Variables(session, path + "/variables");
	}

	public Tasks tasks() {
		return new Tasks(session, path + "/tasks");
	}

	public class Items extends Api {

		public Items(Session session, String url) {
			super(session, url);
		}

		public Response post(String id) {
			Map<String, Object> body = new HashMap<String, Object>();
			body.put("id", id);
			return session.post(url, body);
		}

	}

	public class Variables extends Api {

		public Variables(Session session, String path) {
			super(session, path);
		}

		public Response post(String name, String value, String type) {
			Map<String, Object> body = new HashMap<String, Object>();
			body.put("name", name);
			body.put("value", value);
			body.put("type", type);
			return session.post(url, body);
		}

		public Response delete() {
			return session.delete(url);
		}

	}

	public class Tasks extends Api {

		public Tasks(Session session, String path) {
			super(session, path);
		}

	}

}
