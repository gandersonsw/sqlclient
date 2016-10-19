/* Copyright (C) 2016 Graham Anderson gandersonsw@gmail.com - All Rights Reserved */
package com.graham.sqlclient.ui.sql;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import com.graham.sqlclient.SqlClientApp;
import com.graham.sqlclient.ui.sqltablebrowser.DBMetaSchema;
import com.graham.sqlclient.ui.sqltablebrowser.DBMetaTable;
import com.graham.sqlclient.ui.sqltablebrowser.TableColInfo;
import com.graham.appshell.data.AppData;
import com.graham.tools.SqlInfo;
import com.graham.tools.UITools;

public class SQLAutoCompleteTableModel extends AbstractTableModel {
	private static final long serialVersionUID = 1L;
	
	private List<String> schemasToLookIn = null;
	
	private SqlClientApp app;
	private JTable table; // todo remove this??
	
	private List<String> objectNames;
	private List<String> objectTypes;
	private List<String> objectParents;
	private List<String> objectOtherInfo; // currently this is only used for table indexes
	
	private UITools.SubString currentTokenSave;
	
	private int rowCount;

	public SQLAutoCompleteTableModel(SqlClientApp appParam) {
		app = appParam;
	}
	
	public void initSchemasToLookIn() {
		if (schemasToLookIn == null) {
			schemasToLookIn = new ArrayList<String>();
			Collection<AppData> data = app.appsh.getDataManagerList(DBMetaSchema.class).getList();
			for (AppData d1 : data) {
				DBMetaSchema s = (DBMetaSchema)d1;
				if (s.tableCount > 0) {
					schemasToLookIn.add(s.getPrimaryKey());
				}
			}
		}
	}
	
	public UITools.SubString getCurrentTokenToReplace() {
		return currentTokenSave;
	}

	public void insertTokenForTab(UITools.SubString currentToken) {
		currentTokenSave = currentToken;
	}

	/**
	 * return true is the results should be shown
	 */
	public boolean dataChanged(SqlInfo info, UITools.SubString currentToken) {
		currentTokenSave = currentToken;
		
		objectNames = new ArrayList<String>();
		objectTypes = new ArrayList<String>();
		objectParents = new ArrayList<String>();
		objectOtherInfo = new ArrayList<String>();
		
		initSchemasToLookIn();
		
		currentToken.s = currentToken.s.toUpperCase();
		int dot = currentToken.s.indexOf('.');
		
		if (currentToken.s.trim().equals("")) {
			
			for (String schemaName : schemasToLookIn) {
				for (String tableName : info.tables) {
					DBMetaTable table = (DBMetaTable)app.appsh.getDataManagerList(DBMetaTable.class).getByPrimaryKey(DBMetaTable.generatePrimaryKey(schemaName,tableName));
					if (table != null) {
						for (TableColInfo col : table.columns) {
							objectNames.add(col.name);
							objectTypes.add("COLUMN");
							objectParents.add(tableName);
							objectOtherInfo.add(col.indexInfo);
						}
					}
				}
			}
			
		} else if (dot == -1) {
			for (String schemaName : schemasToLookIn) {
				
				for (String tableName : info.tables) {
					DBMetaTable table = (DBMetaTable)app.appsh.getDataManagerList(DBMetaTable.class).getByPrimaryKey(DBMetaTable.generatePrimaryKey(schemaName,tableName));
					if (table != null) {
						for (TableColInfo col : table.columns) {
							if (col.name.startsWith(currentToken.s)) {
								objectNames.add(col.name);
								objectTypes.add("COLUMN");
								objectParents.add(tableName);
								objectOtherInfo.add(col.indexInfo);
							}
						}
					}
				}
			}
			
			for (String schemaName : schemasToLookIn) {
				DBMetaSchema schema = (DBMetaSchema)app.appsh.getDataManagerList(DBMetaSchema.class).getByPrimaryKey(schemaName);
				for (String tname : schema.tableList) {
					if (tname.startsWith(currentToken.s)) {
						objectNames.add(tname);
						objectTypes.add("TABLE");
						objectParents.add(schemaName);
						objectOtherInfo.add("");
					}
				}
			}
			
			// also - check all tables we have in sql for columns
		} else {
			String currentTokenPreDot = currentToken.s.substring(0, dot);
			String currentTokenPostDot = currentToken.s.substring(dot + 1);
			
			currentTokenSave.s = currentTokenPostDot;
			currentTokenSave.startIndex += dot + 1; // NEW !!!
			
			String tableName = info.tableShortcuts.get(currentTokenPreDot);

			for (String schemaName : schemasToLookIn) {
				DBMetaTable table = (DBMetaTable)app.appsh.getDataManagerList(DBMetaTable.class).getByPrimaryKey(DBMetaTable.generatePrimaryKey(schemaName,tableName));
				if (table != null) {
					for (TableColInfo col : table.columns) {
						if (col.name.startsWith(currentTokenPostDot)) {
							objectNames.add(col.name);
							objectTypes.add("COLUMN");
							objectParents.add(tableName);
							objectOtherInfo.add(col.indexInfo);
						}
					}
				}
			}
			
		}
		rowCount = objectNames.size();

		fireTableStructureChanged();
		
		for (int colIndex = 0; colIndex < getColumnCount(); colIndex++) {
			table.getColumnModel().getColumn(colIndex).setPreferredWidth(220);
		}
		
		return rowCount > 0;
	}

	@Override
	public Class<?> getColumnClass(int arg0) {
		return String.class;
	}

	@Override
	public int getColumnCount() {
		return 4;
	}

	@Override
	public String getColumnName(int arg0) {
		if (arg0 == 0) {
			return "Name";
		} else if (arg0 == 1) {
			return "Type";
		} else if (arg0 == 2) {
			return "Location";
		} else if (arg0 == 3) {
			return "Table Index";
		} else {
			return "question";
		}
	}

	@Override
	public int getRowCount() {
		return rowCount;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		if (rowIndex > objectOtherInfo.size()) {
			return ""; // this can happen when the other thread is still loading
		}
		if (columnIndex == 0) {
			return objectNames.get(rowIndex);
		} else if (columnIndex == 1) {
			return objectTypes.get(rowIndex);
		} else if (columnIndex == 2) {
			return objectParents.get(rowIndex);
		} else if (columnIndex == 3) {
			return objectOtherInfo.get(rowIndex);
		} else {
			return "q";
		}
	}
	
	public void setTable1(JTable t) {
		table = t;
	}

}
