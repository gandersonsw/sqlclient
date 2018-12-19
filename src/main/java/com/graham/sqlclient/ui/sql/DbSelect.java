/* Copyright (C) 2016 Graham Anderson gandersonsw@gmail.com - All Rights Reserved */
package com.graham.sqlclient.ui.sql;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import com.graham.sqlclient.SqlClientApp;

public class DbSelect extends AbstractAction { // TODO delete this
	
	private static final long serialVersionUID = 1L;
	
	private SqlClientApp app;
	private SqlUIPanel sqlUi;

	public DbSelect(SqlClientApp appParam, SqlUIPanel sqlUiParam) {
		super("1/2 Databases");
		app = appParam;
		sqlUi = sqlUiParam;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}
}
