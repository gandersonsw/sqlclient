/* Copyright (C) 2016 Graham Anderson gandersonsw@gmail.com - All Rights Reserved */
package com.graham.tools;

public class SimpleMatcher {
	
	private boolean caseSensitive = false;
	private String regex;
	boolean passAll = false;
	
	/**
	 * Create a not case sensitive matcher.
	 * 
	 * @param matchString
	 */
	public SimpleMatcher(String matchString) {
		setMatchString(matchString);
	}
	
	public SimpleMatcher(String matchString, boolean caseSensitiveParam) {
		caseSensitive = caseSensitiveParam;
		setMatchString(matchString);
	}
	
	private void setMatchString(String matchString) {
		if (matchString == null) {
			throw new NullPointerException();
		}
		if (matchString.equals("")) {
			matchString = "*";
		}
		
		int oldLength;
		do {
			oldLength = matchString.length();
			matchString = StringTools.replaceAll(matchString, SearchContext.createReplaceAllContext("**", "*"));
		} while (matchString.length() != oldLength);
		
		regex = StringTools.replaceAll(matchString, SearchContext.createReplaceAllContext("*", ".*"));
		if (!caseSensitive) {
			regex = regex.toLowerCase();
		}
		if (regex.equals(".*")) {
			passAll = true;
		}
	}
	
	/**
	 * 
	 * @return True if matches will always return true, for every String. Meaning, it is no nessecary to call it.
	 */
	public boolean getPassAll() {
		return passAll;
	}
	
	/**
	 * If passAll returns true, this will return true.
	 * 
	 * Otherwise, if txt parameter is null, this will return false.
	 * 
	 * Otherwise, a match will be tested.
	 * 
	 * @param txt
	 * @return 
	 */
	public boolean matches(String txt) {
		if (passAll) {
			return true;
		}
		if (txt == null) {
			return false;
		}
		
		if (!caseSensitive) {
			txt = txt.toLowerCase();
		}
		return txt.matches(regex);
	}
}
