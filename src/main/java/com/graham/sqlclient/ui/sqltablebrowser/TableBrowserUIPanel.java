/* Copyright (C) 2016 Graham Anderson gandersonsw@gmail.com - All Rights Reserved */
package com.graham.sqlclient.ui.sqltablebrowser;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.regex.PatternSyntaxException;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import com.graham.appshell.data.AppData;
import com.graham.appshell.tabui.*;
import com.graham.sqlclient.LapaeUIPanelSingletonType;
import com.graham.sqlclient.SqlClientApp;
import com.graham.tools.SearchContext;
import com.graham.tools.SimpleMatcher;

public class TableBrowserUIPanel extends AppUIPanelSingleton implements ListSelectionListener  {
	
	private SqlClientApp app;
	private JTable tableList;
	private JTabbedPane tableInfoPanel;
	private JTextField tableFilterField;
	private JTextField columnFilterField;
	private JComboBox schemaCombo;
	private JButton reloadButton;
	private String currentTableName;
	private String lastSelectedTable; // this is the table that was last selccted by the user.  If we are filtering, and we revertback to no selection, we will default to this if we can.
	private DefaultTableModel tm;
	private JSplitPane split;

	private SqlTableNameCellRender tableCellRender;
	private SqlTableNameCellRender colCellRender;

	private JPanel mainPanel;
	private schemasChangeListener scListener;
	
	public TableBrowserUIPanel(SqlClientApp appParam) {
		app = appParam;
		mainPanel = new JPanel(new BorderLayout());
		JPanel topBar = new JPanel();

		reloadButton = new JButton(new ReloadDBMetaData(app));
		reloadButton.setToolTipText("Build a local copy of the database schema and table locally.  This can take a long time.");
		topBar.add(reloadButton);
		
		topBar.add(new JLabel("Schema:"));
		schemaCombo = new JComboBox();
		setUpSchemaCombo();
		schemaCombo.addActionListener(new schemaComboListener());
		topBar.add(schemaCombo);
		
		topBar.add(new JLabel("Table Filter:"));
		tableFilterField = new JTextField("*");
		tableFilterField.setColumns(15);
		topBar.add(tableFilterField);
		tableFilterField.getDocument().addDocumentListener(new tableFilterChangeListener(true));
		
		topBar.add(new JLabel("Column Filter:"));
		columnFilterField = new JTextField("*");
		columnFilterField.setColumns(15);
		topBar.add(columnFilterField);
		columnFilterField.getDocument().addDocumentListener(new tableFilterChangeListener(false));
		
		tableList = new JTable();
		tableCellRender = new SqlTableNameCellRender();
		colCellRender = new SqlTableNameCellRender(); // used for the column names table
		tableList.setDefaultRenderer(Object.class, tableCellRender);
		reloadTableList();
		
		tableInfoPanel = new JTabbedPane();
		tableInfoPanel.add("Columns", new JLabel(":)"));
		tableInfoPanel.add("Indexes", new JLabel(":)"));

		JScrollPane tableListScroll = new JScrollPane(tableList);
		split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, tableListScroll, tableInfoPanel);

		mainPanel.add(topBar, BorderLayout.NORTH);

		mainPanel.add(split, BorderLayout.CENTER);

		scListener = new schemasChangeListener();
		app.appsh.getDataManagerList(DBMetaSchema.class).addDataChangeListener(scListener);
	}
	
	private void setUpSchemaCombo() {
		schemaCombo.removeAllItems();
		
		AppData data[] = app.appsh.getDataManagerList(DBMetaSchema.class).getSortedArray();
		ArrayList<DBMetaSchema> schemasWithTables = new ArrayList<DBMetaSchema>();
		ArrayList<DBMetaSchema> schemasNoTables = new ArrayList<DBMetaSchema>();
		for (AppData obj : data) {
			DBMetaSchema schema = (DBMetaSchema)obj;
			if (schema.tableCount == 0) {
				schemasNoTables.add(schema);
			} else {
				schemasWithTables.add(schema);
			}
		}
		
		for (DBMetaSchema schema : schemasWithTables) {
			schemaCombo.addItem(schema.name + ":" + schema.tableCount);
		}
		
		for (DBMetaSchema schema : schemasNoTables) {
			schemaCombo.addItem(schema.name + ":" + schema.tableCount);
		}
	}
	
	private DBMetaSchema getSelectedSchema() {
		String s = (String)schemaCombo.getSelectedItem();
		if (s == null) {
			return null;
		}
		s = s.substring(0, s.lastIndexOf(":"));
		return (DBMetaSchema)app.appsh.getDataManagerList(DBMetaSchema.class).getByPrimaryKey(s);
	}
	
	private DBMetaTable getSelectedTable() {
		String s = (String)schemaCombo.getSelectedItem();
		if (s == null) {
			return null;
		}
		s = s.substring(0, s.lastIndexOf(":"));
		if (currentTableName == null) {
			return null;
		}
		
		return (DBMetaTable)app.appsh.getDataManagerList(DBMetaTable.class).getByPrimaryKey(s + "." + currentTableName);
	}

	@Override
	public AppUIPanelType getUIPanelType() {
		return LapaeUIPanelSingletonType.tableBrowser;
	}
	
	@Override
	public boolean findText(SearchContext params) {
		return false; //findTextInTextArea(ta, text, startFromBegining, caseSensitive);
	}

	@Override
	public HashMap<String,Object> getQuitingObjectSave() {
		HashMap<String,Object> data = new HashMap<String,Object>();
		data.put("TableFilter", tableFilterField.getText());
		data.put("Schema", schemaCombo.getSelectedItem());
		return data;
	}

	@Override
	public void initStartingUp(HashMap<String,Object> quitingObjectSave) {
		if (quitingObjectSave == null) {
			return;
		}
		
		tableFilterField.setText((String)quitingObjectSave.get("TableFilter"));
		schemaCombo.setSelectedItem(quitingObjectSave.get("Schema"));
	}

	private void reloadTableList() {
		DBMetaSchema schema = getSelectedSchema();
		if (schema == null) {
			return;
		}
		
		if (schema.tableList == null) {
			System.out.println("this shouldn't happen");
			return;
		}
		
		// Filter by table name, if given
		SimpleMatcher matcher = new SimpleMatcher(tableFilterField.getText().trim());
		List<String> tableNames;
		List<String> filteredOutTableNames = new ArrayList<String>();
		if (matcher.getPassAll()) {
			tableNames = schema.tableList;
		} else {
			try {
				tableNames = new ArrayList<String>();
				for (String tableName : schema.tableList) {
					if (matcher.matches(tableName)) {
						tableNames.add(tableName);
					} else {
						filteredOutTableNames.add(tableName);
					}
				}
				tableFilterField.setForeground(Color.BLACK);
			} catch (PatternSyntaxException e) {
				tableFilterField.setForeground(Color.RED);
				tableNames = schema.tableList;
			}
		}

		// Filter by column name, if given
		matcher = new SimpleMatcher(columnFilterField.getText().trim());
		if (matcher.getPassAll()) {
			// no additional filtering
		} else {
			try {
				ArrayList<String> tableNames2 = new ArrayList<String>();
				for (String tableName : tableNames) {
					boolean notFoundOne = true;
					DBMetaTable table = (DBMetaTable)app.appsh.getDataManagerList(DBMetaTable.class).getByPrimaryKey(schema.name + "." + tableName);
					for (TableColInfo col : table.columns) {
						if (matcher.matches(col.name)) {
							tableNames2.add(tableName);
							notFoundOne = false;
							break;
						}
					}
					
					if (notFoundOne) {
						filteredOutTableNames.add(tableName);
					}
				}
				tableNames = tableNames2;
				columnFilterField.setForeground(Color.BLACK);
			} catch (PatternSyntaxException e) {
				columnFilterField.setForeground(Color.RED);
			}
		}
		
		tableCellRender.setStartFilterOutRow(tableNames.size());
		
		Object tables2[] = tableNames.toArray();
		Arrays.sort(tables2);
		
		Object tables2FilteredOut[] = filteredOutTableNames.toArray();
		Arrays.sort(tables2FilteredOut);
		
		Object tmData[][] = new Object[tables2.length + tables2FilteredOut.length][1];
		for (int i = 0; i < tables2.length; i++) {
			tmData[i][0] = tables2[i];
		}
		for (int i = 0; i < tables2FilteredOut.length; i++) {
			tmData[tables2.length + i][0] = tables2FilteredOut[i];
		}
		
		Object colNames[] = new Object[]{"Table Name"};

		tm = new DefaultTableModel(tmData, colNames);
		
		
		tableList.setModel(tm);
		tableList.getSelectionModel().addListSelectionListener(this);
		//tableList.setDefaultRenderer(Object.class, tableCellRender);
		
		if (lastSelectedTable != null) {
			valueChanged(null); // when the user has a table selected, then that table is not listed because of filtering, then it is listed again, we want that table to be re-selected
		}
	}
	
	private void reloadTableInfo(DBMetaTable table) {
		if (table == null) {
			tableInfoPanel.setComponentAt(0, new JLabel(":)"));
			return;
		}
		
		boolean hasRemarksFlag = false;
		boolean hasDeafultFlag = false;
		for (TableColInfo col : table.columns) {
			if (col.remarks != null) {
				hasRemarksFlag = true;
			}
			if (col.defaultVal != null) {
				hasDeafultFlag = true;
			}
		}
		
		List<TableColInfo> columns;
		List<TableColInfo> columnsFilteredOut = new ArrayList<TableColInfo>();;
		
		// Filter by column name, if given
		SimpleMatcher matcher = new SimpleMatcher(columnFilterField.getText().trim());
		if (matcher.getPassAll()) {
			columns = table.columns;
		} else {
			try {
				columns = new ArrayList<TableColInfo>();
				for (TableColInfo col : table.columns) {
					if (matcher.matches(col.name)) {
						columns.add(col);
					} else {
						columnsFilteredOut.add(col);
					}
				}
				columnFilterField.setForeground(Color.BLACK);
			} catch (PatternSyntaxException e) {
				columnFilterField.setForeground(Color.RED);
				columns = table.columns;
			}
		}
		
		Object cols2[] = columns.toArray();
		Arrays.sort(cols2, new TableColInfoComp());
		
		Object cols2FilteredOut[] = columnsFilteredOut.toArray();
		Arrays.sort(cols2FilteredOut, new TableColInfoComp());
		
		colCellRender.setStartFilterOutRow(cols2.length);
		
		int colCount = 4 + (hasRemarksFlag ? 1 : 0) + (hasDeafultFlag ? 1 : 0);
		Object tmData[][] = new Object[cols2.length + cols2FilteredOut.length][colCount];
		for (int i = 0; i < cols2.length; i++) {
			makeTableColRecord(hasRemarksFlag, hasDeafultFlag, cols2, tmData, i, i);
		}

		for (int i = 0; i < cols2FilteredOut.length; i++) {
			makeTableColRecord(hasRemarksFlag, hasDeafultFlag, cols2FilteredOut, tmData, i, i + cols2.length);
		}
		
		Object colNames[];
		if (hasRemarksFlag && hasDeafultFlag) {
			colNames = new Object[]{"Column Name", "Type", "Size", "Nullable", "Remarks", "Default"};
		} else if (hasRemarksFlag) {
			colNames = new Object[]{"Column Name", "Type", "Size", "Nullable", "Remarks"};
		} else if (hasDeafultFlag) {
			colNames = new Object[]{"Column Name", "Type", "Size", "Nullable", "Default"};
		} else {
			colNames = new Object[]{"Column Name", "Type", "Size", "Nullable"};
		}
	
		DefaultTableModel tm2 = new DefaultTableModel(tmData, colNames);
		
		JTable jtab = new JTable(tm2);
		jtab.setDefaultRenderer(Object.class, colCellRender);
		
		jtab.setCellSelectionEnabled(true);
		JScrollPane sp = new JScrollPane(jtab);
		
		tableInfoPanel.setComponentAt(0, sp);
	}

	private void makeTableColRecord(boolean hasRemarksFlag, boolean hasDeafultFlag, Object[] cols2, Object[][] tmData, int i, int i2) {
		tmData[i2][0] = ((TableColInfo)cols2[i]).name;
		tmData[i2][1] = ((TableColInfo)cols2[i]).type;
		tmData[i2][2] = ((TableColInfo)cols2[i]).size;
		tmData[i2][3] = ((TableColInfo)cols2[i]).isNullable;
		if (hasRemarksFlag) {
			tmData[i2][4] = ((TableColInfo)cols2[i]).remarks;
		}
		if (hasDeafultFlag) {
			tmData[i2][hasRemarksFlag ? 5 : 4] = ((TableColInfo)cols2[i]).defaultVal;
		}
	}

	private void reloadTableIndexInfo(DBMetaTable table) {
		if (table == null) {
			tableInfoPanel.setComponentAt(1, new JLabel(":)"));
			return;
		}
		
		Object cols2[] = table.indexes.toArray();
		int colCount = 2;
		Object tmData[][] = new Object[cols2.length][colCount];
		for (int i = 0; i < cols2.length; i++) {
			tmData[i][0] = ((TableIndexInfo)cols2[i]).INDEX_NAME;
			tmData[i][1] = ((TableIndexInfo)cols2[i]).COLUMN_NAME;
		}
		
		Object colNames[];//  = new Object[]{"Column Name", "Type", "Size", "Nullable", "Remarks", "Default"};
		colNames = new Object[]{"INDEX_NAME", "COLUMN_NAME"};
		
		DefaultTableModel tm2 = new DefaultTableModel(tmData, colNames);
		
		JTable jtab = new JTable(tm2);
		jtab.setCellSelectionEnabled(true);
		JScrollPane sp = new JScrollPane(jtab);
		
		tableInfoPanel.setComponentAt(1, sp);
	}
	
	private int getRowByTableName(String tableName) {
		int imax = tableList.getModel().getRowCount();
		for (int i = 0; i < imax; i++) {
			if (tableName.equals(tableList.getModel().getValueAt(i, 0))) {
				return i;
			}
		}
		return -1;
	}
	
	@Override
	public void valueChanged(ListSelectionEvent arg0) {
		
		// this doesn't work quite right TODO
	//	boolean userClicked = arg0 != null && arg0.getFirstIndex() > 0;
		
		if (tableList.getSelectedRow() == -1 && lastSelectedTable != null) {
			int r = getRowByTableName(lastSelectedTable);
			if (arg0 == null && tableCellRender.isRowFilteredOut(r)) {

			} else if (r != -1) {
				tableList.changeSelection(r, 0, false, false);
			}
		}

		if (arg0 == null && tableCellRender.isRowFilteredOut(tableList.getSelectedRow())) {
			tableList.changeSelection(0, 0, false, false);
		}

		if (tableList.getSelectedRow() == -1) {
			currentTableName = null;
		} else {
			Object obj = tm.getValueAt(tableList.getSelectedRow(), 0);
		
			String selectedTableName = obj.toString();

			if (selectedTableName.equals(currentTableName)) {
				return;
			}
			
			currentTableName = selectedTableName;
			
			if (arg0 == null && tableCellRender.isRowFilteredOut(tableList.getSelectedRow())) {

			} else {
			//	if (userClicked) {
					lastSelectedTable = currentTableName;
			//	}
			}
		}

		DBMetaTable table = getSelectedTable();
		reloadTableInfo(table);
		reloadTableIndexInfo(table);
	}

	public class TableColInfoComp implements Comparator {
		@Override
		public int compare(Object arg0, Object arg1) {
			return ((TableColInfo)arg0).name.compareTo(((TableColInfo)arg1).name);
		}
	}

	public class schemasChangeListener implements ChangeListener {
		@Override
		public void stateChanged(ChangeEvent arg0) {
			setUpSchemaCombo();
			reloadTableList();
		}
	}
	
	public class schemaComboListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			reloadTableList();
		}
	}
	
	class tableFilterChangeListener implements DocumentListener {
		boolean tableFilter;
		public tableFilterChangeListener(boolean tableFilterParam) {
			tableFilter = tableFilterParam;
		}
		
		@Override
		public void changedUpdate(DocumentEvent arg0) {
			reloadTableList();
			DBMetaTable table = getSelectedTable();
			reloadTableInfo(table);
		}

		@Override
		public void insertUpdate(DocumentEvent arg0) {
			reloadTableList();
			DBMetaTable table = getSelectedTable();
			reloadTableInfo(table);
		}

		@Override
		public void removeUpdate(DocumentEvent arg0) {
			reloadTableList();
			DBMetaTable table = getSelectedTable();
			reloadTableInfo(table);
		}
	}

	public JComponent getJComponent() {
		return mainPanel;
	}

	public List<Action> getActionsForMenu() {
		List<Action> a = new ArrayList<>();
		a.add(new ReloadDBMetaData(app));
		return a;
	}

	public boolean close() {
		app.appsh.getDataManagerList(DBMetaSchema.class).removeDataChangeListener(scListener);
		return true;
	}
}

