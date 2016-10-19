/* Copyright (C) 2016 Graham Anderson gandersonsw@gmail.com - All Rights Reserved */
package com.graham.sqlclient.ui.sql;

public class WorkUnitSQLState {
	public boolean connClosed;
	public boolean commitPending;
	public boolean moreRowsWaiting;
	public PreviousSQL data;
	public WorkUnitSql cancelWorkUnitAndQueueNewWU;

	public WorkUnitSQLState(boolean connClosedParam, boolean commitPendingParam, boolean moreRowsWaitingParam, PreviousSQL dataParam, WorkUnitSql newWU) {
		connClosed = connClosedParam;
		commitPending = commitPendingParam;
		moreRowsWaiting = moreRowsWaitingParam;
		data = dataParam;
		cancelWorkUnitAndQueueNewWU = newWU;
	}
}
