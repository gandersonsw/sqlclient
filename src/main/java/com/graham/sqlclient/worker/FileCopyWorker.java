/* Copyright (C) 2016 Graham Anderson gandersonsw@gmail.com - All Rights Reserved */
package com.graham.sqlclient.worker;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import com.graham.tools.FileTools;

public class FileCopyWorker extends WorkerAbstractImpl {

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

	@Override
	public void run() {
		ob.reset();
		
		int convMethod = FileTools.CONVERT_NONE;
		if (ib.fileConversion.get("None").equals(Boolean.TRUE)) {
			convMethod = FileTools.CONVERT_NONE;
		} else if (ib.fileConversion.get("PC - Unix").equals(Boolean.TRUE)) {
			convMethod = FileTools.CONVERT_PC_UNIX;
		} else if (ib.fileConversion.get("Unix - PC").equals(Boolean.TRUE)) {
			convMethod = FileTools.CONVERT_UNIX_PC;
		}
		
		while (ib.outbox.hasNext()) {
			List item = ib.outbox.next();
			File inputF = new File((String)item.get(0));
			File outputF = new File((String)item.get(1));
			
			List<Object> row = new ArrayList<Object>();
			row.add(outputF);
			
			try {
				FileTools.copyFile(inputF, outputF, convMethod);
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
	

	static List INBOX_PARAM_NAMES = Arrays.asList(new String[] {"File Conversion"});
	public class FInbox implements WorkerInboxList {
		
		HashMap fileConversion;
		WorkerOutboxList outbox;

		@Override
		public Object getParam(String name) {
			if (name.equals("File Conversion")) {
				if (fileConversion == null) {
					fileConversion = new HashMap();
					fileConversion.put("None", Boolean.valueOf(true));
					fileConversion.put("PC - Unix", Boolean.valueOf(false));
					fileConversion.put("Unix - PC", Boolean.valueOf(false));
				}
				return fileConversion;
			}
			return null;
		}

		@Override
		public List<String> getParamNameList() {
			return INBOX_PARAM_NAMES;
		}

		@Override
		public void setParam(String name, Object value) {
			if (name.equals("File Conversion")) {
				fileConversion = (HashMap)value;
			}
		}
		
		public void setSource(WorkerOutboxList outboxParam) {
			outbox = outboxParam;
		}
		
	}

}
