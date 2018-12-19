/* Copyright (C) 2016 Graham Anderson gandersonsw@gmail.com - All Rights Reserved */
package com.graham.tools;

import java.util.HashMap;
import java.util.HashSet;

public class SqlInfo {
	public String sql;
	public HashMap<String, String> tableShortcuts = new HashMap<String, String>();
	public HashSet<String> tables = new HashSet<String>();
	public boolean hasWhere;
}
