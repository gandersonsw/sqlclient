/* Copyright (C) 2016 Graham Anderson gandersonsw@gmail.com - All Rights Reserved */
package com.graham.appshell.handlers;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import com.graham.appshell.App;

public class CloseTabAction extends AbstractAction {
	private static final long serialVersionUID = 1L;
	private App app;

	public CloseTabAction(App appParam) {
		super("Close Tab");
		//putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_Q, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		app = appParam;
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		app.getTabManager().closeSelectedTab();
	}

}
