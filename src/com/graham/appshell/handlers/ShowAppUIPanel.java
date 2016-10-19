/* Copyright (C) 2016 Graham Anderson gandersonsw@gmail.com - All Rights Reserved */
package com.graham.appshell.handlers;

import com.graham.appshell.*;
import com.graham.appshell.tabui.*;

import javax.swing.*;
import java.awt.event.*;
import java.util.*;

public class ShowAppUIPanel extends AbstractAction {

	private static final long serialVersionUID = 1L;
	private App app;
	private AppUIPanelType panelType;
	private boolean alwaysCreateNew;
	private AppUIPanel uiPanel;

	public ShowAppUIPanel(App appParam, AppUIPanelMultipleType panelTypeParam, boolean alwaysCreateNewParam) {
		super(panelTypeParam.getCreateLabel());
		app = appParam;
		panelType = panelTypeParam;
		alwaysCreateNew = alwaysCreateNewParam;
	}

	public ShowAppUIPanel(App appParam, AppUIPanelSingletonType panelTypeParam) {
		super(panelTypeParam.getCreateLabel());
		app = appParam;
		panelType = panelTypeParam;
		alwaysCreateNew = false;
	}

	public void actionPerformed(ActionEvent e) {
		List<AppUIPanel> p = app.getTabManager().getUIPanels(panelType);
		AppUIPanelKey key;
		if (alwaysCreateNew || p.size() == 0) {
			uiPanel = app.createAppUIPanel(panelType);
			if (uiPanel == null) {
				throw new NullPointerException("uiPanelTypeType=" + panelType);
			}
			app.getTabManager().addTab(uiPanel);
			key = uiPanel.getKey();
		} else {
			uiPanel = p.get(0);
			key = uiPanel.getKey();
		}
		app.getTabManager().setSelectedTab(key);
	}

	public AppUIPanel getPanelThatWasShown() {
		return uiPanel;
	}
}
