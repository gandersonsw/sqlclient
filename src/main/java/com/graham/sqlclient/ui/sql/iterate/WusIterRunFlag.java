/* Copyright (C) 2016 Graham Anderson gandersonsw@gmail.com - All Rights Reserved */
package com.graham.sqlclient.ui.sql.iterate;

import com.graham.appshell.*;
import com.graham.appshell.tabui.*;
import com.graham.appshell.workunit.*;
import com.graham.sqlclient.ui.sql.PreviousSQL;
import com.graham.sqlclient.ui.sql.WorkUnitSQLState;
import com.graham.tools.*;

import java.sql.*;

/**
 * Created by grahamanderson on 9/18/15.
 */
public class WusIterRunFlag extends WusIterAbstract implements AppQuitListener {

	private WusIterGetNewResults newResults = new WusIterGetNewResults();
	private WusIterGetMoreResults moreResults = new WusIterGetMoreResults();

	private WusIterContext context_classObj;

	private boolean runFlag;
	private boolean keepConnAlive;
	private boolean pendingCommitWaiting;
	private boolean connClosed;

	@Override
	public void start(WusIterContext context) {
		context.hasCSV = newResults.hasCSV(context.sql);

		runFlag = true;
		connClosed = true;
		context.prevSql = new PreviousSQL();

		context_classObj = context;
	}

	@Override
	public boolean next(WusIterContext context) {
		return runFlag;
	}

	public void afterNext(WusIterContext context) {

		if (context.sqlData == null) {
			context.sqlData = new SqlDataArray();
		}
		context.sqlData.setLoadLOBs(context.loadLOBs);
		context.doCommit = false;
		context.doRollback = false;
		context.workUnit.starting("Running...", null);

		String errorString = null;
		context.moreRowsWaiting = false;
		keepConnAlive = pendingCommitWaiting;
		boolean keepRsAlive = false;

		try {
			if (context.doGetMoreResults) {
				moreResults.run(context);
			} else {
				if (context.conn == null) {
//System.out.println(">>>>>>>>>> opening connection:1");
					context.conn = context.curConnPool.getConn();
//System.out.println(">>>>>>>>>> opening connection:1.1");
					App.getCurrentApp().addAppQuitListener(this);
				}
				connClosed = false;
				newResults.run(context);
			}

			context.workUnit.resetCancel(); //cancelWorkUnitFlag = false;

			// if this last sql was an update, or if there was a previous update
			pendingCommitWaiting = context.prevSql.updateCount > 0 || pendingCommitWaiting;

			keepConnAlive = context.sqlData.getLazyDataTypeFound() || pendingCommitWaiting || context.moreRowsWaiting;
			if (context.hasMultipleConn) {
				// TODO this will disable lazyDataTypes and commit and moreRows, we may want to keep multipe connections open ????
				keepConnAlive = false;
				pendingCommitWaiting = false;
				context.moreRowsWaiting = false;
			}
			keepRsAlive = context.moreRowsWaiting;

		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
			errorString = e1.toString();
		} catch (SQLException e1) {
			if (e1.getErrorCode() != 1013) { // ORA-01013: user requested cancel of current operation
				System.out.println("errodeCode = " + e1.getErrorCode());
				e1.printStackTrace();
			}
			errorString = e1.toString();
		} finally {
			if (!keepRsAlive) {
				closeRS(context);
			}
			if (!keepConnAlive) {
				closeConn(context);
				connClosed = true;
			}
		}

		wrapItUp(context, errorString);
	}

	private void wrapItUp(WusIterContext context, String errorString) {

		String endString = null;
		context.callbacks.dataChanged(context.sqlData.sqlDataArray, Boolean.FALSE);
		if (errorString != null) {
			context.workUnit.ending(errorString, WorkUnitCallback.WU_STATE.ERROR, new WorkUnitSQLState(connClosed, pendingCommitWaiting, context.moreRowsWaiting, context.prevSql, context.cancelWorkUnitAndQueueNewWU));
		} else if (context.prevSql.wasUpdate) {
			endString = context.prevSql.updateCount + " Rows updated.  SQL Run time: " + DateTools.formatTimeSpan(context.prevSql.executionTime);
			context.workUnit.ending(endString,
				WorkUnitCallback.WU_STATE.SUCCESSFUL_FINISH,
				new WorkUnitSQLState(connClosed, pendingCommitWaiting, context.moreRowsWaiting, context.prevSql, context.cancelWorkUnitAndQueueNewWU));
		} else {
			endString =  context.prevSql.resultCount + (context.prevSql.hasMoreResults ? "+" : "") + " Rows.  SQL Run time: " + DateTools.formatTimeSpan(context.prevSql.executionTime);
			context.workUnit.ending(endString,
				WorkUnitCallback.WU_STATE.SUCCESSFUL_FINISH,
				new WorkUnitSQLState(connClosed, pendingCommitWaiting, context.moreRowsWaiting, context.prevSql, context.cancelWorkUnitAndQueueNewWU));
		}

		if (keepConnAlive) {
			try {
				context.canCommitNow = pendingCommitWaiting;
				context.canGetMoreNow = context.moreRowsWaiting;
				Thread.sleep(1000 * 60 * 2);  // keep connection for 2 minutes
				context.canGetMoreNow = context.moreRowsWaiting;
				context.canCommitNow = false;
			} catch (InterruptedException e) {
				//System.out.println("interupted exception:1");
			}

			if (context.doCommit) {
				try {
					endString = "commiting...";
					context.callbacks.setStatus(endString, WorkUnitCallback.WU_STATE.RUNNING, null);
					context.conn.commit();
					endString = "commit complete";
					context.callbacks.setStatus(endString, WorkUnitCallback.WU_STATE.RUNNING, null);
				} catch (SQLException e) {
					endString = "commit failed:" + e.getMessage();
					context.callbacks.setStatus(endString, WorkUnitCallback.WU_STATE.RUNNING, null);
					e.printStackTrace();
				}
			} else if (context.doRollback) {
				try {
					endString = "rolling back...";
					context.callbacks.setStatus(endString, WorkUnitCallback.WU_STATE.RUNNING, null);
					context.conn.rollback();
					endString = "rollback complete";
					context.callbacks.setStatus(endString, WorkUnitCallback.WU_STATE.RUNNING, null);
				} catch (SQLException e) {
					e.printStackTrace();
					endString = "rollback failed:" + e.getMessage();
					context.callbacks.setStatus(endString, WorkUnitCallback.WU_STATE.RUNNING, null);
				}
			}

			if (context.doGetMoreResults) {

			} else if (context.nextSql != null) {
				context.sql = context.nextSql;
				context.nextSql = null;
				closeRS(context);
			} else {
				runFlag = false;
				// closing connection - disable the commit and rollback buttons
				context.workUnit.ending(endString,
					WorkUnitCallback.WU_STATE.SUCCESSFUL_FINISH,
					new WorkUnitSQLState(true, false, false, context.prevSql, context.cancelWorkUnitAndQueueNewWU));
				closeRS(context);
				closeConn(context);
			}
		} else {
			runFlag = false;
		}
	}



	private void closeRS(WusIterContext context) {
		try {
			if (context.curResultSet != null) {
				context.curResultSet.close();
				context.curResultSet = null;
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		try {
			if (context.stmt != null) {
				context.stmt.close();
				context.stmt = null;
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
	}

	private void closeConn(WusIterContext context) {
		App.getCurrentApp().removeAppQuitListener(this);
		try {
			if (context.conn != null) {
//System.out.println(">>>>>>>>>> closing connection:1");
				context.curConnPool.releaseConn(context.conn);
				context.conn = null;
			} else {
//System.out.println(">>>>>>>>>> closing connection:2");
			}
			// connClosed = true;
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
	}


	@Override
	public boolean canQuit() {
		return true;
	}

	@Override
	public void quiting() {

		if (context_classObj.stmt != null) {
			try {
				context_classObj.stmt.cancel();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		closeRS(context_classObj);
		closeConn(context_classObj);
	}
}
