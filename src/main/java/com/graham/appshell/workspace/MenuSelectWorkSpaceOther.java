/* Copyright (C) 2016 Graham Anderson gandersonsw@gmail.com - All Rights Reserved */
package com.graham.appshell.workspace;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;

import com.graham.appshell.*;
import com.graham.appshell.data.AppScopeImpl;

public class MenuSelectWorkSpaceOther extends AbstractAction {

	private static final long serialVersionUID = 1L;
	
	private App app;
	private AppExtensions appExt;
	
	public MenuSelectWorkSpaceOther(App appParam, AppExtensions appExtParam) {
		super("Other...");
		app = appParam;
		appExt = appExtParam;
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		System.out.println("workspace changed:other");

		new WorkSpaceDirFrame(
				AppScopeImpl.getPrefsAppHomePath() + System.getProperty("file.separator") + "work1", 
				app, 
				appExt.newInstance());
	}
}