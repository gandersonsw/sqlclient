/* Copyright (C) 2016 Graham Anderson gandersonsw@gmail.com - All Rights Reserved */
package com.graham.sqlclient.worker;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FileMergeWorker extends WorkerAbstractImpl {
	
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
		FileWriter fw = null;
		
		File outF = new File(ib.outFile);
		try {
		fw = new FileWriter(outF);
		
		char buff[] = new char[1024];
		int bytesRead;
		
		final String newline = System.getProperty("line.separator");
		
		while (ib.outbox.hasNext()) {
			List item = ib.outbox.next();
			FileReader fr = null;
			try {
				File inputF = (File)item.get(0);
				fr = new FileReader(inputF);
			
				while ((bytesRead = fr.read(buff)) != -1) {
					fw.write(buff, 0 ,bytesRead);
				}
				fw.write(ib.delim);
				if (ib.appendFileNameFlag) {
					fw.write(inputF.getName());
					fw.write(newline);
				}
			} catch (Exception e) {
				ob.error(null, e, false);
			} finally {
				if (fr != null) {
					try { fr.close(); } catch (IOException ioe) { }
				}
			}
			
			if (ob.stopFlag) {
				return;
			}
		}
		
		} catch (Exception e) {
			ob.error(null, e, false);
		} finally {
			if (fw != null) {
				try { fw.close(); } catch (IOException ioe) { }
				}
		}
		List<Object> row = new ArrayList<Object>();
		row.add(outF);
		ob.add(row, this);
		
		ob.done();
	}
	
	static List INBOX_PARAM_NAMES = Arrays.asList(new String[] {"Out File", "Append File name", "Delimiter"});
	public class FInbox implements WorkerInboxList {
		
		String outFile = "";
		Boolean appendFileNameFlag = Boolean.FALSE;
		String delim = ""; 
		WorkerOutboxList outbox;

		@Override
		public Object getParam(String name) {
			if (name.equals("Out File")) {
				return outFile;
			} else if (name.equals("Append File name")) {
				return appendFileNameFlag;
			} else if (name.equals("Delimiter")) {
				return delim;
			}
			return null;
		}

		@Override
		public List<String> getParamNameList() {
			return INBOX_PARAM_NAMES;
		}

		@Override
		public void setParam(String name, Object value) {
			if (name.equals("Out File")) {
				outFile = (String)value;
			} else if (name.equals("Append File name")) {
				appendFileNameFlag = (Boolean)value;
			} else if (name.equals("Delimiter")) {
				delim = (String)value;
			}
		}
		
		public void setSource(WorkerOutboxList outboxParam) {
			outbox = outboxParam;
		}
		
	}

}
