/* Copyright (C) 2016 Graham Anderson gandersonsw@gmail.com - All Rights Reserved */
package com.graham.sqlclient.worker;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import com.graham.tools.FileTools;

/**
 * 
 * Input should be from MirrorDirectoryStructure.  This will create
 * 
 * @author ganderson
 *
 */
public class FileTreeFlatten2 extends WorkerAbstractImpl {

	FInbox ib = new FInbox();
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

	private int getConvMethod(HashMap m) {
		int convMethod = FileTools.CONVERT_NONE;
		if (m.get("None").equals(Boolean.TRUE)) {
			convMethod = FileTools.CONVERT_NONE;
		} else if (m.get("PC - Unix").equals(Boolean.TRUE)) {
			convMethod = FileTools.CONVERT_PC_UNIX;
		} else if (m.get("Unix - PC").equals(Boolean.TRUE)) {
			convMethod = FileTools.CONVERT_UNIX_PC;
		}
		return convMethod;
	}
	
	@Override
	public void run() {
		ob.reset();
		
		int convMethod1 = getConvMethod(ib.fileConversion1);
		int convMethod2 = getConvMethod(ib.fileConversion2);
		
		File root1DirDst = new File(ib.toPath, ib.root1Dir);
		File root2DirDst = new File(ib.toPath, ib.root2Dir);
		
		FileTools.makeEmptyDir(root1DirDst);
		FileTools.makeEmptyDir(root2DirDst);
		
		while (ib.outbox.hasNext()) {
			List item = ib.outbox.next();
			File root1FileSrc = new File((String)item.get(0));
			File root2FileSrc = new File((String)item.get(1));
			
			File root1FileDst = new File(root1DirDst, root1FileSrc.getName());
			File root2FileDst = new File(root2DirDst, root2FileSrc.getName());
			
			List<Object> row = new ArrayList<Object>();
			row.add(root1FileDst);
			row.add(root2FileDst);
			
			try {
				FileTools.copyFile(root1FileSrc, root1FileDst, convMethod1);
				FileTools.copyFile(root2FileSrc, root2FileDst, convMethod2);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				row.add(e.getMessage());
				ob.error("copyFile failed", e, false);
			}

			ob.add(row, this);
			if (ob.stopFlag) {
				return;
			}
		}
		ob.done();
	}
	

	static List INBOX_PARAM_NAMES = Arrays.asList(new String[] {"File Conversion 1", "File Conversion 2", "To Path", "Root1 Dir", "Root2 Dir"});
	public class FInbox implements WorkerInboxList {
		
		HashMap fileConversion1;
		HashMap fileConversion2;
		String toPath = "";
		String root1Dir = "root1";
		String root2Dir = "root2";
		WorkerOutboxList outbox;

		private HashMap getDefaultFileConv() {
			HashMap fileConversion = new HashMap();
			fileConversion.put("None", Boolean.valueOf(true));
			fileConversion.put("PC - Unix", Boolean.valueOf(false));
			fileConversion.put("Unix - PC", Boolean.valueOf(false));
			return fileConversion;
		}
		
		@Override
		public Object getParam(String name) {
			if (name.equals("File Conversion 1") || name.equals("File Conversion 2")) {
				if (fileConversion1 == null) {
					fileConversion1 = getDefaultFileConv();
				}
				return fileConversion1;
			}
			if (name.equals("File Conversion 2")) {
				if (fileConversion2 == null) {
					fileConversion2 = getDefaultFileConv();
				}
				return fileConversion2;
			}
			if (name.equals("To Path")) {
				return toPath;
			}
			if (name.equals("Root1 Dir")) {
				return root1Dir;
			}
			if (name.equals("Root2 Dir")) {
				return root2Dir;
			}
			return null;
		}

		@Override
		public List<String> getParamNameList() {
			return INBOX_PARAM_NAMES;
		}

		@Override
		public void setParam(String name, Object value) {
			if (name.equals("File Conversion 1")) {
				fileConversion1 = (HashMap)value;
			} else if (name.equals("File Conversion 2")) {
				fileConversion2 = (HashMap)value;
			} else if (name.equals("To Path")) {
				toPath = (String)value;
			} else if (name.equals("Root1 Dir")) {
				root1Dir = (String)value;
			} else if (name.equals("Root2 Dir")) {
				root2Dir = (String)value;
			} 
		}
		
		public void setSource(WorkerOutboxList outboxParam) {
			outbox = outboxParam;
		}
		
	}

}
