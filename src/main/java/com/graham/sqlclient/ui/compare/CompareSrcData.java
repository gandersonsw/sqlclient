/* Copyright (C) 2016 Graham Anderson gandersonsw@gmail.com - All Rights Reserved */
package com.graham.sqlclient.ui.compare;

public interface CompareSrcData {
	
	int getGroupCount();
	
	int getColumnCount();
	
	int getRowCount(int groupIndex);
	
	String getData(int groupIndex, int rowIndex, int columnIndex);
	
	String getColumnLabel(int columnIndex);

}
