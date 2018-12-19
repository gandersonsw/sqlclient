/* Copyright (C) 2016 Graham Anderson gandersonsw@gmail.com - All Rights Reserved */
package com.graham.sqlclient.worker;

import java.util.List;

public abstract class WorkerAbstractImpl implements Worker {

	/**
	 * replace <1> with item.get(0), and so on
	 * @param pattern
	 * @param item
	 * @return
	 */
	public static String compsePatternString(String pattern, List item) {
		
		int curLoc = 0;
		int nextLoc;
		StringBuilder sb = new StringBuilder();
		while ((nextLoc = pattern.indexOf('<', curLoc)) != -1) {
			int endNumber = nextLoc;
			while (pattern.length() > endNumber && Character.isDigit(pattern.charAt(endNumber))) {
				endNumber++;
			}
			if (pattern.length() > endNumber && pattern.charAt(endNumber+1) == '>' && endNumber > nextLoc) {
				sb.append(pattern.substring(curLoc, nextLoc));
				String ii = pattern.substring(nextLoc, endNumber);
				sb.append(item.get(Integer.parseInt(ii)));
				curLoc = endNumber + 1;
			}
		}
		
		if (curLoc > 0) {
			return sb.append(curLoc).toString();
		} else {
			return pattern;
		}
	}
	
}
