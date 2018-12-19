/* Copyright (C) 2016 Graham Anderson gandersonsw@gmail.com - All Rights Reserved */
package com.graham.sqlclient.ui.databrowser;

/**
 * Created by grahamanderson on 10/16/16.
 */
public interface EditorContext {

	Object getEditingObject();

	String getColumnName();

	String getId();

	String getTableName();

	String getIdColumnName();

	String getDBGroupName();

	void setEditedValue(String val);
}
