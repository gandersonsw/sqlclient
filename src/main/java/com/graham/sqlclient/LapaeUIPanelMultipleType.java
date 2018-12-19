/* Copyright (C) 2016 Graham Anderson gandersonsw@gmail.com - All Rights Reserved */
package com.graham.sqlclient;

import com.graham.appshell.tabui.*;

/**
 *
 * Created by grahamanderson on 9/13/15.
 */
public enum LapaeUIPanelMultipleType implements AppUIPanelMultipleType {

	// the are the not-singleton types
	sql("SQL"),
	dataBrowser("Browser"),
	worker("Worker"),
	sets("Set"),
	compare("Compare"),
	sqlPerformance("Sql Perf "),
	sqlCopy("Copy SQL");

	String baseLabel;

	LapaeUIPanelMultipleType(String valueParam) {
		this.baseLabel = valueParam;
	}

	public String getBaseLabel() {
		return baseLabel;
	}

	public String getCreateLabel() {
		return "New " + getBaseLabel();
	}
}

