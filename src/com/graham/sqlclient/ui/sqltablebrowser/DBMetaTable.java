/* Copyright (C) 2016 Graham Anderson gandersonsw@gmail.com - All Rights Reserved */
package com.graham.sqlclient.ui.sqltablebrowser;

import java.util.List;

import com.graham.appshell.data.AppDataAbstract;
import com.graham.appshell.data.AppDataClassAnn;
import com.graham.appshell.data.AppDataFieldAnn;

@AppDataClassAnn(
	classID = "DBMetaTable"
)
public class DBMetaTable extends AppDataAbstract {

	@AppDataFieldAnn(
		uiLabel = "Name",
		primaryKeyFlag = true
	)
	public String name;
	
	@AppDataFieldAnn(
		uiLabel = "Schema",
		primaryKeyFlag = true
	)
	public String schema;
	
	@AppDataFieldAnn(
		uiLabel = "Columns"
	)
	public List<TableColInfo> columns;
	
	@AppDataFieldAnn(
		uiLabel = "Indexes"
	)
	public List<TableIndexInfo> indexes;
	
	public DBMetaTable() {
		super();
	}
	
	public DBMetaTable(String schemaParam, String nameParam) {
		super();
		schema = schemaParam;
		name = nameParam;
	}

	public static String generatePrimaryKey(String schemaParam, String tablenameParam) {
		return schemaParam + "." + tablenameParam;
	}
	
	@Override
	public String getPrimaryKey() {
		return schema + "." + name;
	}

}
