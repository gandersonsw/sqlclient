/* Copyright (C) 2016 Graham Anderson gandersonsw@gmail.com - All Rights Reserved */
package com.graham.appshell.data;

import java.io.File;
import java.util.prefs.Preferences;

public class AppScopeImpl implements AppDataScope {

	private static Class userNodeClass = null;
	final public static String PREFS_HOME_PATH = "app_home_path";
	
	private File scopeFile;
	
	@Override
	public File getScopeDirectory() {
		if (scopeFile == null) {
			String p = getPrefsAppHomePath();
			if (p != null) {
				scopeFile = new File(p);
			}
		}
		return scopeFile;
	}

	@Override
	public String getScopeKey() {
		return "A";
	}
	
	public static String getPrefsAppHomePath() {
		if (userNodeClass == null) {
			throw new NullPointerException();
		}
		Preferences userPrefs = Preferences.userNodeForPackage(userNodeClass);
		return userPrefs.get(PREFS_HOME_PATH, null);
	}
	
	public static void setPrefsAppHomePath(String path) {
		if (userNodeClass == null) {
			throw new NullPointerException();
		}
		Preferences userPrefs = Preferences.userNodeForPackage(userNodeClass);
		userPrefs.put(PREFS_HOME_PATH, path);
	}
	
	@Override
	public String toString() {
		return "Application";
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		return true;
	}

	@Override
	public int hashCode() {
		return "Application".hashCode();
	}

	public static void setUserNodeClass(Class c) {
		if (c == null) {
			throw new NullPointerException();
		}
		userNodeClass = c;
	}
}
