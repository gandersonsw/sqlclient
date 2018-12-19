/* Copyright (C) 2016 Graham Anderson gandersonsw@gmail.com - All Rights Reserved */
package com.graham.sqlclient.ui.databrowser;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import com.graham.sqlclient.SqlClientApp;
import com.graham.sqlclient.plugins.methodcall.JavaMethodCall;
import com.graham.sqlclient.plugins.methodcall.JavaMethodCallManager;
import com.graham.appshell.data.AppData;
import com.graham.appshell.data.AppDataAbstract;
import com.graham.appshell.data.AppDataClassAnn;
import com.graham.appshell.data.AppDataFieldAnn;

@AppDataClassAnn(
		classID = "BrowserDefinedRelationship"
	)
public class DataBrowserDefinedRelationship extends AppDataAbstract {

	private static String FN_FROM_TABLE = "fromTable";
	private static String FN_TO_TABLE = "toTable";
	private static String FN_FROM_COLUMN = "fromColumn";
	private static String FN_TO_COLUMN = "toColumn";
	private static String FN_ALLOW_ANY_TABLE = "allowAnyTable";
	
	@AppDataFieldAnn(
		uiLabel = "From Table",
		primaryKeyFlag = true
	)
	public String fromTable;
	
	@AppDataFieldAnn(
		uiLabel = "To Table"
	)
	public String toTable;
	
	@AppDataFieldAnn(
		uiLabel = "From Column"
	)
	public String fromColumn;
	
	@AppDataFieldAnn(
		uiLabel = "To Column"
	)
	public String toColumn;
	
	@AppDataFieldAnn(
		uiLabel = "Name",
		primaryKeyFlag = true
	)
	public String displayName;
	
	@AppDataFieldAnn(
		uiLabel = "Additional Condition"
	)
	public String otherSQL;
	
	@AppDataFieldAnn(
		uiLabel = "Any Table"
	)
	public boolean allowAnyTable; // if true, ignore fromTable, and use any table with the column name
	
	private SqlClientApp app;
	
	public DataBrowserDefinedRelationship() {
		super();
	}
	
	public DataBrowserDefinedRelationship(SqlClientApp appParam) {
		super();
		app = appParam;
	}
	
	public DataBrowserDefinedRelationship(
			String fromTableParam, 
			String toTableParam, 
			String fromColumnParam, 
			String toColumnParam, 
			String displayNameParam,
			String otherSQLParam,
			boolean allowAnyTableParam) {
		super();
		fromTable = fromTableParam;
		toTable = toTableParam;
		fromColumn = fromColumnParam;
		toColumn = toColumnParam;
		displayName = displayNameParam;
		allowAnyTable = allowAnyTableParam;
		otherSQL = otherSQLParam;
	}

	@Override
	public String getPrimaryKey() {
		return fromTable.toUpperCase() + ":" + displayName;
	}

	@Override
	public List<String> getUIAllowedValues(String fieldName) {
		if (fieldName.equals(FN_TO_TABLE)) {
			List<String> a = new ArrayList<String>();
			for (AppData d : app.appsh.getDataManagerList(DataBrowserDefinedTable.class).getList()) {
				a.add(((DataBrowserDefinedTable)d).getPrimaryKey());
			}
			a.add(JavaMethodCall.JavaMethodCallTableName);
			return a;
		}
		return null;
	}

	@Override
	public boolean isUIEditable(String fieldName) {
		if (fieldName.equals(FN_FROM_TABLE)) {
			return false;
		}
		return true;
	}

	@Override
	public String getUIToolTipText(String fieldName) {
		if (fieldName.equals(FN_ALLOW_ANY_TABLE)) {
			return "if checked, any table with the column name can use this relationship";
		} else if (fieldName.equals(FN_FROM_COLUMN)) {
			return "can be comma seperated list";
		}	else if (fieldName.equals(FN_TO_COLUMN)) {
			return "can be comma seperated list";
		}
		return null;
	}

	@Override
	public boolean getUITextTrimFlag(String fieldName) {
		return true;
	}

	@Override
	public String verifyAfterUIOK() {
		
		if (displayName.length() == 0) {
			return "display name is required";
		}

		Object result;
		if (toTable.equals(JavaMethodCall.JavaMethodCallTableName)) {
			if (!JavaMethodCallManager.getValidClassNames().contains(displayName)) {
				return "error: java class not found: " + displayName;
			}
		} else {
			try {
				result = app.runOneSelect("select * from " + toTable + " where rownum = 1", null);
			} catch (SQLException e1) {
				return "error: table not found: " + toTable;
			}
		}
		
		int fromColCount = 0;
		int toColCount = 0;
		StringTokenizer st = new StringTokenizer(toColumn, ",");
		while (st.hasMoreTokens()) {
			toColCount++;
			String toColumn = st.nextToken();
			if (toTable.equals(JavaMethodCall.JavaMethodCallTableName)) {
			} else {
				try {
					result = app.runOneSelect("select " + toColumn + " from " + toTable + " where rownum = 1", null);
				} catch (SQLException e1) {
					return "error: to column not found: " + toColumn;
				}
			}
		}
		
		st = new StringTokenizer(fromColumn, ",");
		while (st.hasMoreTokens()) {
			fromColCount++;
			String fromColumn = st.nextToken();
			try {
				result = app.runOneSelect("select " + fromColumn + " from " + fromTable + " where rownum = 1", null);
			} catch (SQLException e1) {
				return "error: from column not found: " + fromColumn;
			}
		}
		
		if (fromColCount != toColCount) {
			return "must be same number of columns in from and to table";
		}

		return null;
	}

	@Override
	public AppData newInstance() {
		return new DataBrowserDefinedRelationship(app);
	}

}
