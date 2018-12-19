/* Copyright (C) 2016 Graham Anderson gandersonsw@gmail.com - All Rights Reserved */
package com.graham.appshell.workunit;

import java.util.HashMap;

import javax.swing.*;

import com.graham.appshell.*;
import com.graham.appshell.tabui.*;
import com.graham.sqlclient.LapaeUIPanelSingletonType;

public class WorkUnitsUIPanel extends AppUIPanelSingleton {

	// TODO delete this class, or fix it

	private JPanel p;

	public WorkUnitsUIPanel(App app) {
		p = new JPanel();
		p.add(new JLabel("Work Units"));
	}

	@Override
	public AppUIPanelType getUIPanelType() {
		return LapaeUIPanelSingletonType.workUnits;
	}


	@Override
	public void initStartingUp(HashMap<String,Object> obj) {
		// TODO Auto-generated method stub
	}

	@Override
	public HashMap<String,Object> getQuitingObjectSave() {
		return new HashMap<String,Object>();
	}

	public JComponent getJComponent() {
		return p;
	}
}
