/* Copyright (C) 2016 Graham Anderson gandersonsw@gmail.com - All Rights Reserved */
package com.graham.appshell.tabui;

/**
 * Created by grahamanderson on 9/13/15.
 */
public enum AppUIPanelSingletonTypeImpl implements AppUIPanelSingletonType {
	AppSettings("App Settings"),
	AppAbout("About"),
	AppHelp("Help");

	private String baseLabel;
	private AppUIPanelKey key;

	AppUIPanelSingletonTypeImpl(String valueParam) {
		baseLabel = valueParam;
	}


	public AppUIPanelKey getKey() {
		if (key == null) {
			key = new AppUIPanelKey(this);
		}
		return key;
	}

	public String getBaseLabel() {
		return baseLabel;
	}

	public String getCreateLabel() {
		return getBaseLabel();
	}
}
