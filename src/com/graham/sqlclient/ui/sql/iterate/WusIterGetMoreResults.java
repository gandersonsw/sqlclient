/* Copyright (C) 2016 Graham Anderson gandersonsw@gmail.com - All Rights Reserved */
package com.graham.sqlclient.ui.sql.iterate;

import java.sql.*;

/**
 * Created by grahamanderson on 9/18/15.
 */
public class WusIterGetMoreResults extends WusIterAbstract {

	private int rowCount;

	@Override
	public void start(WusIterContext context) {
		context.doGetMoreResults = false;
		context.moreRowsWaiting = false;
		rowCount = 0;
	}

	@Override
	public void beforeNext(WusIterContext context) throws SQLException {
		if (context.workUnit.isWorkUnitCancelled()) {
			rowCount = context.rowsPerSelect;
		}
		context.sqlData.addRow(context.curResultSet);
		if (context.workUnit.shouldUpdateOutput()) {
			context.workUnit.callbacks.dataChanged(context.sqlData.sqlDataArray, Boolean.FALSE);
		}
		rowCount++;
	}

	@Override
	public boolean next(WusIterContext context) throws SQLException {
		return rowCount < context.rowsPerSelect && context.curResultSet.next();
	}

	@Override
	public void end(WusIterContext context) throws SQLException {
		context.prevSql.hasMoreResults = context.curResultSet.next();
		if (context.prevSql.hasMoreResults) {
			context.moreRowsWaiting = true;
		}

		context.prevSql.resultCount += rowCount;
	}
}
