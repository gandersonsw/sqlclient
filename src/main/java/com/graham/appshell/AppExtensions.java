/* Copyright (C) 2016 Graham Anderson gandersonsw@gmail.com - All Rights Reserved */
package com.graham.appshell;

import com.graham.appshell.tabui.*;

import java.util.*;

/**
 * The application UI main window will be tabbed.  A tab can be a singleton model,
 * or a multiple model.  Singlton has only one. Multiple may have many tabs with the same type
 */
public interface AppExtensions {

	AppUIPanel createAppUIPanel(AppUIPanelType panelType);
	
	AppUIPanelType getStartupUIPanelType();

	List<AppUIPanelType> getTypesForCreateMenu();
	
	/**
	 * this is called after app is set up
	 */
	void init(App app);
	
	AppExtensions newInstance();

	/**
	 * This is used as a way to store preferences for the aplication. Typically, just return the implementing class of this interface.
	 * See also: java.util.prefs.Preferences#userNodeForPackage
	 * @return
	 */
	Class getUserNodeClass();
}
