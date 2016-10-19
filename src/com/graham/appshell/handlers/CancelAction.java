/* Copyright (C) 2016 Graham Anderson gandersonsw@gmail.com - All Rights Reserved */
package com.graham.appshell.handlers;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JFrame;

public class CancelAction extends AbstractAction {
	private static final long serialVersionUID = 1L;
	private final JFrame f;

	public CancelAction(JFrame fParam) {
		super("Cancel");
		f = fParam;
	}

	public void actionPerformed(ActionEvent e) {
		f.setVisible(false);
	}
}
