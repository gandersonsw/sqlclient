/* Copyright (C) 2016 Graham Anderson gandersonsw@gmail.com - All Rights Reserved */
package com.graham.sqlclient.ui.sqltablebrowser;

import java.io.Serializable;
import java.sql.Types;

public class TableColInfo implements Serializable {
	private static final long serialVersionUID = 2L;
	public String name;
	public String type;
	public String size;
	public String isNullable;
	public String remarks;
	public String defaultVal;
	public String indexInfo; // a short summary of the indexes on this table
	

	public static String getDataTypeName(int t) {
		switch (t) {
			case Types.ARRAY: return "Array";
			case Types.BIGINT : return "BigInt";
			case Types.BINARY : return "Binary";
			case Types.BIT : return "Bit";
			case Types.BLOB : return "BLOB";
			case Types.CHAR : return "Char";
			case Types.CLOB : return "CLOB";
			case Types.DATE : return "Date";
			case Types.DECIMAL : return "Decimal";
			case Types.DISTINCT : return "Disinct";
			case Types.FLOAT   : return "Float";
			case Types.INTEGER  : return "Integer";
			case Types.JAVA_OBJECT   : return "JAVA_OBJECT";
			case Types.LONGVARBINARY   : return "LONGVARBINARY";
			case Types.LONGVARCHAR   : return "LONGVARCHAR";
			case Types.NULL   : return "NULL";
			case Types.NUMERIC   : return "NUMERIC";
			case Types.OTHER    : return "OTHER";
			case Types.REF   : return "REF";
			case Types.SMALLINT    : return "SMALLINT";
			case Types.STRUCT    : return "STRUCT";
			case Types.TIME : return "TIME";
			case Types.TIMESTAMP  : return "TIMESTAMP ";
			case Types.TINYINT  : return "TINYINT ";
			case Types.VARBINARY  : return "VARBINARY ";
			case Types.VARCHAR  : return "VARCHAR ";
		}
		return String.valueOf(t);
	}
}
