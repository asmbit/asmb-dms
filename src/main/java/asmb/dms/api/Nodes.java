package asmb.dms.api;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import asmb.dms.Response;
import asmb.dms.http.Session;

public class Nodes extends Api {

	public Nodes(Session session, String path, String query) {
		super(session, path, query);
	}

	public Response put(Map<String, Object> body) {
		return session.put(url, body);
	}

	public Response delete() {
		return session.delete(url);
	}

	public Children children() {
		return new Children(session, path + "/children", query);
	}

	public Content content() {
		return new Content(session, path + "/content", query);
	}

	public Lock lock() {
		return new Lock(session, path + "/lock", query);
	}

	public Move move() {
		return new Move(session, path + "/move", query);
	}

	public Unlock unlock() {
		return new Unlock(session, path + "/unlock", query);
	}

	public Comments comments() {
		return new Comments(session, path + "/comments", query);
	}

	public Parents parents() {
		return new Parents(session, path + "/parents", query);
	}

	public Sources sources() {
		return new Sources(session, path + "/sources", query);
	}

	public Tags tags() {
		return new Tags(session, path + "/tags", query);
	}

	public Tags tags(String tagId) {
		return new Tags(session, path + "/tags/" + tagId, query);
	}

	public Targets targets() {
		return new Targets(session, path + "/targets", query);
	}

	public Targets targets(String targetId) {
		return new Targets(session, path + "/targets/" + targetId, query);
	}

	public SecondaryChildren secondaryChildren() {
		return new SecondaryChildren(session, path + "/secondary-children/", query);
	}

	public SecondaryChildren secondaryChildren(String childId) {
		return new SecondaryChildren(session, path + "/secondary-children/" + childId, query);
	}

	public class Children extends Api {

		public Children(Session session, String path, String query) {
			super(session, path, query);
		}

		public Response post(String name, String nodeType) {
			return post(name, nodeType, null);
		}

		public Response post(File file) {
			return post(file, file.getName(), "cm:content", null);
		}

		public Response post(String name, String nodeType, Map<String, String> properties) {
			Map<String, Object> body = new HashMap<String, Object>();
			body.put("name", name);
			body.put("nodeType", nodeType);
			if (properties != null)
				body.put("properties", properties);
			return post(body);
		}

		public Response post(Map<String, Object> body) {
			if (query != null)
				illegalPath();
			return session.post(url, body);
		}

		public Response post(File file, String name, String nodeType, Map<String, String> properties) {
			if (query != null)
				illegalPath();
			return session.post(url, file, name, nodeType, properties);
		}

	}

	public class Comments extends Api {

		public Comments(Session session, String path, String query) {
			super(session, path, query, true);
		}

		public Response post(String content) {
			Map<String, Object> body = new HashMap<String, Object>();
			body.put("content", content);
			return session.post(url, body);
		}

	}

	public class Parents extends Api {

		public Parents(Session session, String path, String query) {
			super(session, path, query, true);
		}
	}

	public class Sources extends Api {

		public Sources(Session session, String path, String query) {
			super(session, path, query, true);
		}
	}

	public class Tags extends Api {

		public Tags(Session session, String path, String query) {
			super(session, path, query, true);
		}

		public Response post(String tag) {
			Map<String, Object> body = new HashMap<String, Object>();
			body.put("tag", tag);
			return session.post(url, body);
		}

		public Response delete() {
			return session.delete(url);
		}

	}

	public class Targets extends Api {

		public Targets(Session session, String path, String query) {
			super(session, path, query, true);
		}

		public Response post(String targetId, String assocType) {
			Map<String, Object> body = new HashMap<String, Object>();
			body.put("targetId", targetId);
			body.put("assocType", assocType);
			return session.post(url, body);
		}

		public Response delete() {
			return session.delete(url);
		}

	}

	public class SecondaryChildren extends Api {

		public SecondaryChildren(Session session, String path, String query) {
			super(session, path, query, true);
		}

		public Response post(String childId, String assocType) {
			Map<String, Object> body = new HashMap<String, Object>();
			body.put("childId", childId);
			body.put("assocType", assocType);
			return session.post(url, body);
		}

		public Response delete() {
			return session.delete(url);
		}

	}

	public class Lock extends Api {

		public Lock(Session session, String path, String query) {
			super(session, path, query, true);
		}

		public Response post() {
			return post(0, "ALLOW_OWNER_CHANGES", "PERSISTENT");
		}

		public Response post(int timeToExpire, String type, String lifetime) {
			Map<String, Object> body = new HashMap<String, Object>();
			body.put("timeToExpire", timeToExpire);
			body.put("type", type);
			body.put("lifetime", lifetime);
			return session.post(url, body, 200);
		}

	}

	public class Unlock extends Api {

		public Unlock(Session session, String path, String query) {
			super(session, path, query, true);
		}

		public Response post() {
			return session.post(url, null, 200);
		}

	}

	public class Move extends Api {

		public Move(Session session, String path, String query) {
			super(session, path, query, true);
		}

		public Response post(String targetParentId) {
			return post(targetParentId, null);
		}

		public Response post(String targetParentId, String name) {
			Map<String, Object> body = new HashMap<String, Object>();
			body.put("targetParentId", targetParentId);
			body.put("name", name);
			return session.post(url, body, 200);
		}

	}

	public class Content extends Api {

		public Content(Session session, String path, String query) {
			super(session, path, query, true);
		}

		@Override
		public Response get(Map<String, String> query) {
			return session.file(url, query);
		}

		public Response put(File file) {
			return put(file, false);
		}

		public Response put(File file, boolean majorVersion) {
			Map<String, String> query = new HashMap<String, String>();
			if (majorVersion) {
				query.put("majorVersion", "true");
			}
			return session.put(url, file, query);
		}

	}

}
