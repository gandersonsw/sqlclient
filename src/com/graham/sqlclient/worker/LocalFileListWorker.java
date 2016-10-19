/* Copyright (C) 2016 Graham Anderson gandersonsw@gmail.com - All Rights Reserved */
package com.graham.sqlclient.worker;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.graham.tools.StringTools;

public class LocalFileListWorker extends WorkerAbstractImpl {
	
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
	public void run() {
		ob.reset();
		List<File> dirs = new ArrayList<File>();
		dirs.add(new File(ib.path));
		
		FilenameFilter fnf = new MyFilter(ib.filePattern, ib.caseSens);
		for (int dirIndex = 0; dirIndex < dirs.size(); dirIndex++) {
			if (dirs.get(dirIndex).isDirectory()) {
				File f2[] = dirs.get(dirIndex).listFiles(fnf);
				for (int i = 0; i < f2.length; i++) {
					if (f2[i].isFile()) {
						List row = new ArrayList();
						row.add(f2[i]);
						ob.add(row, this);
					} else if (ib.incSubDirs && f2[i].isDirectory()) {
						dirs.add(f2[i]);
					}
					if (ob.stopFlag) {
						return;
					}
				}
			} else {
				ob.error("not a directory:" + dirs.get(dirIndex).getAbsolutePath(), null, false);
			}
		}
		ob.done();
	}
	
	@Override
	public void stop() {
		ob.stopFlag = true;
	}
	
	static List INBOX_PARAM_NAMES = Arrays.asList(new String[] {"Dir Path", "File Pattern", "Case Sensitive", "Include Subdirectories"});
	public class Inbox implements WorkerInboxNone {
		
		String path = "";
		String filePattern = "*.*";
		boolean caseSens; // TODO get / set as Boolean
		boolean incSubDirs; // TODO get / set as Boolean
		
		@Override
		public Object getParam(String name) {
			if (name.equals("Dir Path")) {
				return path;
			} else if (name.equals("File Pattern")) {
				return filePattern;
			} else if (name.equals("Case Sensitive")) {
				return Boolean.valueOf(caseSens);
			} else if (name.equals("Include Subdirectories")) {
				return Boolean.valueOf(incSubDirs);
			}
			return null;
		}

		@Override
		public List<String> getParamNameList() {
			return INBOX_PARAM_NAMES;
		}

		@Override
		public void setParam(String name, Object value) {
			if (name.equals("Dir Path")) {
				path = (String)value;
			} else if (name.equals("File Pattern")) {
				filePattern = (String)value;
			} else if (name.equals("Case Sensitive")) {
				caseSens = ((Boolean)value).booleanValue();
			} else if (name.equals("Include Subdirectories")) {
				incSubDirs = ((Boolean)value).booleanValue();
			}
		}
	}
	
	public class MyFilter implements FilenameFilter {
		int ftype;
		String filter1;
		boolean caseSens;
		public MyFilter(String filter, boolean caseSensParam) {
			caseSens = caseSensParam;
			if (filter.equals("*")) {
				ftype = 0;
				filter1 = filter;
			} else if (filter.startsWith("*") && filter.indexOf('*', 1) == -1) {
				ftype = 1;
				filter1 = filter.substring(1);
			} else if (filter.indexOf('*') == filter.length() - 1) {
				ftype = 2;
				filter1 = filter.substring(0, filter.length()-1);
			} else if (filter.startsWith("*") && filter.indexOf('*', 1) == filter.length() - 1) {
				ftype = 3;
				filter1 = filter.substring(1, filter.length()-1);
			} else {
				ftype = 4;
				filter1 = filter;
			}
			if (!caseSens) {
				filter1 = filter1.toLowerCase();
			}
			//System.out.println("filter1=" + filter1);
			//System.out.println("ftype=" + ftype);
		}

		@Override
		public boolean accept(File arg0, String arg1) {
			if (arg0.isDirectory()) {
				return true; // return all directories, if "Include Subsdirtories" is not checked, it will be filtered out in the "run" method
			}
			if (!caseSens) {
				arg1 = arg1.toLowerCase();
			}
			switch (ftype) {
			case 0:
				return true;
			case 1:
				return arg1.endsWith(filter1);
			case 2:
				return arg1.startsWith(filter1);
			case 3:
				return arg1.indexOf(filter1) != -1;
			case 4:
				return StringTools.match(arg1, filter1) != -1;
			}
			return false;
		}
	}

}
