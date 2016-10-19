/* Copyright (C) 2016 Graham Anderson gandersonsw@gmail.com - All Rights Reserved */
package com.graham.sqlclient.ui.sql;

import java.util.Date;
import java.util.List;

public class PreviousSQL {

	public String sql;
	public Date createdTime; // when the stmt was executed
	public long executionTime; // how long the execution took
	public int resultCount;
	public boolean hasMoreResults;
	public int updateCount;
	
	List<List<Object>> results; // we will keep the last 10 results, discard the rest
	public boolean wasUpdate; // true if it was an update insert / false if it was a select
	
}
