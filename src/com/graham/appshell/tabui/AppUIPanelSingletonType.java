/* Copyright (C) 2016 Graham Anderson gandersonsw@gmail.com - All Rights Reserved */
package com.graham.appshell.tabui;

/**
 * A UIPanel that can have only 1 instance.  For example, a global settings panel.
 *
 * Created by grahamanderson on 9/15/15.
 */
public interface AppUIPanelSingletonType extends AppUIPanelType {
	AppUIPanelKey getKey();
}
