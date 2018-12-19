/* Copyright (C) 2016 Graham Anderson gandersonsw@gmail.com - All Rights Reserved */
package com.graham.sqlclient.ui.sql.iterate;

import com.graham.appshell.workunit.*;
import com.graham.sqlclient.ui.sql.DBIterator;
import com.graham.sqlclient.ui.sql.WorkUnitSQLState;

import java.sql.*;

/**
 * Created by grahamanderson on 9/18/15.
 *
 * This is the call hierarchy for the iterators:
 * WusDBIter
 *    WusIterRunFlag
 *       WusIterGetNewResults
 *          WusIterNewResultSet
 *       WusIterGetMoreResults
 */
public class WusDBIter extends WusIterAbstract {

	private DBIterator dbIter;
	private WusIterRunFlag wusIterRunFlag = new WusIterRunFlag();

	public WusDBIter(DBIterator dbIterParam) {
		dbIter = dbIterParam;
	}

	@Override
	public void start(WusIterContext context) {
		context.hasMultipleConn = dbIter.isMultiple();
		context.isFirstConn = true;
	}

	@Override
	public boolean next(WusIterContext context) {
		return dbIter.hasMore();
	}

	@Override
	public void afterNext(WusIterContext context) throws SQLException {
		context.curConnPool = dbIter.next();
//System.out.println("Current DB connection:" + context.curConnPool);

		wusIterRunFlag.run(context);

		context.isFirstConn = false;
	}

	public void end(WusIterContext context) {
		// this flag will be set if there were zero connections in DBIter
		if (context.isFirstConn) {
			context.workUnit.ending("no matching databases to connect to", WorkUnitCallback.WU_STATE.ERROR, new WorkUnitSQLState(true, false, false, context.prevSql, null));
		}
	}
}
