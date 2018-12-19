/* Copyright (C) 2016 Graham Anderson gandersonsw@gmail.com - All Rights Reserved */
package com.graham.appshell.tabui;

public abstract class AppUIPanelSingleton extends AppUIPanel {

	public AppUIPanelKey getKey() {
		return ((AppUIPanelSingletonType)getUIPanelType()).getKey();
	}

	@Override
	public String getTabLabel() {
		return this.getUIPanelType().getBaseLabel();
	}
	
}