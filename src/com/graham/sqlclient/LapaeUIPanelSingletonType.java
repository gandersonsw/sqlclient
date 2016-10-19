/* Copyright (C) 2016 Graham Anderson gandersonsw@gmail.com - All Rights Reserved */
package com.graham.sqlclient;

import com.graham.appshell.tabui.*;

/**
 * Created by grahamanderson on 9/15/15.
 */
public enum LapaeUIPanelSingletonType implements AppUIPanelSingletonType {

	simpleSettings("SQL Settings"),
	workUnits("Work"),
	tableBrowser("Table Browser"),
	setupHelp("Setup Guide");

	private String baseLabel;
	private AppUIPanelKey key;

	LapaeUIPanelSingletonType(String valueParam) {
		baseLabel = valueParam;
	}

	public String toString() {
		return baseLabel;
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
