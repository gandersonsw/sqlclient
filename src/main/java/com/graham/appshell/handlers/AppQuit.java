/* Copyright (C) 2016 Graham Anderson gandersonsw@gmail.com - All Rights Reserved */
package com.graham.appshell.handlers;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.KeyStroke;

import com.graham.appshell.App;

public class AppQuit extends AbstractAction implements Handler {
	
	public static String NOQUIT = "noquit";

	private static final long serialVersionUID = 1L;
        
	private App app;
	
	public AppQuit(App appParam) {
		super("Quit");
		putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_Q, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		app = appParam;
	}
	
	/**
	 * this should only be called by HandlerNext, HandlerIfSuccess, HandlerIfFail or HandlerIfSuccessElse
	 */
	public void handleEvent(ActionEvent e, ChainedHandler cHandler) {
		actionPerformed(e);
		cHandler.next(e, Handler.RESULT_SUCCESS);
	}
       
	/**
	 * all ways to exit the application go through this
	 */
	public void actionPerformed(ActionEvent e) {
			
		// do saving stuff here
		if (app.saveForExit()) {
			if (!NOQUIT.equals(e.getActionCommand())) {
				System.exit(0);
			}
		}

	}
}
