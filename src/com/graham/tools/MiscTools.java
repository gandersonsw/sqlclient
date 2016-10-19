/* Copyright (C) 2016 Graham Anderson gandersonsw@gmail.com - All Rights Reserved */
package com.graham.tools;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MiscTools {
	
	public static <T> List<T> listOf1(T obj) {
		List<T> a = new ArrayList<T>(1);
		a.add(obj);
		return a;
	}

	/**
	 * Get object size of Maps, Sets, Lists, Strings, Double,
	 */
	public static int estimateObjectSize(Object obj) {

		if (obj == null) {
			return 0;
		} else if (obj instanceof String) {
			int len = ((String)obj).length();
			len = ((len * 2) + 45) / 8;
			return 8 * len;
		} else if (obj instanceof Map) {
			int totalSize = 16;
			for (Object k : ((Map)obj).keySet()) {
				Object v = ((Map)obj).get(k);
				totalSize += estimateObjectSize(k);
				totalSize += estimateObjectSize(v);
				totalSize += 32;
			}
			return totalSize;
		} else if (obj instanceof Integer) {
			return 4;
		} else if (obj instanceof Double) {
			return 8;
		} else if (obj instanceof List) {
			int totalSize = 12;
			for (Object o2 : (List) obj) {
				totalSize += estimateObjectSize(o2);
				totalSize += 4;
			}
			return totalSize;
		} else if (obj instanceof Object[]) {
			int totalSize = 12;
			for (Object o2 : (Object[]) obj) {
				totalSize += estimateObjectSize(o2);
				totalSize += 4;
			}
			return totalSize;
		} else {
			System.out.println("estimateObjectSize unknown object type:" + obj.getClass().getSimpleName());
		}
		return 16;
	}
	
}
