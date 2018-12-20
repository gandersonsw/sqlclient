/* Copyright (C) 2016 Graham Anderson gandersonsw@gmail.com - All Rights Reserved */
package com.graham.sqlclient.ui.sqltablebrowser;

import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.List;

import javax.swing.*;

import com.graham.sqlclient.SqlClientApp;
import com.graham.tools.UICheckboxListDialog;
import com.graham.tools.UIProgressDialog;

public class ReloadDBMetaData extends AbstractAction {
	private static final long serialVersionUID = 1L;

	private SqlClientApp app;
	private UIProgressDialog progressUI;
	private int totalTables;

	public ReloadDBMetaData(SqlClientApp appParam) {
		super("Build Local DBMD");
		app = appParam;
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		loadFromDB();
	}

	private boolean initSchemas(final ResultSet srs, ArrayList<DBMetaSchema> schemas) throws SQLException {
		while (srs.next()) {
			DBMetaSchema s = new DBMetaSchema(srs.getString(1).toUpperCase(), 0, null);
			s.setScope(app.appsh.getActiveWorkspace());
			schemas.add(s);
			if (progressUI.update("Initialize Schemas", 1)) {
				return true;
			}
		}
		srs.close();
		return false;
	}

	private boolean findTables(ArrayList<DBMetaSchema> schemas, DatabaseMetaData md) throws SQLException {
		int counter = 0;

		for (DBMetaSchema schema : schemas) {
			counter++;

			ResultSet  rs = md.getTables(null, schema.name, null, null);
			while (rs.next()) {
				schema.tableCount++;
				if (schema.tableList == null) {
					schema.tableList = new ArrayList<String>();
				}
				schema.tableList.add(rs.getString(3).toUpperCase());
			}
			totalTables += schema.tableCount;
			rs.close();

			if (progressUI.update("Find Tables", 100 * counter / schemas.size())) {
				return true;
			}
		}
		return false;
	}

	DBMetaTable findColumnsAndIndexesForTable(DBMetaSchema schema, String tableName, DatabaseMetaData md, Connection conn) throws SQLException {
		DBMetaTable table = new DBMetaTable(schema.name, tableName);
		table.columns = new ArrayList<TableColInfo>();
		table.indexes = new ArrayList<TableIndexInfo>();



		ResultSet rs = md.getColumns(null, schema.name, tableName, null);
		while (rs.next()) {
			TableColInfo ci = new TableColInfo();
			ci.name = rs.getString(4).toUpperCase();
			ci.type = rs.getString(6);
			ci.size = rs.getString(7);
			ci.isNullable = rs.getString(11);
			ci.remarks  = rs.getString(12);
			table.columns.add(ci);
		}
		rs.close();

		try {
			List<String[]> arr = app.getConnPool().getDbClient().getTableIndexes(conn, (schema.name != null && schema.name.length() > 0 ? schema.name + "." : "") + tableName);
			for (String[] ind : arr) {
				TableIndexInfo ii = new TableIndexInfo();
				ii.INDEX_NAME = ind[0];
				ii.COLUMN_NAME = ind[1];
				table.indexes.add(ii);
			}
		} catch (SQLException sqle) {
			// ignore this error for now
			System.out.println("findColumnsAndIndexesForTable:" + schema.name + ":" + tableName + ":"+ sqle.getMessage());
		}

		table.setScope(app.appsh.getActiveWorkspace());
		return table;

	}

	/**
	 * load from database:
	 * schemas
	 *   tables
	 *      columns
	 *      indexes
	 */
	private void loadFromDB() {

		String tasks[] = {"Initialize Schemas", "Find Tables", "Initialize Tables"};
		progressUI = new UIProgressDialog();
		progressUI.show(app.appsh.getMainFrame(), tasks);

		Connection conn = null;
		try {
			conn = app.getConnPool().getConn();
			DatabaseMetaData md = conn.getMetaData();
			ArrayList<DBMetaSchema> schemas = new ArrayList<DBMetaSchema>();
			totalTables = 0;

			DBMetaSchema noSchema = new DBMetaSchema(DBMetaSchema.NO_SCHMEA, 0, null);
			noSchema.setScope(app.appsh.getActiveWorkspace());
			schemas.add(noSchema);
			if (initSchemas(md.getSchemas(), schemas)) {
				return;
			}
			if (initSchemas(md.getCatalogs(), schemas)) {
				return;
			}

			progressUI.update("Initialize Schemas", 100);

			schemas = chooseSchemasToLoad(schemas);
			if (schemas.size() == 0) {
				progressUI.close();
				return;
			}

			// find all tables for each schema
			if (findTables(schemas, md)) {
				return;
			}

			// find all columns and indexes for each table
			int counter = 0;
			ArrayList<DBMetaTable> tables = new ArrayList<DBMetaTable>();
			for (DBMetaSchema schema : schemas) {
				if (schema.tableList != null) {
					for (String tableName : schema.tableList) {
						counter++;
						tables.add(findColumnsAndIndexesForTable(schema, tableName, md, conn));

						if (progressUI.update("Initialize Tables", 100 * counter / totalTables)) {
							return;
						}
					}
				}
			}

			computeColumnIndexInfo(tables);

			app.appsh.getDataManagerList(DBMetaSchema.class).addAll(schemas, true);
			app.appsh.getDataManagerList(DBMetaTable.class).addAll(tables, true);

			progressUI.close();

		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		} finally {
			if (conn != null) {
				try {
					app.getConnPool().releaseConn(conn);
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void computeColumnIndexInfo(ArrayList<DBMetaTable> tables) {

		for (DBMetaTable t : tables) {
			Map<String,List<String>> colIndexes = new HashMap<String,List<String>>();
			Map<String,Integer> indexCounts = new HashMap<String,Integer>();
			for (TableIndexInfo i : t.indexes) {
				if (colIndexes.containsKey(i.COLUMN_NAME)) {
					colIndexes.get(i.COLUMN_NAME).add(i.INDEX_NAME);
				} else {
					List<String> l = new ArrayList<String>();
					l.add(i.INDEX_NAME);
					colIndexes.put(i.COLUMN_NAME, l);
				}

				if (indexCounts.containsKey(i.INDEX_NAME)) {
					indexCounts.put(i.INDEX_NAME,indexCounts.get(i.INDEX_NAME) + 1);
				} else {
					indexCounts.put(i.INDEX_NAME, 1);
				}
			}

			Map<String,String> indexDispName = new HashMap<String, String>();
			int dispNameCounter = 1;
			for (String indexName : indexCounts.keySet()) {
				int count = indexCounts.get(indexName);
				if (count == 1) {
					indexDispName.put(indexName, "*");
				} else {
					indexDispName.put(indexName, "*" + dispNameCounter);
					dispNameCounter++;
				}
			}

			for (TableColInfo tc : t.columns) {
				if (colIndexes.containsKey(tc.name)) {
					tc.indexInfo = "";
					for (String indexName : colIndexes.get(tc.name)) {
						tc.indexInfo += indexDispName.get(indexName) + " ";
					}
				}
			}
		}
	}

	private ArrayList<DBMetaSchema> chooseSchemasToLoad(ArrayList<DBMetaSchema> schemas) {

		boolean[] checkedFlags = new boolean[schemas.size()];
		for (int i = 0; i < schemas.size(); i++) {
			if (app.appsh.getDataManagerList(DBMetaSchema.class).getByPrimaryKey(schemas.get(i).name) != null) {
				checkedFlags[i] = true;
			}
		}

		UICheckboxListDialog<DBMetaSchema> dialog = new UICheckboxListDialog<>(progressUI.getDialog());
		return dialog.chooseItems(schemas, checkedFlags, "Choose schemas to import");
	}

}
