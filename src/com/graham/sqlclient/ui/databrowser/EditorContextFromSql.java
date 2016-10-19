/* Copyright (C) 2016 Graham Anderson gandersonsw@gmail.com - All Rights Reserved */
package com.graham.sqlclient.ui.databrowser;

import com.graham.sqlclient.ui.sql.SqlDataElementContext;

/**
 * Created by grahamanderson on 10/16/16.
 */
public class EditorContextFromSql implements EditorContext {

	private SqlDataElementContext context;
	private String id;
	private DataBrowserDefinedTable dbdt;

	public EditorContextFromSql(SqlDataElementContext contextParam, String idParam, DataBrowserDefinedTable dbdtParam) {
		context = contextParam;
		id = idParam;
		dbdt = dbdtParam;
	}

	public Object getEditingObject() {
		return context.fieldValue;
		//return dbContext.jtableModel.getValueAtTyped(row, col);
	}

	public String getColumnName() {
		return context.columnName;
	}

	public String getId() {
		return id;
	}

	public String getTableName() {
		return dbdt.tableName;
	}

	public String getIdColumnName() {
		return dbdt.idColumn;
	}

	public String getDBGroupName() {
		return context.dbGroupName;
	}

	public void setEditedValue(String val) {
		// TODO
		//context.model.setValueAt(val, context.rowIndex, context.colIndex);
	}
}
