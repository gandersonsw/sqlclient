/* Copyright (C) 2016 Graham Anderson gandersonsw@gmail.com - All Rights Reserved */
package com.graham.appshell.tabui;

/**
 * A UIPanelMultiple type may have many instances of the same type.  (As opposed to the UIPanelSingleton which can only have 1 instance)
 */
public abstract class AppUIPanelMultiple extends AppUIPanel {

	private AppUIPanelKey key;

	public int getId() {
		return key.getUIPanelId();
	}

	public AppUIPanelKey getKey() {
		return key;
	}

	public void setKey(AppUIPanelKey keyParam) {
		if (key != null) {
			throw new IllegalArgumentException("key is already set. cannot be set again");
		}
		key = keyParam;
	}

	@Override
	public String getTabLabel() {
		return this.getUIPanelType().getBaseLabel() + " " + getId();
	}

}
