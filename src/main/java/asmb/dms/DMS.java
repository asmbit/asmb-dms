package asmb.dms;

import asmb.dms.api.Groups;
import asmb.dms.api.Nodes;
import asmb.dms.api.People;
import asmb.dms.api.Processes;
import asmb.dms.api.Search;
import asmb.dms.api.Tasks;
import asmb.dms.http.Session;

public class DMS {

	private final static String API_CORE = "/alfresco/api/-default-/public/alfresco/versions/1";
	private final static String API_WORKFLOW = "/alfresco/api/-default-/public/workflow/versions/1";
	private final static String API_SEARCH = "/alfresco/api/-default-/public/search/versions/1";

	private final Session session;

	public DMS(String user) throws Exception {
		this(user, null, null, null, null);
	}

	public DMS(String user, String server, String admin, String password, Long timeout) throws Exception {
		session = new Session(user, server, admin, password, timeout);
	}

	public Nodes nodes(String nodeId) {
		String query = null;
		if (nodeId.startsWith("/")) {
			query = "?relativePath=" + nodeId;
			nodeId = "-root-";
		}
		return new Nodes(session, API_CORE + "/nodes/" + nodeId, query);
	}

	public Search search() {
		return new Search(session, API_SEARCH + "/search");
	}

	public People people() {
		return new People(session, API_CORE + "/people");
	}

	public People people(String peopleId) {
		return new People(session, API_CORE + "/people/" + peopleId);
	}

	public Groups groups() {
		return new Groups(session, API_CORE + "/groups");
	}

	public Groups groups(String groupId) {
		return new Groups(session, API_CORE + "/groups/" + groupId);
	}

	public Processes processes() {
		return new Processes(session, API_WORKFLOW + "/processes");
	}

	public Processes processes(String processId) {
		return new Processes(session, API_WORKFLOW + "/processes/" + processId);
	}

	public Tasks tasks() {
		return new Tasks(session, API_WORKFLOW + "/tasks");
	}

	public Tasks tasks(String taskId) {
		return new Tasks(session, API_WORKFLOW + "/tasks/" + taskId);
	}

}
