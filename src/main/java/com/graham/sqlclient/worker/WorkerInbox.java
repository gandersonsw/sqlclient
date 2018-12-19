/* Copyright (C) 2016 Graham Anderson gandersonsw@gmail.com - All Rights Reserved */
package com.graham.sqlclient.worker;

import java.util.List;

/**
 type for the value should be either "String", "Boolean", "List", "HashMap" 
 String -> 1 line text field
 Boolean -> checkbox
 List -> TextArea
 HashMap -> Drop Down Menu
 */
public interface WorkerInbox {

	List<String> getParamNameList();
	
	/**
	 * 
	 * @param name will never be null
	 * @param value will never be null
	 */
	void setParam(String name, Object value);
	
	/**
	 * this should never return null
	 * @param name
	 * @return
	 */
	Object getParam(String name);
	
}
