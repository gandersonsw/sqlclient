/* Copyright (C) 2016 Graham Anderson gandersonsw@gmail.com - All Rights Reserved */
package com.graham.appshell.handlers;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import com.graham.appshell.*;
import com.graham.appshell.tabui.*;

public class RemoveTabAction extends AbstractAction {

	private static final long serialVersionUID = 1L;
	
	private App app;
	private AppUIPanelSingletonType uiPanelType;

	public RemoveTabAction(App appParam, AppUIPanelSingletonType uiPanelTypeParam) {
		super("Cancel");
		app = appParam;
		uiPanelType = uiPanelTypeParam;
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		app.getTabManager().removeTab(uiPanelType.getKey());
	}

}
