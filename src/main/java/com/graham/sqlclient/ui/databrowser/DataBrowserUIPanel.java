/* Copyright (C) 2016 Graham Anderson gandersonsw@gmail.com - All Rights Reserved */
package com.graham.sqlclient.ui.databrowser;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;

import com.graham.appshell.data.AppData;
import com.graham.appshell.data.DataUI;
import com.graham.appshell.data.DataUIList;
import com.graham.appshell.tabui.*;
import com.graham.sqlclient.LapaeUIPanelMultipleType;
import com.graham.sqlclient.SqlClientApp;
import com.graham.sqlclient.WSDB;
import com.graham.sqlclient.ui.sql.SqlDataElementContext;
import com.graham.tools.SqlTools;

public class DataBrowserUIPanel extends AppUIPanelMultiple implements ChangeListener {

	private SqlClientApp app;
	private JComboBox tableCombo;
	private JTextField idField;
	private List<Object> listenersToUnregister = new ArrayList<Object>();
	private JTabbedPane tabbed;
	private JTextField sqlText;
	private JPanel mainPanelBorderLayout;
	private String historyData[][];
	private Component placeHolder1;
	private JButton lookupButton;
	private JComboBox dbGroupField;

	private JPanel editor;
	
	public DataBrowserUIPanel(SqlClientApp appParam) {
		app = appParam;
		JPanel main = new JPanel(new GridLayout(6,3,5,5));
		
		main.add(new JLabel("DB:", SwingConstants.RIGHT));
		dbGroupField = new JComboBox();
		dbGroupField.setEditable(true);
		for (WSDB db : app.getWSS().dbList) {
			dbGroupField.addItem(db.group);
		}
		main.add(dbGroupField);
		main.add(new JLabel(""));

		
		main.add(new JLabel("Table:", SwingConstants.RIGHT));
		tableCombo = new JComboBox();
		addAllTablesToCombo();
		main.add(tableCombo);
		main.add(new JButton(new defineNewTableActionClass()));
		main.add(new JLabel("ID:", SwingConstants.RIGHT));
		idField = new JTextField();
		main.add(idField);
		main.add(new JLabel(""));
		main.add(new JLabel(""));
		lookupButton = new JButton(new lookUpActionClass());
		main.add(lookupButton);
		main.add(new JLabel(""));
		
		main.add(new JLabel(""));
		main.add(new JLabel(""));
		main.add(new JButton(new editTablesActionClass()));
		
		main.add(new JButton(new importRelActionClass()));
		main.add(new JLabel(""));
		main.add(new JButton(new editRelActionClass()));
		
		mainPanelBorderLayout = new JPanel(new BorderLayout());
		mainPanelBorderLayout.add(main, BorderLayout.NORTH);
		placeHolder1 = new JLabel("");
		mainPanelBorderLayout.add(placeHolder1, BorderLayout.CENTER);
		
		
		tabbed = new JTabbedPane();
		tabbed.add("Start", mainPanelBorderLayout);
		
		editor = new JPanel(new BorderLayout());
		editor.add(tabbed, BorderLayout.CENTER);
		sqlText = new JTextField();
		sqlText.setEditable(false);
		editor.add(sqlText, BorderLayout.SOUTH);
		
		app.appsh.getDataManager(DataBrowserDefinedTable.class).addDataChangeListener(this);
	}

	@Override
	public AppUIPanelType getUIPanelType() {
		return LapaeUIPanelMultipleType.dataBrowser;
	}
	
	public class defineNewTableActionClass extends AbstractAction {
		private static final long serialVersionUID = 1L;

		public defineNewTableActionClass() {
			super("Define New Table");
		}

		public void actionPerformed(ActionEvent e) {
			DataUI dg = new DataUI(app.appsh, new DataBrowserDefinedTable(app), app.acessableScopes);
			dg.initCreateNewUIWindow();
			dg.setMessage("enter valid table name and column...");
		}
	}

	public class lookUpActionClass extends AbstractAction {
		private static final long serialVersionUID = 1L;

		public lookUpActionClass() {
			super("Look up");
		}

		public void actionPerformed(ActionEvent e) {
			DataBrowserDefinedTable t = (DataBrowserDefinedTable)tableCombo.getSelectedItem();
			String id = idField.getText().trim();
			addOneToHistoryTable(getDBGroupName(), t.getPrimaryKey(), id);
			doLookUp2(getDBGroupName(), t, id, null, null);
		}
	}

	public static List<AbstractAction> getOpenAction(SqlClientApp appParam, SqlDataElementContext context) {
		List<AbstractAction> retValue = new ArrayList<AbstractAction>();
		if (context.fieldValue == null) {
			return retValue;
		}

		retValue.add(new OpenBrowserForSqlDataElementAction(appParam, context, true));
		retValue.add(new OpenBrowserForSqlDataElementAction(appParam, context, false));

		Iterator<String> tableNamesIter = context.sqlInfo.tables.iterator();
		while (tableNamesIter.hasNext()) {
			String tableName = tableNamesIter.next();

			DataBrowserDefinedTable dbdt = (DataBrowserDefinedTable)appParam.appsh.getDataManagerList(DataBrowserDefinedTable.class).getByPrimaryKey(tableName);
			//if (dbdt == null) {
			//	retValue.add(new DefineAndOpenBrowserAction(appParam, context.dbGroupName, tableName, context.columnName, context.fieldValue));
			//	return retValue;
			//}

			Collection<AppData> list = appParam.appsh.getDataManagerList(DataBrowserDefinedRelationship.class).getList();
			for (AppData d : list) {
				DataBrowserDefinedRelationship r = (DataBrowserDefinedRelationship) d;
				if (r.allowAnyTable) {
					if (r.toColumn.equalsIgnoreCase(context.columnName)) {
						retValue.add(new OpenBrowserAction(appParam, context.dbGroupName, r, context.fieldValue));
					}
				} else if (r.fromColumn.equals(context.columnName) && r.fromTable.equalsIgnoreCase(tableName)) {
					retValue.add(new OpenBrowserAction(appParam, context.dbGroupName, r, context.fieldValue));
				}
			}
		}

		return retValue;
	}

	/**
	 * @param dbGroupNameParam
	 * @param t
	 * @param id
	 * @param optionalColumnName Can be null. If not null, SQL will also check that column has the given value, or null
	 * @param optionalColumnValue
	 * @return true if the value was found in the database
	 */
	boolean doLookupFromExternal(String dbGroupNameParam, DataBrowserDefinedTable t, String id, String optionalColumnName, String optionalColumnValue) {
		setDBGroupName(dbGroupNameParam);
		tableCombo.setSelectedItem(t);
		idField.setText(id);
		addOneToHistoryTable(dbGroupNameParam, t.getPrimaryKey(), id);
		return doLookUp2(dbGroupNameParam, t, id, optionalColumnName, optionalColumnValue);
	}
	/*
	protected void doLookupFromExternal(String dbGroupNameParam, DataBrowserDefinedRelationship r, String id) {
		DataBrowserDefinedTable dbdt = (DataBrowserDefinedTable)app.appsh.getDataManagerList(DataBrowserDefinedTable.class).getByPrimaryKey(r.toTable);
		doLookupFromExternal(dbGroupNameParam, dbdt, id);
	}
	*/

	public static String createSqlPredicate(DataBrowserDefinedTable t, String id) {
		return t.idColumn + " = '" + id + "'";
	}

	public static String createSql(DataBrowserDefinedTable t, String id, String optionalColumnName, String optionalColumnValue) {
		StringBuilder sb = new StringBuilder();
		String sqlPredicate = createSqlPredicate(t, id);
		sb.append("select * from ").append(t).append(" where ").append(sqlPredicate);
		if (optionalColumnName != null) {
			if (optionalColumnValue == null || optionalColumnValue.length() == 0) {
				sb.append(" and ").append(optionalColumnName).append(" is null");
			} else {
				sb.append(" and ").append(optionalColumnName).append(" = '").append(SqlTools.makeTextSqlSafe(optionalColumnValue)).append("'");
			}
		}
		return sb.toString();
	}
	
	private boolean doLookUp2(String dbGroupNameParam, DataBrowserDefinedTable t, String id, String optionalColumnName, String optionalColumnValue) {
		DBContext context = new DBContext();
		context.app = app;
		String sqlPredicate = createSqlPredicate(t, id);
		context.sql = createSql(t, id, optionalColumnName, optionalColumnValue);
			/*"select * from " + t + " where " + sqlPredicate;
		if (optionalColumnName != null) {
			if (optionalColumnValue == null || optionalColumnValue.length() == 0) {
				context.sql += " and " + optionalColumnName + " is null";
			} else {
				context.sql += " and " + optionalColumnName + " = '" + SqlTools.makeTextSqlSafe(optionalColumnValue) + "'";
			}
		}
		*/
		context.tabPane = tabbed;
		context.t = t;
		context.sqlTextBox = sqlText;
		context.dbGroupname = getDBGroupName();
		
		return RelClickedAction.makeSubcontext1(context, t, context.sql, null, sqlPredicate);
		//RelClickedActionClass.makeSubcontext1(context, t, id, sqlPredicate);z
	}
	
	public class editTablesActionClass extends AbstractAction {
		private static final long serialVersionUID = 1L;

		public editTablesActionClass() {
			super("Edit Tables");
		}

		public void actionPerformed(ActionEvent e) {
			DataUIList gl = new DataUIList(app.appsh.getDataManagerList(DataBrowserDefinedTable.class), app.appsh, app.acessableScopes);
			gl.initEditListWithEditItemUIWindow();
		}
	}
	
	public class editRelActionClass extends AbstractAction {
		private static final long serialVersionUID = 1L;

		public editRelActionClass() {
			super("Edit Relationships");
		}

		public void actionPerformed(ActionEvent e) {
			DataUIList gl = new DataUIList(app.appsh.getDataManagerList(DataBrowserDefinedRelationship.class), app.appsh, app.acessableScopes);
			gl.initEditListWithEditItemUIWindow();
		}
	}

	public class importRelActionClass extends AbstractAction {
		private static final long serialVersionUID = 1L;
		public importRelActionClass() {
			super("Import Relationships");
		}
		public void actionPerformed(ActionEvent e) {
			ImportRelationshipsFromDB i = new ImportRelationshipsFromDB(app, app.getConnPool(getDBGroupName()));
			i.doStuff();

	//		DataUIList gl = new DataUIList(app.appsh.getDataManagerList(DataBrowserDefinedRelationship.class), app.appsh, app.acessableScopes);
	//		gl.initEditListWithEditItemUIWindow();
		}
	}
	
	/**
	 * called when the list of tables changes
	 */
	@Override
	public void stateChanged(ChangeEvent arg0) {
		addAllTablesToCombo();
	}
	
	private void addAllTablesToCombo() {
		AppData tList2[] = app.appsh.getDataManagerList(DataBrowserDefinedTable.class).getSortedArray();
		
		tableCombo.removeAllItems();
		for (int i = 0; i < tList2.length; i++) {
			tableCombo.addItem(tList2[i]);
		}
	}

	@Override
	public HashMap<String,Object> getQuitingObjectSave() {
		HashMap<String,Object> data = new HashMap<String,Object>();
		data.put("ID", idField.getText());
		data.put("Hist2", historyData);
		DataBrowserDefinedTable table = (DataBrowserDefinedTable)tableCombo.getSelectedItem();
		if (table != null) {
			data.put("Table", table.getPrimaryKey());
		}
		
		return data;
	}

	@Override
	public void initStartingUp(HashMap<String,Object> quitingObjectSave) {
		if (quitingObjectSave == null) {
			return;
		}
		
		idField.setText((String) quitingObjectSave.get("ID"));
		historyData = (String[][])quitingObjectSave.get("Hist2");
		DataBrowserDefinedTable table = (DataBrowserDefinedTable)app.appsh.getDataManagerList(DataBrowserDefinedTable.class).getByPrimaryKey((String)quitingObjectSave.get("Table"));
		tableCombo.setSelectedItem(table);
		
		initHistoryTable();
		
		if (quitingObjectSave.get("dbGroup") != null) {
			setDBGroupName((String)quitingObjectSave.get("dbGroup"));
		}
	}
	
	
	
	private void addOneToHistoryTable(String dbGroupNameParam, String tableName, String id) {
		if (historyData == null) {
			historyData = new String[1][4];
		} else {
			if (historyData.length > 0) {
				if (tableName.equals(historyData[0][0]) && id.equals(historyData[0][1]) && id.equals(historyData[0][3])) {
					return; // don't add it if its the same as the last one
				}
			}
			
			int newSize = historyData.length + 1;
			if (newSize > 100) {
				newSize = 100;
			}
			String newData[][] = new String[newSize][4];
			for (int i = 0; i < newSize-1; i++) {
				newData[i+1][0] = historyData[i][0];
				newData[i+1][1] = historyData[i][1];
				newData[i+1][2] = historyData[i][2];
				newData[i+1][3] = historyData[i][3];
			}
	
			historyData = newData;
		}
		
		historyData[0][0] = tableName;
		historyData[0][1] = id;
		historyData[0][2] = new Date().toString();
		historyData[0][3] = dbGroupNameParam;
		
		initHistoryTable();
	}

	private void initHistoryTable() {
		String colNames[] = new String[]{"Table", "ID", "Time Ran", "Connection Name"};
		
		if (historyData == null) {
			historyData = new String[1][4];
			historyData[0][0] = "";
			historyData[0][1] = "";
			historyData[0][2] = "";
			historyData[0][3] = "";
		}
	
		DefaultTableModel tm2 = new DefaultTableModel(historyData, colNames) {
			private static final long serialVersionUID = -1379871405807687147L;

			public boolean isCellEditable(int row, int column) {
			     return false;
			 }
		};

		JTable jtab = new JTable(tm2);
		jtab.setCellSelectionEnabled(true);

		jtab.addMouseListener(new TableMouseApapter2(jtab, tm2));
		
		JScrollPane sp = new JScrollPane(jtab);
		
		JPanel withLabel = new JPanel(new BorderLayout());
		withLabel.add(sp, BorderLayout.CENTER);
		withLabel.add(new JLabel(" History"), BorderLayout.NORTH);
		
		placeHolder1.invalidate();
		mainPanelBorderLayout.remove(placeHolder1);
		
		placeHolder1 = withLabel;
		
		mainPanelBorderLayout.add(withLabel, BorderLayout.CENTER);

		//jtab.validate();
		//sp.validate();
		mainPanelBorderLayout.validate();

	}
	
	class TableMouseApapter2 extends MouseAdapter {
		JTable table;
		DefaultTableModel tableModel;
		public TableMouseApapter2(JTable tableParam, DefaultTableModel tableModelParam) {
			table = tableParam;
			tableModel = tableModelParam;
		}
		
		public void mouseClicked(MouseEvent evt) {
			if (evt.getClickCount() == 2) {
				int row = table.getSelectedRow();
				String sqlTableName = (String)tableModel.getValueAt(row, 0);
				String id = (String)tableModel.getValueAt(row, 1);
				String db = (String)tableModel.getValueAt(row, 3);
				
				DataBrowserDefinedTable table = (DataBrowserDefinedTable)app.appsh.getDataManagerList(DataBrowserDefinedTable.class).getByPrimaryKey(sqlTableName);
				tableCombo.setSelectedItem(table);
				idField.setText(id);
				setDBGroupName(db);
				
				doLookUp2(db, table, id, null, null);
			}
		}
	}
	
	public String getDBGroupName() {
		return dbGroupField.getSelectedItem().toString();
	}
	
	private void setDBGroupName(String db) {
		boolean hasMatch = false;
		for (int i = 0; i < dbGroupField.getItemCount(); i++) {
			if (dbGroupField.getItemAt(i).toString().equals(db)) {
				hasMatch = true;
				dbGroupField.setSelectedIndex(i);
			}
		}
		
		if (!hasMatch) {
			dbGroupField.insertItemAt(db, 0);
			dbGroupField.setSelectedIndex(0);
		}
	}

	public JComponent getJComponent() {
		return editor;
	}

	public List<Action> getActionsForMenu() {
		List<Action> a = new ArrayList<>();
		a.add(new defineNewTableActionClass());
		a.add(new editTablesActionClass());
		a.add(new editRelActionClass());
		a.add(new importRelActionClass());
		return a;
	}

	public boolean close() {
		app.appsh.getDataManager(DataBrowserDefinedTable.class).removeDataChangeListener(this);
		return true;
	}
}
