/* Copyright (C) 2016 Graham Anderson gandersonsw@gmail.com - All Rights Reserved */
package com.graham.sqlclient.worker;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.*;

import com.graham.appshell.tabui.*;
import com.graham.sqlclient.LapaeUIPanelMultipleType;
import com.graham.sqlclient.SqlClientApp;
import com.graham.tools.StringTools;

public class WorkerUIPanel extends AppUIPanelMultiple {
	
	private JTabbedPane tabbed;
	JComboBox workerTypeComboBox;
	boolean firstWorkerAdded = false;
	List<WorkerParam> params = new ArrayList<WorkerParam>();
	
	JPanel workerPanel;
	SqlClientApp app;
	
	Worker firstWorker;
	Worker lastWorker;
	
	JButton startButton;
	JButton stopButton;
	
	JLabel status;
	int totalErrors;
	
	public WorkerUIPanel(SqlClientApp appParam) {
		app = appParam;
		
		JPanel top = new JPanel();
		startButton = new JButton(new RunWorkers2(app));
		top.add(startButton);
		stopButton = new JButton(new StopWorkers());
		stopButton.setEnabled(false);
		top.add(stopButton);
		workerTypeComboBox = new JComboBox();
		for (int i = 0; i < WorkerManager.getWorkerTypeCount(true); i++) {
			workerTypeComboBox.addItem(WorkerManager.getWorkerTypeName(true, i));
		}
		top.add(workerTypeComboBox);
		top.add(new JButton(new AddWorker()));
		status = new JLabel("idle");
		top.add(status);
		
		workerPanel = new JPanel();
		workerPanel.setLayout(new BoxLayout(workerPanel, BoxLayout.PAGE_AXIS));

		JScrollPane scrolledResults = new JScrollPane(workerPanel);
		
		JPanel tab1 = new JPanel(new BorderLayout());
		tab1.add(scrolledResults, BorderLayout.CENTER);
		tab1.add(top, BorderLayout.NORTH);
		
		tabbed = new JTabbedPane();
		tabbed.add("Define", tab1);
		
		//editor = tabbed;
	}
	
	public class AddWorker extends AbstractAction {
		
		private static final long serialVersionUID = 1L;

		public AddWorker() {
			super("Add");
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			
			//String workerName = workerTypeComboBox.getSelectedItem().toString();
			addWorker(workerTypeComboBox.getSelectedItem().toString());
		}
	}
	
	public void addWorker(String workerName) {
		Worker w = WorkerManager.createWorker(workerName, app);
		
		if (w == null) {
			return;
		}
		
		workerPanel.add(getWorkerpanel(workerName, w));
		
		JTextArea txt = new JTextArea();
		JScrollPane txtScr = new JScrollPane(txt);
		JLabel progress = new JLabel("ready");
		JPanel p4 = new JPanel(new BorderLayout());
		p4.add(txtScr, BorderLayout.CENTER);
		p4.add(progress, BorderLayout.SOUTH);
		
		tabbed.add(workerName, p4);
		
		w.getWorkerOutbox().setChangeCallback(new DocWorkerChangeCallback(w, txt, progress));
		
		if (firstWorkerAdded) {
			lastWorker.getWorkerOutbox().setNextWorker(w);
			lastWorker = w;
			//((WorkerInboxList)w.getWorkerInbox()).setSource(lastWorker.getWorkerOutbox());
			//lastWorker = w;
			//lastWorker.setNextWorker(w);
		} else  {
			firstWorkerAdded = true;
			workerTypeComboBox.removeAllItems();
			for (int i = 0; i < WorkerManager.getWorkerTypeCount(false); i++) {
				workerTypeComboBox.addItem(WorkerManager.getWorkerTypeName(false, i));
			}
			firstWorker = w;
			lastWorker = w;
		}
	}
	
	public class StopWorkers extends AbstractAction {
		
		private static final long serialVersionUID = 1L;

		public StopWorkers() {
			super("Stop");
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			WorkerManager.stopWorker(firstWorker);
			startButton.setEnabled(true);
			stopButton.setEnabled(false);
			status.setText(status.getText() + " stopped");
		}
	}

	@Override
	public AppUIPanelType getUIPanelType() {
		return LapaeUIPanelMultipleType.worker;
	}

	@Override
	public HashMap<String,Object> getQuitingObjectSave() {
		
		Worker w = firstWorker;
		if (w == null) {
			return new HashMap<String,Object>();
		}
		ArrayList<WorkerDocumentSaveItem> list = new ArrayList<WorkerDocumentSaveItem>();
		do {
			WorkerDocumentSaveItem ws = new WorkerDocumentSaveItem();
			ws.workerName = WorkerManager.getWorkerTypeName(w);
			ws.params = new HashMap<String,Object>();
			for (WorkerParam wp : params) {
				if (wp.wib == w.getWorkerInbox()) {
					ws.params.put(wp.name, wp.getValue());
				}
			}
			list.add(ws);
		} while ((w = w.getWorkerOutbox().getNextWorker()) != null);

		HashMap<String,Object> ret = new HashMap<String,Object>();
		ret.put("saveItems", list);
		return ret;
	}

	@Override
	public void initStartingUp(HashMap<String,Object> quitingObjectSave) {
		if (quitingObjectSave == null) {
			return;
		}
		ArrayList<WorkerDocumentSaveItem> list = (ArrayList<WorkerDocumentSaveItem>)quitingObjectSave.get("saveItems");
		if (list == null) {
			return;
		}
		
		for (WorkerDocumentSaveItem ws : list) {
			addWorker(ws.workerName);
			for (WorkerParam wp : params) {
				if (wp.wib == lastWorker.getWorkerInbox()) {
					if (ws.params.get(wp.name) != null) { // it can be null if a new parameter was added since last save
						wp.setValue(ws.params.get(wp.name));
						//wp.txt.setText(ws.params.get(wp.name));
					}
				}
			}
		}
	}
	
	public JPanel getWorkerpanel(String workerName, Worker w) {
		JPanel p = new JPanel();
		
		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints labelConstr = new GridBagConstraints();
		labelConstr.weightx = 0.0;
		labelConstr.gridx = 1;
		labelConstr.anchor = GridBagConstraints.WEST;
		labelConstr.insets = new Insets(3,3,3,3);
		GridBagConstraints fieldConstr = new GridBagConstraints();
		fieldConstr.fill = GridBagConstraints.HORIZONTAL;
		fieldConstr.gridx = 2;
		fieldConstr.weightx = 1.0;
		fieldConstr.insets = new Insets(3,3,3,3);
		p.setLayout(gridbag);
		
		JLabel lab = new JLabel(workerName + ":");
		gridbag.setConstraints(lab, labelConstr);
		p.add(lab);
		lab = new JLabel("");
		gridbag.setConstraints(lab, fieldConstr);
		p.add(lab);
		
		WorkerInbox wib = w.getWorkerInbox();
		List<String> pn = wib.getParamNameList();
		for (String name : pn) {
			lab = new JLabel(name + ":");
			gridbag.setConstraints(lab, labelConstr);
			p.add(lab);
			Object obj = wib.getParam(name);
			if (obj instanceof String) {
				JTextArea txt = new JTextArea();
				JScrollPane txtScr = new JScrollPane(txt);
				gridbag.setConstraints(txtScr, fieldConstr);
				p.add(txtScr);
				WorkerParam par = new WorkerParamString(txt, name, wib);
				par.setValue(obj);
				params.add(par);
			} else if (obj instanceof List) {
				JTextArea txt = new JTextArea();
				JScrollPane txtScr = new JScrollPane(txt);
				gridbag.setConstraints(txtScr, fieldConstr);
				p.add(txtScr);
				WorkerParam par = new WorkerParamList(txt, name, wib);
				par.setValue(obj);
				params.add(par);
			} else if (obj instanceof Boolean) {
				JCheckBox cb = new JCheckBox();
				gridbag.setConstraints(cb, fieldConstr);
				p.add(cb);
				WorkerParam par = new WorkerParamBoolean(cb, name, wib);
				par.setValue(obj);
				params.add(par);
			} else if (obj instanceof HashMap) {
				JComboBox cb = new JComboBox();
				gridbag.setConstraints(cb, fieldConstr);
				p.add(cb);
				WorkerParam par = new WorkerParamHashMap(cb, name, wib, (HashMap)obj);
				par.setValue(obj);
				params.add(par);
			} else {
				JLabel j = new JLabel("?");
				gridbag.setConstraints(j, fieldConstr);
				p.add(j);
			}
			
		}
		return p;
	}
	
	public class DocWorkerChangeCallback implements WorkerChangeCallback {
		Worker w;
		JTextArea t;
		JLabel progress;
		StringBuilder buff = new StringBuilder();
		long lastUpdate;
		int saveIndex;
		String errMsg;
		
		public DocWorkerChangeCallback(Worker wp, JTextArea tp, JLabel progressp) {
			w = wp;
			t = tp;
			progress = progressp;
		}
		
		@Override
		public void oneNewRowAdded(int index) {
		
			saveIndex = index + 1;
			WorkerOutboxList wol = w.getWorkerOutbox();
			List l = wol.getItemByIndex(index);
			boolean first = true;
			String s2;
			for (int i = 0; i < l.size(); i++) {
				Object obj = l.get(i);
				if (obj instanceof File) {
					s2 = ((File)obj).getAbsolutePath();
				} else {
					s2 = obj.toString();
				}
				
				if (first) {
					buff.append(s2);
					first = false;
				} else {
					buff.append("\t").append(s2);
				}
			}
			buff.append("\n");
			if (lastUpdate + 500 < System.currentTimeMillis()) {
				flush();
			}
		}
		
		@Override
		public void reset() {
			buff.setLength(0);
			t.setText("");
			saveIndex = 0;
			progress.setText("ready");
			errMsg = null;
		}
		
		@Override
		public void done() {
			t.setText(t.getText() + buff.toString());
			if (errMsg == null) {
				progress.setText("rows: " + saveIndex + "  ... done");
			}
			if (WorkerManager.allDone(lastWorker)) { // orkerManager.allDone(firstWorker) && 
				startButton.setEnabled(true);
				stopButton.setEnabled(false);
				status.setText(status.getText() + " ... complete");
			}
		}
		
		@Override
		public void flush() {
			t.setText(t.getText() + buff.toString());
			buff.setLength(0);
			progress.setText("rows: " + saveIndex);
			lastUpdate = System.currentTimeMillis();
		}
		
		public void error(String notes, Exception e, boolean fatal) {
			totalErrors++;
			
			buff.append("\n\n");
			if (notes != null) {
				buff.append(notes);
				buff.append("\n");
				errMsg = notes;
			}
			if (e != null) {
				buff.append(StringTools.getExceptionTrace(e));
				if (notes == null) {
					errMsg = e.getMessage();
				}
			}
			
			progress.setText("error: " + errMsg);
			status.setText("running ... " + totalErrors + (totalErrors == 1 ? " error" : " errors"));
			//Font f = status.getFont();
			//status.setFont(font);
			
		}
	
	}
	
	public class RunWorkers2 extends AbstractAction {
		
		private static final long serialVersionUID = 1L;
		SqlClientApp app;
		
		public RunWorkers2(SqlClientApp appParam) {
			super("Run");
			app = appParam;

		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			
			startButton.setEnabled(false);
			stopButton.setEnabled(true);
			status.setText("running");
			
			for (WorkerParam wp : params) {
				wp.copyParam();
			}
			
			if (firstWorker != null) {
				totalErrors = 0;
				WorkerManager.startWorker(firstWorker);
			}
		}
	}
	
	public abstract class WorkerParam {
		String name;
		WorkerInbox wib;
		public void copyParam() {
			wib.setParam(name, getValue());
		}
		public abstract Object getValue();
		public abstract void setValue(Object v);
	}
	
	public class WorkerParamString extends WorkerParam {
		private JTextArea txt;
		public WorkerParamString(JTextArea txtp, String namep, WorkerInbox wibp) {
			txt = txtp;
			name = namep;
			wib = wibp;
		}
		public Object getValue() {
			return txt.getText();
		}
		public void setValue(Object v) {
			txt.setText((String)v);
		}
	}
	
	public class WorkerParamList extends WorkerParam {
		private JTextArea txt;
		public WorkerParamList(JTextArea txtp, String namep, WorkerInbox wibp) {
			txt = txtp;
			name = namep;
			wib = wibp;
		}
		public Object getValue() {
			return StringTools.convertListToArray(txt.getText(), "\n");
		}
		public void setValue(Object v) {
			txt.setText(StringTools.convertArrayToList((List)v, "\n"));
		}
	}
	
	public class WorkerParamBoolean extends WorkerParam {
		private JCheckBox cb;
		public WorkerParamBoolean(JCheckBox cbp, String namep, WorkerInbox wibp) {
			cb = cbp;
			name = namep;
			wib = wibp;
		}
		public Object getValue() {
			return Boolean.valueOf(cb.isSelected());
		}
		public void setValue(Object v) {
			cb.setSelected(((Boolean)v).booleanValue());
		}
	}
	
	public class WorkerParamHashMap extends WorkerParam {
		private JComboBox cb;
		HashMap data;
		public WorkerParamHashMap(JComboBox cbp, String namep, WorkerInbox wibp, HashMap datap) {
			cb = cbp;
			name = namep;
			wib = wibp;
			data = datap;
		}
		public Object getValue() {
			Object sel = cb.getSelectedItem();
			for (Object key : data.keySet()) {
				if (key.equals(sel)) {
					data.put(key, Boolean.TRUE);
				} else {
					data.put(key, Boolean.FALSE);
				}
			}
			return data;
		}
		public void setValue(Object v) {
			if (v == null) {
				return;
			}
			cb.removeAllItems();
			HashMap v2 = (HashMap)v;
			String sel = null;
			for (Object key : v2.keySet()) {
				cb.addItem(key);
				if (v2.get(key).equals(Boolean.TRUE)) {
					sel = (String)key;
				}
			}
			data = v2;
			cb.setSelectedItem(sel);
		}
	}

	public JComponent getJComponent() {
		return tabbed;
	}
}
