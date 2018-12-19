/* Copyright (C) 2016 Graham Anderson gandersonsw@gmail.com - All Rights Reserved */
package com.graham.tree.editorui;

import java.util.List;

import com.graham.appshell.data.AppData;

public interface TreeEditorCallback {
	
	 List<AppData> getChildTypes(AppData parentType);

}
