/* Copyright (C) 2016 Graham Anderson gandersonsw@gmail.com - All Rights Reserved */
package com.graham.appshell.workspace;

import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;

import com.graham.appshell.App;
import com.graham.appshell.AppExtensions;
import com.graham.appshell.handlers.ChainedHandler;
import com.graham.appshell.handlers.Handler;

public class CreateNewWorkspaceAndStartApp extends AbstractAction implements Handler {
	private static final long serialVersionUID = 1L;

	private App app;
	private WorkSpace ws;
	private AppExtensions app2;

	public CreateNewWorkspaceAndStartApp(App appParam, AppExtensions app2Param, WorkSpace wsParam) {
		super();
		app = appParam; // this can be null
		ws = wsParam;
		app2 = app2Param;
	}
	
	public void handleEvent(ActionEvent e, ChainedHandler cHandler) {
		actionPerformed(e);
		cHandler.next(e, Handler.RESULT_SUCCESS);
	}
	
	public void actionPerformed(ActionEvent arg0) {
		File f = ws.getScopeDirectory();
		f.mkdirs();
		if (app == null) {
			new App().init(app2, ws.path);
		} else {
			app.getActiveWorkspace().activeFlag = false;
			app.getDataManagerList(WorkSpace.class).changed(app.getActiveWorkspace());
			ws.activeFlag = true;
			if (app.getDataManagerList(WorkSpace.class).getByPrimaryKey(ws.getPrimaryKey()) == null) {
				ws.setScope(app.getAppScope());
				app.getDataManagerList(WorkSpace.class).add(ws);
			} else {
				app.getDataManagerList(WorkSpace.class).changed(ws);
			}
			
			app.getMainFrame().setVisible(false);
			new App().init(app2, null);
		}
	}
}
