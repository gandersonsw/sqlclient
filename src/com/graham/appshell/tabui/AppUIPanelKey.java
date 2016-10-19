/* Copyright (C) 2016 Graham Anderson gandersonsw@gmail.com - All Rights Reserved */
package com.graham.appshell.tabui;

import java.io.Serializable;

public class AppUIPanelKey implements Serializable {
	private AppUIPanelType uiPanelType;
	private int id;

	public AppUIPanelKey(AppUIPanelMultipleType uiPanelTypeParam, int idPostfixParam) {
		if (uiPanelTypeParam == null) {
			throw new NullPointerException();
		}
		if (idPostfixParam <= 0) {
			throw new IllegalArgumentException("id must be 1 or more");
		}
		uiPanelType = uiPanelTypeParam;
		id = idPostfixParam;
	}

	public AppUIPanelKey(AppUIPanelSingletonType uiPanelTypeParam) {
		if (uiPanelTypeParam == null) {
			throw new NullPointerException();
		}
		uiPanelType = uiPanelTypeParam;
		id = -1;
	}

	public AppUIPanelType getUIPanelType() {
		return uiPanelType;
	}

	public int getUIPanelId() {
		return id;
	}

	public boolean equals(Object obj) {

		if (obj == null) {
			return false;
		}

		if (obj instanceof AppUIPanelKey) {
			return this.toString().equals(((AppUIPanelKey) obj).toString());
		}

		return false;
	}

	public int hashCode() {
		return this.toString().hashCode();
	}

	public String toString() {
		if (id <= 0) {
			return uiPanelType.toString();
		}
		return uiPanelType.toString() + id;
	}
}
