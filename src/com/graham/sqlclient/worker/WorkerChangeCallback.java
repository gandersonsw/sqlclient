/* Copyright (C) 2016 Graham Anderson gandersonsw@gmail.com - All Rights Reserved */
package com.graham.sqlclient.worker;

public interface WorkerChangeCallback {

	/**
	 * must be called for every new row
	 * @param index
	 */
	void oneNewRowAdded(int index);
	
	/**
	 * called if we are starting from begining
	 */
	void reset();
	
	/**
	 * called if we completed. Should also be called after error, if we are done
	 * don't call if "stop" was called
	 */
	void done();
	
	/**
	 * called if there will be a delay before next callback is made
	 */
	void flush();
	
	/**
	 * 
	 * @param notes both notes and e should not be null
	 * @param e
	 * @param fatal true if no additional processing will happen
	 */
	void error(String notes, Exception e, boolean fatal);
}
