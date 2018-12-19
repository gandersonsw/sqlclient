/* Copyright (C) 2016 Graham Anderson gandersonsw@gmail.com - All Rights Reserved */
package com.graham.sqlclient.ui.sqltablebrowser;

import java.util.List;

import com.graham.appshell.data.AppDataAbstract;
import com.graham.appshell.data.AppDataClassAnn;
import com.graham.appshell.data.AppDataFieldAnn;

@AppDataClassAnn(
	classID = "DBMetaSchema"
)
public class DBMetaSchema extends AppDataAbstract {

	public static String NO_SCHMEA = "[No Schema]";

	@AppDataFieldAnn(
		uiLabel = "Name",
		primaryKeyFlag = true
	)
	public String name;
	
	@AppDataFieldAnn(
		uiLabel = "Table Count"
	)
	public int tableCount;
	
	@AppDataFieldAnn(
		uiLabel = "Table List"
	)
	public List<String> tableList;
	
	public DBMetaSchema() {
		super();
	}
	
	public DBMetaSchema(
			String nameParam, 
			int tableCountParam, 
			List<String> tableListParam) {
		super();
		if (nameParam == null) {
			throw new IllegalArgumentException("DBMetaSchema.name cannot be null");
		}
		name = nameParam;
		tableCount = tableCountParam;
		tableList = tableListParam;
	}
	
	public String getSchema1() {
		if (name.equals(NO_SCHMEA)) {
			return null;
		}
		return name;
	}

}
