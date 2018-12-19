/* Copyright (C) 2016 Graham Anderson gandersonsw@gmail.com - All Rights Reserved */
package com.graham.sqlclient.worker;

import com.graham.sqlclient.SqlClientApp;

public class WorkerManager {

	public static int getWorkerTypeCount(boolean inBoxNoneFlag) {
		if (inBoxNoneFlag) {
			return 3;
		} else {
			return 6;
		}
	}
	
	public static String getWorkerTypeName(boolean inBoxNoneFlag, int index) {
		if (inBoxNoneFlag) {
			if (index == 0) {
				return "Local File List";
			} else if (index == 1) {
				return "Dir Mirror";
			}
		} else {
			if (index == 0) {
				return "SQL Join";
			} else if (index == 1) {
				return "HTTP File";
			} else if (index == 2) {
				return "File Xslt";
			} else if (index == 3) {
				return "File Copy";
			} else if (index == 4) {
				return "File Tree Flatten";
			} else if (index == 5) {
				return "File Merge";
			}
		}
		return null;
	}
	
	public static String getWorkerTypeName(Worker w) {
		if (w instanceof GetFileHttpWorker) {
			return "HTTP File";
		} else if (w instanceof LocalFileListWorker) {
			return "Local File List";
		} else if (w instanceof FileXsltWorker) {
			return "File Xslt";
		} if (w instanceof MirrorDirectoryStructure) {
			return "Dir Mirror";
		} else if (w instanceof FileCopyWorker) {
			return "File Copy";
		} else if (w instanceof FileTreeFlatten2) {
			return "File Tree Flatten";
		} else if (w instanceof FileMergeWorker) {
			return "File Merge";
		}
		return null;
	}
	
	public static Worker createWorker(String name, SqlClientApp app) {
		if (name.equals("SQL Join")) {
			return null;
		} else if (name.equals("HTTP File")) {
			return new GetFileHttpWorker();
		} else if (name.equals("Local File List")) {
			return new LocalFileListWorker();
		} else if (name.equals("File Xslt")) {
			return new FileXsltWorker();
		} else if (name.equals("Dir Mirror")) {
			return new MirrorDirectoryStructure();
		} else if (name.equals("File Copy")) {
			return new FileCopyWorker();
		} else if (name.equals("File Tree Flatten")) {
			return new FileTreeFlatten2();
		} else if (name.equals("File Merge")) {
			return new FileMergeWorker();
		}
		return null;
	}
	
	public static void startWorker(Worker w) {
		Thread thread = new Thread(w);
		thread.start();
		
		Worker w2 = w.getWorkerOutbox().getNextWorker();
		if (w2 != null) {
			startWorker(w2);
		}
	}
	
	public static boolean allDone(Worker w) {
		if (w.getWorkerOutbox().getTotalCountSoFar() == -1) {
			return false;
		}
		Worker w2 = w.getWorkerOutbox().getNextWorker();
		if (w2 != null) {
			allDone(w2);
		}
		return true;
	}
	
	public static void stopWorker(Worker w) {
		w.stop();
		
		Worker w2 = w.getWorkerOutbox().getNextWorker();
		if (w2 != null) {
			stopWorker(w2);
		}
	}
	
}
