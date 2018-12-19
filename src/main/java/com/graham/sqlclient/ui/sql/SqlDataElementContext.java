/* Copyright (C) 2016 Graham Anderson gandersonsw@gmail.com - All Rights Reserved */
package com.graham.sqlclient.ui.sql;

import com.graham.tools.SqlInfo;

/**
 * Created by grahamanderson on 10/16/16.
 */
public class SqlDataElementContext {
	public String dbGroupName;
	//public String tableName;
	public String columnName;
	public String fieldValue;
	public SQLTableModel model;
	public int rowIndex;
	public int colIndex;
	public SqlInfo sqlInfo;

	public SqlDataElementContext(String dbGroupNameParam, String columnNameParam, String fieldValueParam, SQLTableModel modelParam, int rowIndexParam, int colIndexParam, SqlInfo sqlInfoParam) {
		dbGroupName = dbGroupNameParam;
	//	tableName = tableNameParam;
		columnName = columnNameParam;
		fieldValue = fieldValueParam;
		model = modelParam;
		rowIndex = rowIndexParam;
		colIndex = colIndexParam;
		sqlInfo = sqlInfoParam;
	}
}
