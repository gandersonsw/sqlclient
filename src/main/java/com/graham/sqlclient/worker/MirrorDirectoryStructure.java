/* Copyright (C) 2016 Graham Anderson gandersonsw@gmail.com - All Rights Reserved */
package com.graham.sqlclient.worker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MirrorDirectoryStructure extends WorkerAbstractImpl {
	
	Inbox ib = new Inbox();
	WorkerOutboxListImpl ob = new WorkerOutboxListImpl();

	@Override
	public WorkerInbox getWorkerInbox() {
		return ib;
	}

	@Override
	public WorkerOutboxList getWorkerOutbox() {
		return ob;
	}

	@Override
	public void stop() {
		ob.stopFlag = true;
	}

	@Override
	public void run() {
		ob.reset();
		
		for (String path : ib.paths) {
			List row = new ArrayList();
			row.add(ib.root1 + path);
			row.add(ib.root2 + path);
			ob.add(row, this);
			if (ob.stopFlag) {
				return;
			}
		}
		
		ob.done();
	}
	
	
	static List INBOX_PARAM_NAMES = Arrays.asList(new String[] {"Root 1", "Root 2", "Paths"});
	public class Inbox implements WorkerInboxNone {
		
		String root1 = "";
		String root2 = "";
		List<String> paths = new ArrayList<String>();
		
		@Override
		public Object getParam(String name) {
			if (name.equals("Root 1")) {
				return root1;
			} else if (name.equals("Root 2")) {
				return root2;
			} else if (name.equals("Paths")) {
				return paths;
			}
			return null;
		}

		@Override
		public List<String> getParamNameList() {
			return INBOX_PARAM_NAMES;
		}

		@Override
		public void setParam(String name, Object value) {
			if (name.equals("Root 1")) {
				root1 = (String)value;
			} else if (name.equals("Root 2")) {
				root2 = (String)value;
			} else if (name.equals("Paths")) {
				paths = (List)value;
			}
		}
	}

}
