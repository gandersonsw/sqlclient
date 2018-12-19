/* Copyright (C) 2016 Graham Anderson gandersonsw@gmail.com - All Rights Reserved */
package com.graham.sqlclient.worker;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import com.graham.sqlclient.SqlClientApp;

public class RunWorkers extends AbstractAction {
	
	private static final long serialVersionUID = 1L;
	SqlClientApp app;
	
	public RunWorkers(SqlClientApp appParam) {
		super("Run");
		app = appParam;

	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		
	}

}
