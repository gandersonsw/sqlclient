/* Copyright (C) 2016 Graham Anderson gandersonsw@gmail.com - All Rights Reserved */
package com.graham.sqlclient.ui.compare;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JTable;

import com.graham.sqlclient.ui.sql.SQLTableModel;

public class CompareSrcDataFromSqlUI implements CompareSrcData { // TODO rename this

	private int columnCount;
	private List<String[][]> groups = new ArrayList<String[][]>();
	
	private List<String> columnLabels = new ArrayList<String>();

	public CompareSrcDataFromSqlUI(JTable results, SQLTableModel model) {
		int rows[] = results.getSelectedRows();
		int cols[] = results.getSelectedColumns();
		columnCount = model.getColumnCount();
		if (rows.length < 4) {
			initNoGrouping(model, rows);
		} else {
			initWithGrouping(model, rows, cols);
		}
		
		for (int col = 0; col < model.getColumnCount(); col++) {
			if (rows.length > 3 && isInArr(cols, col)) {
				columnLabels.add(">>> " + model.getColumnName(col) + " <<<");
			} else {
				columnLabels.add(model.getColumnName(col));
			}
		}
	}

	private boolean isInArr(int cols[], int col) {
		for (int i = 0; i < cols.length; i++) {
			if (cols[i] == col) {
				return true;
			}
		}
		
		return false;
	}
	
	private void initWithGrouping(SQLTableModel model, int[] rows, int cols[]) {
		
		Map<String, List<Integer>> groupBuilder = new HashMap<String, List<Integer>>();
		
		for (int i = 0; i < rows.length; i++) {
			StringBuilder iGroupIndexBdr = new StringBuilder();
			for (int j = 0; j < cols.length; j++) {
				iGroupIndexBdr.append(model.getValueAt(rows[i], cols[j]));
				iGroupIndexBdr.append("^Z^");
			}
			String iGroupIndex = iGroupIndexBdr.toString();
			
			List<Integer> curGroup;
			if (groupBuilder.containsKey(iGroupIndex)) {
				curGroup = groupBuilder.get(iGroupIndex);
			} else {
				curGroup = new ArrayList<Integer>();
				groupBuilder.put(iGroupIndex, curGroup);
			}

			curGroup.add(rows[i]);
		}
		
		// done creating groupBuilder
	//	groupCount = groupBuilder.size();
		for (List<Integer> groupRowList : groupBuilder.values()) {
			if (groupRowList.size() > 1) { // drop groups with only 1
			
				String[][] group = new String[groupRowList.size()][columnCount];
			
				for (int i = 0; i < groupRowList.size(); i++) {
					int row = groupRowList.get(i); //  rows[i];
					for (int col = 0; col < columnCount; col++) {
						group[i][col] = obj2str(model.getValueAt(row, col));
					}
				}
				
				groups.add(group);
			}
		}
	}
	
	private void initNoGrouping(SQLTableModel model, int[] rows) {
	//	groupCount = 1;
		String[][] group1 = new String[rows.length][columnCount];
		for (int i = 0; i < rows.length; i++) {
			int row = rows[i];
			for (int col = 0; col < columnCount; col++) {
				group1[i][col] = obj2str(model.getValueAt(row, col));
			}
		}
		groups.add(group1);
	}
	
	private String obj2str(final Object obj) {
		if (obj == null) {
			return null;
		}
		return obj.toString();
	}
	
	@Override
	public int getGroupCount() {
		return groups.size();
	}

	@Override
	public int getColumnCount() {
		return columnCount;
	}

	@Override
	public int getRowCount(int groupIndex) {
		return groups.get(groupIndex).length;
	}

	@Override
	public String getData(int groupIndex, int rowIndex, int columnIndex) {
		return groups.get(groupIndex)[rowIndex][columnIndex];
	}

	@Override
	public String getColumnLabel(int columnIndex) {
		return columnLabels.get(columnIndex);
	}

}
