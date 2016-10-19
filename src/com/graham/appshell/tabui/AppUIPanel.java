/* Copyright (C) 2016 Graham Anderson gandersonsw@gmail.com - All Rights Reserved */
package com.graham.appshell.tabui;

import java.util.*;

import javax.swing.*;

import com.graham.tools.SearchContext;

/**
 * Panels that are in the main tabbed section will extend this. The UI should be built in the constructor.
 */
public abstract class AppUIPanel {

	/**
	 * The componet must be built only once. This may be called multiple times, and the same component must be returned each time.
	 * @return Get the component that hold all the UI. Usually you want to build this in the constrtcor, and just return it here.
	 */
	public abstract JComponent getJComponent();

	/**
	 * @return The label used for the tab in the UI
	 */
	public abstract String getTabLabel();
	
	/**
	 * 
	 * @param text
	 * @param params
	 * @return true if we found some text, return false if no additional results were found
	 */
	public boolean findText(SearchContext params) {
		System.out.println("this UI does not support searching");
		return false;
	}

	/**
	 * Called before quitting.  If this returns true, this UIPanel should not be persisted. If returns true, getQuitingObjectSave will not be called.
	 * @return
	 */
	public boolean throwAwayWhenQuiting() {
		return false;
	}
	
	/**
	 * Called before quitting
	 * @return null if quitting process should be canceled.  Otherwise, return an 
	 * object of stuff to save, that will be passed to "initStartingUp"
	 * If an exception is thrown, it will be ignored, and app will quit anyway.
	 */
	public abstract HashMap<String,Object> getQuitingObjectSave();
	
	/**
	 * called after starting up. This will be called if this is an existing UIPanel, or it is a new UIPanel.
	 * @param quitingObjectSave object that was serialized from "getQuitingObjectSave". This will be empty if this is a new UIPanel. This may be null.
	 */
	public abstract void initStartingUp(HashMap<String,Object> quitingObjectSave);

	public abstract AppUIPanelType getUIPanelType();

	public abstract AppUIPanelKey getKey();

	public List<Action> getActionsForMenu() {
		return null;
	}

	/**
	 * Called when the panel is closing. This in only called when the app is not quiting, but just this panel is being closed.
	 * @return False if the panel should not close, such as if some user input makes it so the panel should remain open.
	 */
	public boolean close() {
		return true;
	}

}
