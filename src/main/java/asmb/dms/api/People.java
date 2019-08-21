package asmb.dms.api;

import asmb.dms.http.Session;

public class People extends Api {

	public People(Session session, String path) {
		super(session, path);
	}

	public Groups groups() {
		return new Groups(session, path + "/groups");
	}

	public Favorites favorites() {
		return new Favorites(session, path + "/favorites");
	}

	public class Groups extends Api {

		public Groups(Session session, String path) {
			super(session, path);
		}
	}

	public class Favorites extends Api {

		public Favorites(Session session, String path) {
			super(session, path);
		}
	}

}
