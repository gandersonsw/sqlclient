/* Copyright (C) 2016 Graham Anderson gandersonsw@gmail.com - All Rights Reserved */
package com.graham.sqlclient.ui.databrowser;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import com.graham.appshell.handlers.ShowAppUIPanel;
import com.graham.sqlclient.LapaeUIPanelMultipleType;
import com.graham.sqlclient.SqlClientApp;

public class OpenBrowserAction extends AbstractAction {
	private static final long serialVersionUID = 1L;
	
	private SqlClientApp app;
	private String dbGroupName;
	private DataBrowserDefinedTable dbdt;
	private DataBrowserDefinedRelationship dbdr;
	private String fieldValue;

	public OpenBrowserAction(SqlClientApp appParam, String dbGroupNameParam, DataBrowserDefinedTable dbdtParam, String fieldValueParam) {
		super("Open Browser for " + dbdtParam.tableName + "." + dbdtParam.idColumn + "=" + fieldValueParam);
		app = appParam;
		dbGroupName = dbGroupNameParam;
		dbdt = dbdtParam;
		fieldValue = fieldValueParam;
	}
	
	public OpenBrowserAction(SqlClientApp appParam, String dbGroupNameParam, DataBrowserDefinedRelationship dbdrParam, String fieldValueParam) {
		super("Open Browser for " + dbdrParam.toTable + "." + dbdrParam.toColumn + "=" + fieldValueParam);
		app = appParam;
		dbGroupName = dbGroupNameParam;
		dbdr = dbdrParam;
		fieldValue = fieldValueParam;
	}

	public void actionPerformed(ActionEvent e) {


		//ShowAppUIPanel sp = new ShowAppUIPanel(app.appsh, LapaeUIPanelMultipleType.dataBrowser, false);
		//sp.actionPerformed(null);

		DataBrowserUIPanel browser = app.getOrCreateDataBrowser();
		
		if (dbdt != null) {
			browser.doLookupFromExternal(dbGroupName, dbdt, fieldValue, null, null);
		}
		if (dbdr != null) {
			DataBrowserDefinedTable dbdt2 = (DataBrowserDefinedTable)app.appsh.getDataManagerList(DataBrowserDefinedTable.class).getByPrimaryKey(dbdr.toTable);
			browser.doLookupFromExternal(dbGroupName, dbdt2, fieldValue, null, null);
		}
	//	app.appsh.getTabManager().setSelectedTab(browser.getKey());
	}
}