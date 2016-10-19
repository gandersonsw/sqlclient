/* Copyright (C) 2016 Graham Anderson gandersonsw@gmail.com - All Rights Reserved */
package com.graham.sqlclient.ui.sql;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

public class SQLTableModel extends AbstractTableModel {
	private static final long serialVersionUID = 1L;
	private List<List<Object>> data;
	private int columnCount;
	private int rowCount;

	public List<List<Object>> getData() {
		return data;
	}

	@SuppressWarnings("unchecked")
	public void dataChanged(Object dataParam, boolean firstTime) {
		if (firstTime) {
			data = (List<List<Object>>)dataParam;
			columnCount = data.size();
			rowCount = data.get(0).size() - 1;
			
			fireTableStructureChanged();

		} else {
			int oldRowCount = rowCount + 1;
			rowCount = data.get(0).size() - 1;
			this.fireTableRowsInserted(oldRowCount, rowCount);
		}
	}

	@Override
	public Class<?> getColumnClass(int arg0) {
		return String.class;
	}

	@Override
	public int getColumnCount() {
		return columnCount;
	}

	@Override
	public String getColumnName(int arg0) {
		return data.get(arg0).get(0).toString();
	}

	@Override
	public int getRowCount() {
		return rowCount;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		List<Object> col = data.get(columnIndex);
		Object obj = col.get(rowIndex+1);
		return obj == null ? null : obj.toString();
	}
	
	public Object getValueAtTyped(int rowIndex, int columnIndex) {
		List<Object> col = data.get(columnIndex);
		return col.get(rowIndex+1);
	}
	
	public Object getSerilizableData() {
		if (data == null) {
			return null;
		}
		ArrayList<ArrayList<Object>> ret = new ArrayList<ArrayList<Object>>();
		for (List<Object> l1 : data) {
			ArrayList<Object> slist = new ArrayList<Object>();
			for (Object obj : l1) {
				slist.add(obj == null ? null : obj.toString());
			}
			ret.add(slist);
		}
		
		return ret;
	}

	public int[] getColumnWithName(String columnName) {
		List<Integer> cols = new ArrayList<>();
		if (columnName != null) {
			for (int i = 0; i < data.size(); i++) {
				if (columnName.equals(data.get(i).get(0))) {
					cols.add(i);
				}
			}
		}
		int[] ret = new int[cols.size()];
		for (int i = 0; i < cols.size(); i++) {
			ret[i] = cols.get(i);
		}
		return ret;
	}

}
