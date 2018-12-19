/* Copyright (C) 2016 Graham Anderson gandersonsw@gmail.com - All Rights Reserved */
package com.graham.sqlclient.plugins.methodcall;

import java.util.Map;
import java.util.List;

import com.graham.sqlclient.SqlClientApp;

public interface JavaMethodCall {

	final public static String JavaMethodCallTableName = "JAVA_METHOD_CALL";

	List<List<Object>> execute(Map<String, Object> params, SqlClientApp app);
}
