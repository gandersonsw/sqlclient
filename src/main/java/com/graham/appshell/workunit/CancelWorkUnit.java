/* Copyright (C) 2016 Graham Anderson gandersonsw@gmail.com - All Rights Reserved */
package com.graham.appshell.workunit;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;


public class CancelWorkUnit extends AbstractAction {
	private static final long serialVersionUID = 1L;

	private WorkUnit wu;
	
	public void setWorkUnit(WorkUnit wuParam) {
		wu = wuParam;
	}

	public CancelWorkUnit() {
		super("Cancel");
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		wu.cancelWorkUnit();
		wu = null; // this is not nesseccary - I am just putting this here for now to find any miss uses of this
	}

}
