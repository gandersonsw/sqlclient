/* Copyright (C) 2016 Graham Anderson gandersonsw@gmail.com - All Rights Reserved */
package com.graham.sqlclient.ui.databrowser;

import java.sql.SQLException;
import java.util.Set;

import com.graham.sqlclient.SqlClientApp;
import com.graham.appshell.data.AppData;
import com.graham.appshell.data.AppDataAbstract;
import com.graham.appshell.data.AppDataClassAnn;
import com.graham.appshell.data.AppDataFieldAnn;

@AppDataClassAnn(
	classID = "BrowserDefinedTable"
)
public class DataBrowserDefinedTable extends AppDataAbstract {

	final private static String FN_SHORT_NAME = "shortName";

	@AppDataFieldAnn(
		uiLabel = "Table"
	)
	public String tableName;
	
	@AppDataFieldAnn(
		uiLabel = "ID Column"
	)
	public String idColumn;
	
	@AppDataFieldAnn(
		uiLabel = "Short Name"
	)
	public String shortName; // should be a 1 or 2 character string
	public Set<String> importantColumnNames;
	
	private SqlClientApp app;
	
	public DataBrowserDefinedTable() {
	}
	
	public DataBrowserDefinedTable(SqlClientApp appParam) {
		super();
		app = appParam;
	}
	
	public DataBrowserDefinedTable(String t, String c, String sn) {
		super();
		tableName = t;
		idColumn = c;
		shortName = sn;
	}

	@Override
	public String getPrimaryKey() {
		return tableName.toUpperCase();
	}

	@Override
	public boolean isUIEditable(String fieldName) {
		return true;
	}

	@Override
	public String getUIToolTipText(String fieldName) {
		if (fieldName.equals(FN_SHORT_NAME)) {
			return "a 1 or 2 charactor short abbreviation for this table";
		}
		return null;
	}

	@Override
	public boolean getUITextTrimFlag(String fieldName) {
		return true;
	}

	@Override
	public String verifyAfterUIOK() {

		if (shortName.length() == 0) {
			return "short name is required";
		}
		
		Object result;
		try {
			result = app.runOneSelect("select * from " + tableName + " where rownum = 1", null);
		} catch (SQLException e1) {
			return "error: table not found";
		}

		try {
			result = app.runOneSelect("select " + idColumn + " from " + tableName + " where rownum = 1", null);
		} catch (SQLException e1) {
			return "error: column not found";
		}
		
		return null;
	}

	@Override
	public AppData newInstance() {
		return new DataBrowserDefinedTable(app);
	}
	
}
