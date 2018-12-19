/* Copyright (C) 2016 Graham Anderson gandersonsw@gmail.com - All Rights Reserved */
package com.graham.tools;

public class DateTools {

	public static String formatTimeSpan(long millsec) {
		
		long mSec = (millsec % 1000) / 100;
		long rest = millsec / 1000;
		long sec = rest % 60;
		rest = rest / 60;
		String result;
		
		if (rest > 0) {
			long minute = rest % 60;
			result = StringTools.pad2DigitNumber((int)minute) + ":" + StringTools.pad2DigitNumber((int)sec) + "." + mSec;
			rest = rest / 60;
			if (rest > 0) {
				result = StringTools.pad2DigitNumber((int)rest) + ":" + result;
			}
		} else {
			result = sec + "." + mSec;
		}

		return result;
	}
	
}
