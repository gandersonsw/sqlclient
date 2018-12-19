/* Copyright (C) 2016 Graham Anderson gandersonsw@gmail.com - All Rights Reserved */
package com.graham.sqlclient.ui.sql;

import com.graham.sqlclient.SqlClientApp;
import com.graham.sqlclient.WSDB;
import com.graham.sqlclient.WorkSpaceSettings;
import com.graham.tools.ConnPool;
import com.graham.tools.SimpleMatcher;

public class GroupDBIterator implements DBIterator {
	
	private String groupName;
	private int index = -1;
	private SqlClientApp app;
	private ConnPool cur;
	private SimpleMatcher matcher;
	
	public GroupDBIterator(SqlClientApp appParam, String groupNameParam) {
		groupName = groupNameParam;
		app = appParam;
		matcher = new SimpleMatcher(groupName);
	}

	@Override
	public boolean hasMore() {
		
		cur = null;
		
		// if empty, only use the default database
		if (groupName.length() == 0 && index == 0) {
			return false;
		}
		
		WorkSpaceSettings s = app.getWSS();
		while (index < s.dbList.size()) {
			index++;
			
			if (index == s.dbList.size()) {
				return false;
			}
			
			// if empty, only use the default database
			if (groupName.length() == 0 && index == 0) {
				return true;
			}

			if (matcher.matches(s.dbList.get(index).group)) {
			//if (groupName.equals(s.dbList.get(index).group)) {
				return true;
			}
		}

		return false;
	}

	@Override
	public ConnPool next() {
		WSDB db = app.getWSS().dbList.get(index);
		if (cur == null) {
			cur = new ConnPool(db.databaseUrl, db.databaseUserName, db.databasePassword);
		}
		return cur;
	}



	@Override
	public boolean isMultiple() {
		int savedIndex = index;
		index = -1;
		boolean flag = false;
		
		if (hasMore()) {
			if (hasMore()) {
				flag = true;
			}
		}
		
		index = savedIndex;

		return flag;
	}
	
	public String getGroupName() {
		return groupName;
	}
	
	

}
