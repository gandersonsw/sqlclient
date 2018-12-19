/* Copyright (C) 2016 Graham Anderson gandersonsw@gmail.com - All Rights Reserved */
package com.graham.sqlclient.ui.databrowser;

/**
 * Created by grahamanderson on 10/16/16.
 */
public class EditorContextFromDataBrowser implements EditorContext {
	private DBContext dbContext;
	private int row;
	private int col;

	public EditorContextFromDataBrowser(DBContext dbContextParam, int rowParam, int colParam) {
		dbContext = dbContextParam;
		row = rowParam;
		col = colParam;
	}

	public Object getEditingObject() {
		return dbContext.jtableModel.getValueAtTyped(row, col);
	}

	public String getColumnName() {
		return dbContext.jtableModel.getSqlColumnName(row, col);
	}

	public String getId() {
		return dbContext.jtableModel.getSqlValue(dbContext.t.idColumn, dbContext.queryResultIndex).toString();
	}

	public String getTableName() {
		return dbContext.t.tableName;
	}

	public String getIdColumnName() {
		return dbContext.t.idColumn;
	}

	public String getDBGroupName() {
		return dbContext.dbGroupname;
	}

	public void setEditedValue(String val) {
		dbContext.jtableModel.setSqlValueAt(val, getColumnName());
	}
}
