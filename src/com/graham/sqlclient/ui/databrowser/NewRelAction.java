/* Copyright (C) 2016 Graham Anderson gandersonsw@gmail.com - All Rights Reserved */
package com.graham.sqlclient.ui.databrowser;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import com.graham.sqlclient.SqlClientApp;
import com.graham.appshell.data.DataUI;

public class NewRelAction extends AbstractAction {
	private static final long serialVersionUID = 1L;

	private DataBrowserDefinedTable dbt;
	private SqlClientApp app;
	
	public NewRelAction(DataBrowserDefinedTable dbtParam, SqlClientApp appParam) {
		super("Define New");
		dbt = dbtParam;
		app = appParam;
	}
	
	public void actionPerformed(ActionEvent e) {
		DataBrowserDefinedRelationship dbdr = new DataBrowserDefinedRelationship(app);
		dbdr.fromTable = dbt.tableName;
		DataUI dg = new DataUI(app.appsh, dbdr, app.acessableScopes);
		dg.initCreateNewUIWindow();
	}
}
