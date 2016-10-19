/* Copyright (C) 2016 Graham Anderson gandersonsw@gmail.com - All Rights Reserved */
package com.graham.sqlclient.worker;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GetFileHttpWorker extends WorkerAbstractImpl {

	FInbox fin = new FInbox();
	WorkerOutboxListImpl fout = new WorkerOutboxListImpl();
	
	@Override
	public WorkerInbox getWorkerInbox() {
		return fin;
	}

	@Override
	public WorkerOutboxList getWorkerOutbox() {
		return fout;
	}
	/*
	private String getUrl(String pattern, List item) {
		// TODO - use the pattern
		return pattern;
	}
*/
	@Override
	public void run() {
		
		byte buff[] = new byte[1024];
		
		fout.reset();
		while (fin.outbox.hasNext()) {
			List item = fin.outbox.next();
			String urlTxt = compsePatternString(fin.endpoint, item); // getUrl(fin.endpoint, item);
			
			try {
				URL url = new URL(urlTxt);
				URLConnection conn = url.openConnection();
				conn.connect();
				InputStream is = conn.getInputStream();
				
				StringBuilder sb = new StringBuilder();
				int size; 
				while ((size = is.read(buff)) > 0) {
					sb.append(buff); // , 0, size);
					if (fout.stopFlag) {
						is.close();
						return;
					}
				}
				is.close();
				List row = new ArrayList();
				row.add(sb.toString());
				fout.add(row, this);
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				fout.error(null, e, false);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				fout.error(null, e, false);
			}
		}
		
		fout.done();
	}
	
	@Override
	public void stop() {
		fout.stopFlag = true;
	}
	
	static List INBOX_PARAM_NAMES = Arrays.asList(new String[] {"Endpoint", "User", "Password"});
	public class FInbox implements WorkerInboxList {
		
		String endpoint = "";
		String user = "";
		String password = "";
		WorkerOutboxList outbox;

		@Override
		public Object getParam(String name) {
			if (name.equals("Endpoint")) {
				return endpoint;
			} else if (name.equals("User")) {
				return user;
			} else if (name.equals("Password")) {
				return password;
			}
			return null;
		}

		@Override
		public List<String> getParamNameList() {
			return INBOX_PARAM_NAMES;
		}

		@Override
		public void setParam(String name, Object value) {
			if (name.equals("Endpoint")) {
				endpoint = (String)value;
			} else if (name.equals("User")) {
				user = (String)value;
			} else if (name.equals("Password")) {
				password = (String)value;
			}
		}
		
		public void setSource(WorkerOutboxList outboxParam) {
			outbox = outboxParam;
		}
		
	}
	
}
