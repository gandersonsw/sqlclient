/* Copyright (C) 2016 Graham Anderson gandersonsw@gmail.com - All Rights Reserved */
package com.graham.sqlclient.ui.databrowser;

import com.graham.sqlclient.SqlClientApp;
import com.graham.appshell.data.AppData;
import com.graham.appshell.data.DataManagerList;
import com.graham.tools.ConnPool;
import com.graham.tools.UICheckboxListDialog;
import com.graham.tools.UIProgressDialog;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by grahamanderson on 6/7/16.
 */
public class ImportRelationshipsFromDB {

	private SqlClientApp app;
	private ConnPool cp;
	private Connection conn;
	private DatabaseMetaData md;
	private UIProgressDialog progress;

	public ImportRelationshipsFromDB(SqlClientApp appParam, ConnPool cpParam) {
		app = appParam;
		cp = cpParam;
	}

	public void doStuff() {

		ResultSet srs = null;
		progress = new UIProgressDialog();
		String[] tasks = {"Find Tables","Find Foreign Keys"};
		progress.show(app.appsh.getMainFrame(), tasks);
		progress.update("Find Tables", 0);
		try {
			conn = cp.getConn();
			md = conn.getMetaData();

			srs = md.getSchemas();
			ArrayList<String> allSchemas = new ArrayList<>();
			while (srs.next()) {
				allSchemas.add(srs.getString(1).toUpperCase());
			}
			srs.close();
			srs = null;

			UICheckboxListDialog schemaChooser = new UICheckboxListDialog<String>(app.appsh.getMainFrame());

			List<String> pickedSchemas = schemaChooser.chooseItems(allSchemas, null, "Choose DB Schemas to import");
			if (pickedSchemas.size() > 0) {

				Map<String, List<String>> tablesBySchema = findTables(pickedSchemas);
				progress.update("Find Tables", 100);

				DataManagerList dm = app.appsh.getDataManagerList(DataBrowserDefinedRelationship.class);
				dm.addAll(new ArrayList<AppData>(), true);
				for (String schema : tablesBySchema.keySet()) {
					List<String> tableList = tablesBySchema.get(schema);
					for (int i = 0; i < tableList.size(); i++) { // String table : tableList) {

						progress.update("Find Foreign Keys", 100 * i / tableList.size());
						findKeys(schema, tableList.get(i));
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (srs != null) {
				try { srs.close(); } catch (SQLException sqle) { }
			}
			if (conn != null) {
				try { conn.close(); } catch (SQLException sqle) { }
			}
			progress.close();
		}
	}

	private Map<String, List<String>> findTables(List<String> schemas) throws SQLException {

		Map<String, List<String>> tables = new HashMap<>();

		List<AppData> list = new ArrayList<>();

		for (String schema : schemas) {
			List<String> stables = new ArrayList<>();
			tables.put(schema, stables);
			ResultSet  rs = null;
			try {
				rs = md.getTables(null, schema, null, null);
				while (rs.next()) {
					DataBrowserDefinedTable dbdt = new DataBrowserDefinedTable();
					dbdt.setScope(app.appsh.getActiveWorkspace());
					dbdt.tableName = rs.getString(3).toUpperCase();
					dbdt.idColumn = getTablePrimaryKey(schema, dbdt.tableName); // "guid"; // TODO
					dbdt.shortName = makeNameShort(rs.getString(3));
					list.add(dbdt);
					stables.add(dbdt.tableName);
				}
			} finally {
				if (rs != null) {
					try { rs.close(); } catch (SQLException sqle) { }
				}
			}
		}
		DataManagerList dm = app.appsh.getDataManagerList(DataBrowserDefinedTable.class);
		dm.addAll(list, true);
		return tables;
	}

	private String makeNameShort(String s) {
		//System.out.println("makeNameShort:" + s);
		int firstUnder = s.indexOf("_");

		if (s.length() < 4) {
			return s;
		} else if (s.length() < 6) {
			if (firstUnder < 2 && firstUnder != -1) {
				s = s.substring(firstUnder+1);
			}
			//System.out.println("makeNameShort2:" + s);
			return s;
		} else if (s.length() < 12) {
			if (firstUnder < 3 && firstUnder != -1) {
				s = s.substring(firstUnder+1);
			}
			//System.out.println("makeNameShort3:" + s.substring(0,1).toUpperCase() + s.substring(1).toLowerCase());
			return s.substring(0,1).toUpperCase() + s.substring(1).toLowerCase();
		} else {
			if (firstUnder < 3 && firstUnder != -1) {
				s = s.substring(firstUnder+1);
			}
			//System.out.println("makeNameShort4:" + s.substring(0,1).toUpperCase() + s.substring(1).toLowerCase());
			return s.substring(0,1).toUpperCase() + s.substring(1).toLowerCase();
		}
	}

	private String getTablePrimaryKey(String schema, String table) {
		ResultSet rs = null;
		String column = "";
		try {
			rs = md.getPrimaryKeys(null, schema, table);
			if (rs.next()) {
				column =  rs.getString(4);
				//System.out.println("getTablePrimaryKey:" + table + ":" + column);
				if (rs.next()) {
					//System.out.println("warning - more columns in index:" + rs.getString(4));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try { rs.close(); } catch (SQLException sqle) { }
			}
		}
		return column;
	}

	private void findKeys(String schema, String tableName) throws SQLException {

		ResultSet iKeys = null;
		try {
			List<AppData> list = new ArrayList<>();
			iKeys = md.getImportedKeys(null, schema, tableName);
			while (iKeys.next()) {
				DataBrowserDefinedRelationship dbdr = new DataBrowserDefinedRelationship();
				dbdr.setScope(app.appsh.getActiveWorkspace());
				dbdr.fromTable = tableName;
				dbdr.toTable = iKeys.getString(3);
				dbdr.fromColumn = iKeys.getString(8);
				dbdr.toColumn = iKeys.getString(4);
				dbdr.displayName = iKeys.getString(12);
				list.add(dbdr);
			}
			iKeys.close();

			DataManagerList dm = app.appsh.getDataManagerList(DataBrowserDefinedRelationship.class);
			dm.addAll(list, false);
		} finally {
			if (iKeys != null) {
				try { iKeys.close(); } catch (SQLException sqle) { }
			}
		}
	}

}
