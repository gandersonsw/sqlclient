/* Copyright (C) 2016 Graham Anderson gandersonsw@gmail.com - All Rights Reserved */
package com.graham.sqlclient.ui.sql.history;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.AbstractTableModel;

import com.graham.sqlclient.ui.sql.SQLHistoryItem;
import com.graham.appshell.App;
import com.graham.appshell.data.AppData;
import com.graham.appshell.data.DataManagerList;
import com.graham.tools.DateTools;
import com.graham.tools.SearchContext;
import com.graham.tools.SimpleMatcher;
import com.graham.tools.StringTools;

public class SqlHistoryTableModel extends AbstractTableModel {
	private static final long serialVersionUID = 1L;

	private DataManagerList dm;
	private JTextField sqlHistoryFilterField;
	private List<SQLHistoryItem> data;
	private boolean isDataCurrent;
	private transient JTable table;
	private MyListener listener;
	
	public void setTable1(JTable t) {
		table = t;
	}

	public SqlHistoryTableModel(App app) {
		dm = app.getDataManagerList(SQLHistoryItem.class);
		listener = new MyListener();
		dm.addDataChangeListener(listener);
	}
	
	/**
	 * Check if there are more than 200 previous statements, and trim down to 180 if there are.
	 */
	public void trimSqlHistLength(AppData dataArray[]) {
		if (dataArray.length > 200) {
			List<String> del = new ArrayList<String>();
			for (int i = 0; i < 20; i++) {
				del.add(dataArray[i].getPrimaryKey());
			}
			//System.out.println("SqlHistoryTableModel:trimSqlHistLength: " + del.size());
			dm.delete(del);
		}
	}
	
	public void dataChanged() {
		isDataCurrent = false;
		fireTableStructureChanged();

		table.getColumnModel().getColumn(1).setMaxWidth(200);
		table.getColumnModel().getColumn(1).setPreferredWidth(180);
			
		table.getColumnModel().getColumn(2).setMaxWidth(120);
		table.getColumnModel().getColumn(2).setPreferredWidth(100);
	}
	
	private void checkDataCurrent() {
		if (isDataCurrent) {
			return;
		}

		Date start = new Date();
		
		AppData dataArray[] = dm.getSortedArray();
		trimSqlHistLength(dataArray);

		data = new ArrayList<SQLHistoryItem>();
		SimpleMatcher matcher = new SimpleMatcher("*" + sqlHistoryFilterField.getText() + "*");
		SearchContext searchCtx = SearchContext.createReplaceAllContext("\n", " ");
		for (int i = dataArray.length - 1; i >= 0; i--) {
			SQLHistoryItem item = (SQLHistoryItem)dataArray[i];
			if (matcher.matches(StringTools.replaceAll(item.sql, searchCtx))) {
				data.add(item);
			}
		}
		
		System.out.println("SqlHistoryTableModel:loading data complete:" + DateTools.formatTimeSpan(new Date().getTime() - start.getTime()));
		isDataCurrent = true;
	}
	
	@Override
	public Class<?> getColumnClass(int arg0) {
		return String.class;
	}

	@Override
	public int getColumnCount() {
		return 3;
	}

	@Override
	public String getColumnName(int arg0) {
		switch (arg0) {
		case 0:
			return "Sql";
		case 1:
			return "Last Ran";
		case 2:
			return "Row Count";
		}
		return "??";
	}

	@Override
	public int getRowCount() {
		checkDataCurrent();
		return data.size();
	}
	
	public SQLHistoryItem getSQLHistoryAt(int rowIndex, int columnIndex) {
		checkDataCurrent();
		return data.get(rowIndex);
	}
	
	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		checkDataCurrent();
		SQLHistoryItem h = data.get(rowIndex);
		switch (columnIndex) {
		case 0:
			return StringTools.replaceAll(h.sql, SearchContext.createReplaceAllContext("\n", " "));
		case 1:
			return h.createdTime.toString();
		case 2:
			if (h.wasUpdate) {
				return h.updateCount + " updated";
			} else {
				if (h.hasMoreResults) {
					return h.resultCount + "+";
				} else {
					return h.resultCount;
				}
			}
			
		}
		return "??";
	}
	
	public class MyListener implements ChangeListener {

		@Override
		public void stateChanged(ChangeEvent arg0) {
			// TODO Auto-generated method stub
			dataChanged();
		}
		
	}
	
	public void setFilterTextField(JTextField textField) {
		sqlHistoryFilterField = textField;
		this.dataChanged();
	}
	
	public DocumentListener getTextFilterCL() {
		return new sqlFilterChangeListener();
	}
	
	class sqlFilterChangeListener implements DocumentListener {
	
		public sqlFilterChangeListener() {
		
		}
		
		@Override
		public void changedUpdate(DocumentEvent arg0) {
			dataChanged();
		}

		@Override
		public void insertUpdate(DocumentEvent arg0) {
			dataChanged();
		}

		@Override
		public void removeUpdate(DocumentEvent arg0) {
			dataChanged();
		}
	}

	public void close() {
		dm.removeDataChangeListener(listener);
	}

}
