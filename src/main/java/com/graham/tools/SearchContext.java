/* Copyright (C) 2016 Graham Anderson gandersonsw@gmail.com - All Rights Reserved */
package com.graham.tools;

/**
 * Created by grahamanderson on 6/21/16.
 */
public class SearchContext {

	private String searchText;
	private boolean startFromBegining;
	private boolean caseSensitive = true;
	private String replaceText;
	private boolean replaceAll;
	private int replaceCount;

	static public SearchContext createReplaceAllContext(String searchTextParam, String replaceTextParam) {
		SearchContext sc = new SearchContext();
		sc.searchText = searchTextParam;
		sc.replaceText = replaceTextParam;
		sc.replaceAll = true;
		return sc;
	}

	static public SearchContext createSearchContext(String searchTextParam, boolean startFromBeginingParam) {
		SearchContext sc = new SearchContext();
		sc.searchText = searchTextParam;
		sc.startFromBegining = startFromBeginingParam;
		return sc;
	}

	public void setReplaceCount(int c) {
		replaceCount = c;
	}

	public String getSearchText() {
		return searchText;
	}

	public boolean isStartFromBegining() {
		return startFromBegining;
	}

	public boolean isCaseSensitive() {
		return caseSensitive;
	}

	public String getReplaceText() {
		return replaceText;
	}

	public boolean isReplaceAll() {
		return replaceAll;
	}

	public int getReplaceCount() {
		return replaceCount;
	}

}
