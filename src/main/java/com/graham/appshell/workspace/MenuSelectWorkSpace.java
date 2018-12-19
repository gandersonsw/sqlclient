/* Copyright (C) 2016 Graham Anderson gandersonsw@gmail.com - All Rights Reserved */
package com.graham.appshell.workspace;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;

import com.graham.appshell.*;
import com.graham.appshell.handlers.AppQuit;
import com.graham.appshell.handlers.HandlerIfSuccess;

public class MenuSelectWorkSpace extends AbstractAction {
	private static final long serialVersionUID = 1L;

	private WorkSpace ws;
	private App app;
	private AppExtensions appExt;
	
	public MenuSelectWorkSpace(App appParam, WorkSpace wsParam, AppExtensions appExtParam) {
		super(wsParam.path);
		ws = wsParam;
		app = appParam;
		appExt = appExtParam;
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		System.out.println("workspace changed:" + ws.path);
		
		ActionEvent ae2 = new ActionEvent(arg0.getSource(), 0, AppQuit.NOQUIT);
		new HandlerIfSuccess(app.getAppQuitHandler(), new CreateNewWorkspaceAndStartApp(app, appExt.newInstance(), ws)).start(ae2);
	}
}