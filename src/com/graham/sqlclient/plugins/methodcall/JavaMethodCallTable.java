/* Copyright (C) 2016 Graham Anderson gandersonsw@gmail.com - All Rights Reserved */
package com.graham.sqlclient.plugins.methodcall;

import com.graham.sqlclient.ui.databrowser.DataBrowserDefinedTable;

public class JavaMethodCallTable extends DataBrowserDefinedTable {
	static JavaMethodCallTable instance1 = new JavaMethodCallTable();
	
	public JavaMethodCallTable() {
		this.idColumn = "ID";
		this.shortName = "JAVA";
		this.tableName = "JAVA_METHOD_CALL";
	}

	public static JavaMethodCallTable getInstance() {
		return instance1;
	}
}
