/* Copyright (C) 2016 Graham Anderson gandersonsw@gmail.com - All Rights Reserved */
package com.graham.appshell.data;

import java.io.File;

/**
 * Each AppData object will be stored in a certain scope.
 * subclasses should also implement toString for UI
 *
 * subclasses must implement equals and hashCode
 */
public interface AppDataScope {

	/**
	 * Where this scope is persisted in the file system.
	 */
	File getScopeDirectory();

	/**
	 * This must be unique for every scope.
	 */
	String getScopeKey();

}
