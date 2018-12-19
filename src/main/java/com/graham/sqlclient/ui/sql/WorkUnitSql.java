/* Copyright (C) 2016 Graham Anderson gandersonsw@gmail.com - All Rights Reserved */
package com.graham.sqlclient.ui.sql;

import java.sql.SQLException;

import com.graham.appshell.workunit.WorkUnit;
import com.graham.appshell.workunit.WorkUnitCallback;
import com.graham.sqlclient.ui.sql.iterate.WusDBIter;
import com.graham.sqlclient.ui.sql.iterate.WusIterContext;

public class WorkUnitSql extends WorkUnit /*implements AppQuitListener */{
	
	//final public static String PENDING_COMMIT = "pending-commit";

	private WusIterContext context;
	private DBIterator dbIter;

	public WorkUnitSql(DBIterator dbIterParam, String sqlParam, WorkUnitCallback callbacksParam) {
		dbIter = dbIterParam;
		callbacks = callbacksParam;
		context = new WusIterContext(this, sqlParam, callbacksParam);
	}
	
	public DBIterator getDbIter() {
		return dbIter;
	}
	
	public void setLoadLOBs(boolean loadLOBsParam) {
		//loadLOBs = loadLOBsParam;
		context.setloadLOBs(loadLOBsParam);
	}
	
	public void setRowsPerSelect(int rows) {
		//rowsPerSelect = rows;
		context.setRowsPerSelect(rows);
	}
	
	public void cancelWorkUnitInternal() {
		try {
			context.cancelStatement();
			this.thread.interrupt();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void cancelWorkUnitAndQueueNew(WorkUnitSql newWU) {
		context.cancelWorkUnitAndQueueNewWU = newWU;
		this.cancelWorkUnit();
	}

	@Override
	public void run() {
		WusDBIter wusIter = new WusDBIter(dbIter);
		try {
			wusIter.run(context);
		} catch (SQLException e) {
			e.printStackTrace(); // this shouldn't happen
		}
	}

	public void addWork(String sqlParam) {
		if (context.nextSql == null) {
			context.nextSql = sqlParam;
			this.thread.interrupt();
		} else {
			throw new IllegalArgumentException("already running a query");
		}
	}
	
	public void commit() {
		if (!context.canCommitNow) {
			throw new IllegalArgumentException();
		}
		context.doCommit = true;
		this.thread.interrupt();
	}
	
	public void rollback() {
		if (!context.canCommitNow) {
			throw new IllegalArgumentException();
		}
		context.doRollback = true;
		this.thread.interrupt();
	}
	
	public void getMoreResults() {
		if (!context.canGetMoreNow) {
			throw new IllegalArgumentException();
		}
		context.doGetMoreResults = true;
		this.thread.interrupt();
	}

}
