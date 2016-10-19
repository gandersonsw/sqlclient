/* Copyright (C) 2016 Graham Anderson gandersonsw@gmail.com - All Rights Reserved */
package com.graham.sqlclient.ui.sql.iterate;

import java.sql.*;

/**
 * Created by grahamanderson on 9/18/15.
 */
public class WusIterNewResultSet extends WusIterAbstract {

	private int rowCount;

	@Override
	public void start(WusIterContext context) throws SQLException {
		context.curResultSet = context.stmt.getResultSet();
		rowCount = 0;
		ResultSetMetaData md = context.curResultSet.getMetaData();
		if (context.isFirstConn && context.isFirstCsvRow) {
			context.sqlData.initFromMetaData(md);
		}
		if (context.hasMultipleConn) {
			context.sqlData.addHeaderRow("DB:" + context.curConnPool);
		}
		if (context.hasCSV) {
			context.sqlData.addHeaderRow("CSV:" + context.currentCSV);
		}
		context.callbacks.dataChanged(context.sqlData.sqlDataArray, Boolean.TRUE);
	}
	@Override
	public boolean next(WusIterContext context) throws SQLException {
		return rowCount < context.rowsPerSelect && context.curResultSet.next();
	}

	@Override
	public void afterNext(WusIterContext context) throws SQLException {
		if (context.workUnit.isWorkUnitCancelled()) {
			rowCount = context.rowsPerSelect;
		}
		context.sqlData.addRow(context.curResultSet);
		if (context.workUnit.shouldUpdateOutput()) {
			context.callbacks.dataChanged(context.sqlData.sqlDataArray, Boolean.FALSE);
		}
		rowCount++;
	}

	@Override
	public void end(WusIterContext context) throws SQLException {
		context.prevSql.hasMoreResults = context.curResultSet.next();
		if (context.prevSql.hasMoreResults) {
			context.moreRowsWaiting = true;
		}
		context.prevSql.resultCount = rowCount;
		context.prevSql.wasUpdate = false;
	}
}
