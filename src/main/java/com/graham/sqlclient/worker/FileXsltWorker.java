/* Copyright (C) 2016 Graham Anderson gandersonsw@gmail.com - All Rights Reserved */
package com.graham.sqlclient.worker;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

public class FileXsltWorker extends WorkerAbstractImpl {
	
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
		
		File xslF = new File(ib.xslFile);
		File outputDir = new File(ib.outDir);
		while (ib.outbox.hasNext()) {
			List item = ib.outbox.next();
			File inputF = (File)item.get(0);
			File outputF = new File(outputDir, inputF.getName());
			
	        TransformerFactory transFact = TransformerFactory.newInstance();
	        Transformer trans;
			try {
				trans = transFact.newTransformer(new StreamSource(xslF));
				//   String fileContent = FileUtil.loadTextFile(curFile);
				//   fileContent = replaceAll(fileContent, "&", "&amp;");
				// JAXP reads data using the Source interface
				//  Source xmlSource = new StreamSource(new StringReader(fileContent));
				//  trans.setParameter("current-xsd-file-name", "OrderNotification");
				trans.transform(new StreamSource(inputF), new StreamResult(outputF));
				
				List<Object> row = new ArrayList<Object>();
				row.add(outputF);
				ob.add(row, this);
				
			} catch (TransformerConfigurationException e) {
				ob.error(null, e, false);
				//e.printStackTrace();
			} catch (TransformerException e) {
				ob.error(null, e, false);
				//e.printStackTrace();
			}
			
			//List<Object> row = new ArrayList<Object>();
			//row.add(outputF);
			//ob.add(row, this);
			if (ob.stopFlag) {
				return;
			}
		}
		ob.done();
	}
	
	static List INBOX_PARAM_NAMES = Arrays.asList(new String[] {"Xsl File", "Out Dir"});
	public class FInbox implements WorkerInboxList {
		
		String xslFile = "";
		String outDir = "";
		WorkerOutboxList outbox;

		@Override
		public Object getParam(String name) {
			if (name.equals("Xsl File")) {
				return xslFile;
			} else if (name.equals("Out Dir")) {
				return outDir;
			}
			return null;
		}

		@Override
		public List<String> getParamNameList() {
			return INBOX_PARAM_NAMES;
		}

		@Override
		public void setParam(String name, Object value) {
			if (name.equals("Xsl File")) {
				xslFile = (String)value;
			} else if (name.equals("Out Dir")) {
				outDir = (String)value;
			}
		}
		
		public void setSource(WorkerOutboxList outboxParam) {
			outbox = outboxParam;
		}
		
	}

}
