/* Copyright (C) 2016 Graham Anderson gandersonsw@gmail.com - All Rights Reserved */
package com.graham.sqlclient.ui.compare;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.graham.appshell.tabui.*;
import com.graham.sqlclient.LapaeUIPanelMultipleType;
import com.graham.tools.StringTools;

public class CompareUIPanel extends AppUIPanelMultiple {

	private JTable table;
	private JTable textDiff;
	private CompareTextRender compareTextRender;
	private JSplitPane editor;
	
	public class CompareDataModel extends AbstractTableModel {

		final CompareSrcDataFromSqlUI data;
		
		public CompareDataModel(final CompareSrcDataFromSqlUI dataParam) {
			data = dataParam;
		}
		
		@Override
		public int getRowCount() {
			return data.getColumnCount();
		}

		@Override
		public int getColumnCount() {
			int count = 0;
			for (int i = 0; i < data.getGroupCount(); i++) {
				count += data.getRowCount(i);
			}
			return count + 1;
		}

		@Override
		public Object getValueAt(final int rowIndex, final int columnIndex) {
			
			if (columnIndex == 0) {
				return data.getColumnLabel(rowIndex);
			}

			int groupIndex = 0;
			int groupsRowIndex = columnIndex - 1;
			for (int i = 0; i < data.getGroupCount(); i++) {
				if (groupsRowIndex >= data.getRowCount(i)) {
					groupsRowIndex -= data.getRowCount(i);
					groupIndex++;
				} else {
					break;
				}
			}

			return data.getData(groupIndex, groupsRowIndex, rowIndex);
		}
		
		
		public Object getGroup1stValue(final int rowIndex, final int columnIndex) {
			
			if (columnIndex == 0) {
				return data.getColumnLabel(rowIndex);
			}
			
			int groupIndex = 0;
			int groupsRowIndex = columnIndex - 1;
			for (int i = 0; i < data.getGroupCount(); i++) {
				if (groupsRowIndex >= data.getRowCount(i)) {
					groupsRowIndex -= data.getRowCount(i);
					groupIndex++;
				} else {
					break;
				}
			}

			return data.getData(groupIndex, 0, rowIndex);
		}
		
		public int getGroupIndex(int columnIndex) {
			if (columnIndex == 0) {
				return -1;
			}
			
			int groupIndex = 0;
			int groupsRowIndex = columnIndex - 1;
			for (int i = 0; i < data.getGroupCount(); i++) {
				if (groupsRowIndex >= data.getRowCount(i)) {
					groupsRowIndex -= data.getRowCount(i);
					groupIndex++;
				} else {
					break;
				}
			}
			
			return groupIndex;
		}
		
		public int getGroupsRowIndex(int columnIndex) {
			if (columnIndex == 0) {
				return -1;
			}
			
			int groupIndex = 0;
			int groupsRowIndex = columnIndex - 1;
			for (int i = 0; i < data.getGroupCount(); i++) {
				if (groupsRowIndex >= data.getRowCount(i)) {
					groupsRowIndex -= data.getRowCount(i);
					groupIndex++;
				} else {
					break;
				}
			}
			
			return groupsRowIndex;
		}
		
		public boolean isMatching(final int rowIndex, final int columnIndex) {
			if (columnIndex == 0) {
				return true;
			}

			int groupIndex = 0;
			int groupsRowIndex = columnIndex - 1;
			for (int i = 0; i < data.getGroupCount(); i++) {
				if (groupsRowIndex >= data.getRowCount(i)) {
					groupsRowIndex -= data.getRowCount(i);
					groupIndex++;
				} else {
					break;
				}
			}
			
			if (groupsRowIndex == 0) {
				return true;
			}

			final String thisValue = data.getData(groupIndex, groupsRowIndex, rowIndex);
			final String firstValue = data.getData(groupIndex, 0, rowIndex);

			return StringTools.equalWithNullCheck(thisValue,firstValue);
		}
		
	}
	
	public class CompareTextModel extends AbstractTableModel {
		
		int rowCount;
		List<String> textArr1 = new ArrayList<String>();
		List<String> textArr2 = new ArrayList<String>();
		List<Boolean> isTheSame = new ArrayList<Boolean>();
		List<Boolean> isTheSameAnywhere1 = new ArrayList<Boolean>();
		List<Boolean> isTheSameAnywhere2 = new ArrayList<Boolean>();
		int matchedToColumn = -1;
		List<Boolean> matched = new ArrayList<Boolean>();
		
		public CompareTextModel(final String text1, final String text2) {
			if (text1 != null) {
				StringTokenizer t1 = new StringTokenizer(text1, "\n");
				while (t1.hasMoreElements()) {
					textArr1.add(t1.nextToken());
				}
			}
			
			if (text2 != null) {
				StringTokenizer t2 = new StringTokenizer(text2, "\n");
				while (t2.hasMoreElements()) {
					textArr2.add(t2.nextToken());
				}
			}

			if (textArr1.size() > textArr2.size()) {
				rowCount = textArr1.size();
			} else {
				rowCount = textArr2.size();
			}
			
			tryToMatch();
		}
		
		private void tryToMatch() {
			for (int i = 0; i < rowCount; i++) {
				boolean eq = StringTools.equalWithNullCheck((String)getValueAt(i, 0), (String)getValueAt(i, 1));
				isTheSame.add(eq);
				if (eq) {
					isTheSameAnywhere1.add(true);
					isTheSameAnywhere2.add(true);
				} else {
					isTheSameAnywhere1.add(findAnyMatch((String)getValueAt(i, 1), 0));
					isTheSameAnywhere2.add(findAnyMatch((String)getValueAt(i, 0), 1));
				}
			}
		}

		@Override
		public int getRowCount() {
			return rowCount;
		}

		@Override
		public int getColumnCount() {
			return 2;
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			if (columnIndex == 0) {
				if (rowIndex >= textArr1.size()) {
					return "";
				} else {
					return textArr1.get(rowIndex);
				}
			} else {
				if (rowIndex >= textArr2.size()) {
					return "";
				} else {
					return textArr2.get(rowIndex);
				}
			}
		}
		
		/**
		 * is this value the same as the other value in the same row
		 */
		public boolean isTheSame(int rowIndex, int columnIndex) {
			return isTheSame.get(rowIndex);
		}
		
		/**
		 * is this value matched to the selected cell in the other column
		 */
		public boolean isMatched(int rowIndex, int columnIndex) {
			if (columnIndex == matchedToColumn) {
				return matched.get(rowIndex);
			}
			return false;
		}
		
		public boolean isTheSameAnywhere(int rowIndex, int columnIndex) {
			if (columnIndex == 1) {
				return isTheSameAnywhere1.get(rowIndex);
			}
			return isTheSameAnywhere2.get(rowIndex);
		}

		public void setMatcherText(int selectedRow, int selectedColumn) {
			matchedToColumn = selectedColumn == 0 ? 1 : 0;
			
			final String txt = (String)getValueAt(selectedRow, selectedColumn);
			matched = new ArrayList<Boolean>();
			for (int i = 0; i < rowCount; i++) {
				if (txt == null || txt.length() == 0) {
					matched.add(false);
				} else {
					boolean m = txt.equals(getValueAt(i, matchedToColumn));
					matched.add(m);
				}
			}
		}
		
		private boolean findAnyMatch(String txt, int column) {
			if (txt == null || txt.length() == 0) {
				return true;
			}
			for (int i = 0; i < rowCount; i++) {
				if (txt.equals(getValueAt(i, column))) {
					return true;
				}
			}
			return false;
		}
	}

	class MyTabSelListener implements ListSelectionListener {

		@Override
		public void valueChanged(ListSelectionEvent e) {
			
			if (e.getValueIsAdjusting()) {
				return;
			}

			if (table.getSelectedRow() == -1) {
				return;
			}

			CompareDataModel dataModel = (CompareDataModel)table.getModel();
			
			int gri = dataModel.getGroupsRowIndex(table.getSelectedColumn());
			
			if (gri == 0) {
				// don't compare with self
				CompareTextModel textModel = new CompareTextModel("", "");
				textDiff.setModel(textModel);
				return;
			}
			
			final String selectedText = (String)dataModel.getValueAt(table.getSelectedRow(), table.getSelectedColumn());
			final String compareToText = (String)dataModel.getGroup1stValue(table.getSelectedRow(), table.getSelectedColumn());
			CompareTextModel textModel = new CompareTextModel(compareToText, selectedText);
			compareTextRender.setModel(textModel);
			textDiff.setModel(textModel);
		}
	}
	
	
	class MyTextTabSelListener implements ListSelectionListener {

		@Override
		public void valueChanged(ListSelectionEvent e) {
			if (e.getValueIsAdjusting()) {
				return;
			}
			
			if (textDiff.getSelectedRow() == -1) {
				return;
			}

			CompareTextModel dataModel = (CompareTextModel)textDiff.getModel();
			final String selectedText = (String)dataModel.getValueAt(textDiff.getSelectedRow(), textDiff.getSelectedColumn());
			final int otherColumn = textDiff.getSelectedColumn() == 0 ? 1 : 0;
			
			dataModel.setMatcherText(textDiff.getSelectedRow(),textDiff.getSelectedColumn());
			
			textDiff.repaint();
		}
	}
	

	public CompareUIPanel() {
		table = new JTable();
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		table.setCellSelectionEnabled(true);
		MyTabSelListener mtsl = new MyTabSelListener();
		table.getSelectionModel().addListSelectionListener(mtsl); // listens to row changes
		table.getColumnModel().getSelectionModel() .addListSelectionListener(mtsl); // listens to column changes
		JScrollPane tableScroll1 = new JScrollPane(table);

		textDiff = new JTable();
		textDiff.setCellSelectionEnabled(true);
		compareTextRender = new CompareTextRender();
		textDiff.setDefaultRenderer(Object.class, compareTextRender);
		MyTextTabSelListener listener2 = new MyTextTabSelListener();
		textDiff.getSelectionModel().addListSelectionListener(listener2); // listens to row changes
		textDiff.getColumnModel().getSelectionModel() .addListSelectionListener(listener2); // listens to column changes
		JScrollPane tableScroll2 = new JScrollPane(textDiff);

		editor = new JSplitPane(JSplitPane.VERTICAL_SPLIT, tableScroll1, tableScroll2);
	}
	
	@Override
	public AppUIPanelType getUIPanelType() {
		return LapaeUIPanelMultipleType.compare;
	}

	public boolean throwAwayWhenQuiting() {
		return true;
	}
	
	@Override
	public HashMap<String, Object> getQuitingObjectSave() {
		HashMap<String, Object> m = new HashMap<String, Object>();
		return m;
	}

	@Override
	public void initStartingUp(HashMap<String, Object> quitingObjectSave) {
	}

	public void setSrcData(CompareSrcDataFromSqlUI data) {
		CompareDataModel model = new CompareDataModel(data);
		table.setDefaultRenderer(Object.class, new CompareCellRender(model));

		table.setModel(model);

		for (int colIndex = 0; colIndex < model.getColumnCount(); colIndex++) {
			table.getColumnModel().getColumn(colIndex).setPreferredWidth(120);
		}
		
		//editor.doLayout();
		
		editor.setDividerLocation(600);
	}

	public JComponent getJComponent() {
		return editor;
	}
}
