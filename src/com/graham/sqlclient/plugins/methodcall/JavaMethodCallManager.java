/* Copyright (C) 2016 Graham Anderson gandersonsw@gmail.com - All Rights Reserved */
package com.graham.sqlclient.plugins.methodcall;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.graham.sqlclient.SqlClientApp;

public class JavaMethodCallManager {
	
	public static List<String> getValidClassNames() {
		List<String> items = new ArrayList<String>();
		//items.add("test");
		return items;
	}

	public static List<List<Object>> execute(String name, Map<String, Object> params, SqlClientApp app) {
		if (name.equals("test")) {
			return null;
			//return new TestClass().test(params, app);
		} else {
			throw new IllegalArgumentException("unknown class:" + name);
		}
	}
	
}
