/* Copyright (C) 2016 Graham Anderson gandersonsw@gmail.com - All Rights Reserved */
package com.graham.sqlclient.ui.sql.action;

import com.graham.sqlclient.LapaeUIPanelMultipleType;
import com.graham.sqlclient.SqlClientApp;
import com.graham.sqlclient.ui.compare.CompareSrcDataFromSqlUI;
import com.graham.sqlclient.ui.compare.CompareUIPanel;
import com.graham.sqlclient.ui.sql.SQLTableModel;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Created by grahamanderson on 6/18/16.
 */
public class CompareDataRows extends AbstractAction {
	private static final long serialVersionUID = 1L;

	final private SqlClientApp app;
	final private String tableNameForCompare;
	final private SQLTableModel model;
	final private JTable table;

	public CompareDataRows(final SqlClientApp appParam, String tableNameParam, final SQLTableModel modelParam, final JTable tableParam) {
		super("Compare Selected");
		app = appParam;
		tableNameForCompare = tableNameParam;
		model = modelParam;
		table = tableParam;
	}

	public void actionPerformed(ActionEvent e) {
		//int colStart = 0;
		int rows[] = table.getSelectedRows();

		if (rows.length < 2) {
			return; // if less than 2, can't do anything
		}

		CompareSrcDataFromSqlUI data = new CompareSrcDataFromSqlUI(table, model);

		CompareUIPanel compareUI = (CompareUIPanel)app.createAppUIPanel(LapaeUIPanelMultipleType.compare);
		compareUI.setSrcData(data);
		app.appsh.getTabManager().addTab(compareUI);
		app.appsh.getTabManager().setSelectedTab(compareUI.getKey());
	}
}
