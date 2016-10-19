/* Copyright (C) 2016 Graham Anderson gandersonsw@gmail.com - All Rights Reserved */
package com.graham.appshell.data;

public class ChangeEventData {
	// DNEW =  1 new item added
	// DEDIT = 1 item changed
	// DDELETE = 1 item deleted
	// DRELOAD = list completely changed
	public enum EditType { DNEW, DEDIT, DDELETE, DRELOAD }
	
	public AppData item; // null for DRELOAD type
	public EditType type;
	public String oldPrimaryKey; // only set for DEDIT type

	public ChangeEventData(AppData itemParam, EditType et) {
		item = itemParam;
		type = et;
	}
	public ChangeEventData(AppData itemParam, EditType et, String oldPrimaryKeyParam) {
		item = itemParam;
		type = et;
		oldPrimaryKey = oldPrimaryKeyParam;
	}
}
