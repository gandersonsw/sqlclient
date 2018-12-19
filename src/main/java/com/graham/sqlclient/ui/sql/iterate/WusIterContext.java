/* Copyright (C) 2016 Graham Anderson gandersonsw@gmail.com - All Rights Reserved */
package com.graham.sqlclient.ui.sql.iterate;

import com.graham.sqlclient.ui.sql.*;
import com.graham.appshell.workunit.*;
import com.graham.sqlclient.ui.sql.PreviousSQL;
import com.graham.sqlclient.ui.sql.WorkUnitSql;
import com.graham.tools.*;

import java.sql.*;

/**
 * Created by grahamanderson on 9/18/15.
 */
public class WusIterContext {

	boolean hasMultipleConn;
	boolean isFirstConn;
	ConnPool curConnPool;
	boolean moreRowsWaiting;
	WorkUnitSql workUnit;
	PreviousSQL prevSql;
	boolean isFirstCsvRow;
	boolean hasCSV;
	SqlDataArray sqlData;
	boolean loadLOBs;
	WorkUnitCallback callbacks;
	String currentCSV;
	int rowsPerSelect;
	Connection conn;
	Statement stmt;
	ResultSet curResultSet;
	String sql;


	public String nextSql;
	public boolean canGetMoreNow;
	public boolean canCommitNow;
	public boolean doCommit;
	public boolean doGetMoreResults;
	public boolean doRollback;
	public WorkUnitSql cancelWorkUnitAndQueueNewWU;

	public WusIterContext(WorkUnitSql workUnitParam, String sqlParam, WorkUnitCallback callbacksParam) {
		workUnit = workUnitParam;
		sql = sqlParam;
		callbacks = callbacksParam;
	}

	public void setloadLOBs(boolean b) {
		loadLOBs = b;
	}

	public void setRowsPerSelect(int i) {
		rowsPerSelect = i;
	}

	public void cancelStatement() throws SQLException {
		stmt.cancel();
	}
}
