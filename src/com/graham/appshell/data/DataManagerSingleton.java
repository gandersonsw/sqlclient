/* Copyright (C) 2016 Graham Anderson gandersonsw@gmail.com - All Rights Reserved */
package com.graham.appshell.data;


import com.graham.tools.MiscTools;

/**
 * Handles saving of singleton objects.
 */
public class DataManagerSingleton extends DataManager {

	private AppData data;

	public DataManagerSingleton(AppData templateItemParam, AppDataScope accessableScopesParam) {
		super(templateItemParam, MiscTools.listOf1(accessableScopesParam));
	}

	public void clearCachedData() {
		data = null;
		fireChanged(new ChangeEventData(null, ChangeEventData.EditType.DRELOAD));
	}

	public AppData get() {
		if (data == null) {
			data = readNamedObjectSingleton(getDataClassId() + ".obj");
			if (data == null) {
				data = templateItem.newInstance();
			}
		}
		return data;
	}
	
	public void save() {
		if (data != null) {
			writeNamedObjectSingleton(data, getDataClassId() + ".obj");
			fireChanged(new ChangeEventData(data, ChangeEventData.EditType.DEDIT));
		}
	}

}
