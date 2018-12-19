/* Copyright (C) 2016 Graham Anderson gandersonsw@gmail.com - All Rights Reserved */
package com.graham.sqlclient.ui.sql;

import com.graham.tools.ConnPool;

public interface DBIterator {
	
	boolean hasMore();
	
	ConnPool next();
	
	boolean isMultiple();
	
}
