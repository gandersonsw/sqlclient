/* Copyright (C) 2016 Graham Anderson gandersonsw@gmail.com - All Rights Reserved */
package com.graham.appshell.handlers;

import javax.swing.AbstractAction;

public class SysWindow2 extends java.awt.event.WindowAdapter {
	AbstractAction ca;
	public SysWindow2(AbstractAction caParam) {
		super();
		ca = caParam;
	}
	public void windowClosing(java.awt.event.WindowEvent event) {
		ca.actionPerformed(null);
	}
}