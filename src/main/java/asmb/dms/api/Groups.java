package asmb.dms.api;

import asmb.dms.http.Session;

public class Groups extends Api {

	public Groups(Session session, String path) {
		super(session, path);
	}

	public Members members() {
		return new Members(session, path + "/members");
	}

	public class Members extends Api {

		public Members(Session session, String path) {
			super(session, path);
		}

	}

}
