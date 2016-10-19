/* Copyright (C) 2016 Graham Anderson gandersonsw@gmail.com - All Rights Reserved */
package com.graham.sqlclient.ui.databrowser;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import com.graham.tools.SimpleMatcher;

public class DataBrowserTableModel extends AbstractTableModel {
	private static final long serialVersionUID = 1L;
	
	private static final int NON_MATCH_OFFSET = 10000;
	
	private List<List<Object>> data;
	private JTable table;
	private boolean verticalOrientation = true;
	private int rowIndexFilter;
	private SimpleMatcher columnNameMatcher;
	private List<Integer> columnIndexMap;

	/**
	 * we start with veritical orinetation
	 * @param dataParam
	 * @param rowIndexParam
	 */
	public DataBrowserTableModel(List<List<Object>> dataParam, int rowIndexParam) {
		rowIndexFilter = rowIndexParam;
		data = dataParam;
	}

	public void setVerticalOrientationFlag(boolean b) {
		verticalOrientation = b;
		fireTableStructureChanged();
		setColWidths();
	}
	
	private void setColWidths() {
		if (verticalOrientation) {
			table.getColumnModel().getColumn(0).setMaxWidth(320);
			table.getColumnModel().getColumn(0).setPreferredWidth(240);
		} else {
			table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
			for (int colIndex = 0; colIndex < getFilteredDataSize(); colIndex++) {
				table.getColumnModel().getColumn(colIndex).setPreferredWidth(120);
			}
		}
	}
	
	public boolean getVerticalOrientationFlag() {
		return verticalOrientation;
	}

	@Override
	public Class<?> getColumnClass(int arg0) {
		return String.class;
	}

	@Override
	public int getColumnCount() {
		if (verticalOrientation) {
			return 2;
		} else {
			return getFilteredDataSize();
		}
	}

	@Override
	public String getColumnName(int arg0) {
		if (verticalOrientation) {
			if (arg0 == 0) {
				return "Name";
			} else {
				return "Value";
			}
		} else {
			return data.get(mapIndex(arg0)).get(0).toString();
		}
	}
	
	public String getSqlColumnName(int rowIndex, int columnIndex) {
		if (verticalOrientation) {
			return data.get(mapIndex(rowIndex)).get(0).toString();
		} else {
			return data.get(mapIndex(columnIndex)).get(0).toString();
		}
	}
	
	public Object getSqlValue(String sqlColumnName, int rowIndex) {
		for (int i = 0; i < data.size(); i++) {
			String curColName = data.get(i).get(0).toString();
			if (sqlColumnName.equalsIgnoreCase(curColName)) {
				return data.get(i).get(rowIndex);
			}
		}
		return null;
	}

	@Override
	public int getRowCount() {
		if (verticalOrientation) {
			return getFilteredDataSize();
		} else {
			return 1;
		}
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		Object obj = getValueAtTyped(rowIndex, columnIndex);
		return obj == null ? null : obj.toString();
	}
	
	public Object getValueAtTyped(int rowIndex, int columnIndex) {
		if (verticalOrientation) {
			List<Object> col = data.get(mapIndex(rowIndex));
			Object obj;
			if (columnIndex == 0) {
				obj = col.get(0);
			} else {
				obj = col.get(rowIndexFilter);
			}
			return obj;
		} else {
			List<Object> col = data.get(mapIndex(columnIndex));
			Object obj = col.get(rowIndexFilter);
			return obj;
		}
	}
	
	public boolean isSqlValueEditable(int rowIndex, int columnIndex) {
		if (verticalOrientation) {
			return columnIndex == 1;
		} else {
			return true;
		}
	}
	
	public void setSqlValueAt(Object value, String sqlColumnName) {
		for (int i = 0; i < data.size(); i++) {
			String curColName = data.get(i).get(0).toString();
			if (sqlColumnName.equalsIgnoreCase(curColName)) {
				data.get(i).set(rowIndexFilter, value);
				if (verticalOrientation) {
					fireTableCellUpdated(i, 1);
				} else {
					fireTableCellUpdated(0, i);
				}
			}
		}
	}
	
	public void setTable1(JTable t) {
		table = t;
		setColWidths();
	}

	public String getValueColumnName(int rowIndex, int columnIndex) {
		if (verticalOrientation) {
			if (columnIndex == 0) {
				return null;
			} else {
				return data.get(mapIndex(rowIndex)).get(0).toString();
			}
		} else {
			List<Object> col = data.get(mapIndex(columnIndex));
			return  col.get(0).toString();
		}
	}
	
	public void setColumnNameMatcher(SimpleMatcher matcherParam) {
		columnNameMatcher = matcherParam;
		
		if (columnNameMatcher.getPassAll()) {
			columnIndexMap = null;
		} else {
			columnIndexMap = new ArrayList<Integer>();
			List<Integer> nonMatching = new ArrayList<Integer>();
			for (int i = 0; i < data.size(); i++) {
				String colName = data.get(i).get(0).toString();
				if (columnNameMatcher.matches(colName)) {
					columnIndexMap.add(i);
				} else {
					nonMatching.add(i + NON_MATCH_OFFSET);
				}
			}
			columnIndexMap.addAll(nonMatching);
		}
		
		fireTableStructureChanged();
		setColWidths();
	}

	private int mapIndex(int index) {
		if (columnIndexMap == null) {
			return index;
		} else {
			int i = columnIndexMap.get(index);
			if (i >= NON_MATCH_OFFSET) {
				return i - NON_MATCH_OFFSET;
			}
			return i;
		}
	}
	
	private int getFilteredDataSize() {
		if (columnIndexMap == null) {
			return data.size();
		} else {
			return columnIndexMap.size();
		}
	}
	
	/**
	 * Return true if this item is filterout out by the table name fitler text
	 */
	public boolean isFilteredOut(int rowIndex, int columnIndex) {
		
		if (columnIndexMap == null) {
			return false;
		} 
		
		int index = verticalOrientation ? rowIndex : columnIndex;

		int i = columnIndexMap.get(index);
		if (i >= NON_MATCH_OFFSET) {
			return true;
		}
		return false;
	}

}
