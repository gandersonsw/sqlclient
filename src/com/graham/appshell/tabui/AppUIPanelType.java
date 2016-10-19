/* Copyright (C) 2016 Graham Anderson gandersonsw@gmail.com - All Rights Reserved */
package com.graham.appshell.tabui;

public interface AppUIPanelType {
	/**
	 * @return The base label that other label can be made from
	 */
	String getBaseLabel();

	/**
	 * @return The label when creating a new of this panel, or selecting it.
	 */
	String getCreateLabel();
}
