/* Copyright (C) 2016 Graham Anderson gandersonsw@gmail.com - All Rights Reserved */
package com.graham.sqlclient.ui.sqltablebrowser;

import java.io.Serializable;

public class TableIndexInfo implements Serializable {
	private static final long serialVersionUID = 1L;
	//String NON_UNIQUE;
	//String INDEX_QUALIFIER;
	public String INDEX_NAME;
	//String TYPE;
	//String ORDINAL_POSITION;
	public String COLUMN_NAME;
	//String ASC_OR_DESC;
	//String CARDINALITY;
}
