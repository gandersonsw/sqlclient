/* Copyright (C) 2016 Graham Anderson gandersonsw@gmail.com - All Rights Reserved */
package com.graham.sqlclient.ui.databrowser;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.graham.tools.SimpleMatcher;

public class DBColumnFilterChangeListener implements DocumentListener {
	
	private DBContext context;

	public DBColumnFilterChangeListener(DBContext contextParam) {
		context = contextParam;
	}
	
	@Override
	public void changedUpdate(DocumentEvent arg0) {
		reloadColumnList();
	}

	@Override
	public void insertUpdate(DocumentEvent arg0) {
		reloadColumnList();
	}

	@Override
	public void removeUpdate(DocumentEvent arg0) {
		reloadColumnList();
	}
	
	private void reloadColumnList() {
		SimpleMatcher matcher = new SimpleMatcher(context.columnFilterField.getText().trim());
		context.jtableModel.setColumnNameMatcher(matcher);
	}
}
