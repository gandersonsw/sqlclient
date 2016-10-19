/* Copyright (C) 2016 Graham Anderson gandersonsw@gmail.com - All Rights Reserved */
package com.graham.appshell;

import com.graham.appshell.tabui.*;

import java.io.Serializable;
import java.util.List;

public class AppRuntimeSavedData implements Serializable {
	private static final long serialVersionUID = 4L;
	int windowx;
	int windowy;
	int windowWidth;
	int windowHeight;
	AppUIPanelKey selectedUIPane;
	List<AppUIPanelSavedData> uiPanelSavedData;
}
