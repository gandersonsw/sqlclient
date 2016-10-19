/* Copyright (C) 2016 Graham Anderson gandersonsw@gmail.com - All Rights Reserved */
package com.graham.sqlclient.ui.sql;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.graham.sqlclient.SqlClientApp;
import com.graham.appshell.data.AppData;
import com.graham.appshell.data.DataManagerList;
import com.graham.appshell.workunit.WorkUnitCallback;

public class SqlRunCallback extends WorkUnitCallback {
	
	private SqlUIPanel sqlUi;
	private SqlTopButtonManager buttonManager;
	private SqlClientApp app;
	
	public SqlRunCallback(SqlClientApp appParam, SqlUIPanel sqlUiParam, SqlTopButtonManager buttonManagerParam) {
		app = appParam;
		sqlUi = sqlUiParam;
		buttonManager = buttonManagerParam;
	}

	@Override
	public void setStatus(String message, WorkUnitCallback.WU_STATE state, Object param1) {
		sqlUi.setFooterText(message, false);
	}
	
	@Override
	public void setFinishStatus(String message, WorkUnitCallback.WU_STATE state, Object param1) {
		
		WorkUnitSQLState s = (WorkUnitSQLState)param1;
		if (state == WorkUnitCallback.WU_STATE.SUCCESSFUL_FINISH) {
			SQLHistoryItem hist = new SQLHistoryItem();
			hist.sql = s.data.sql.trim();
			hist.createdTime = new Date();
			hist.resultCount = s.data.resultCount;
			hist.hasMoreResults = s.data.hasMoreResults;
			hist.executionTime = s.data.executionTime;
			hist.wasUpdate = s.data.wasUpdate;
			hist.updateCount = s.data.updateCount;
			hist.setScope(app.appsh.getActiveWorkspace());
			hist.setResults(sqlUi.getData());

			// if the exact same sql already exists, delete it
			final DataManagerList sqlHistDM =  app.appsh.getDataManagerList(SQLHistoryItem.class);
			for (AppData d : sqlHistDM.getList()) {
				if (((SQLHistoryItem)d).sql.equals(hist.sql)) {
					List<String> del = new ArrayList<String>();
					del.add(d.getPrimaryKey());
					sqlHistDM.delete(del);
					break;
				}
			}
			// TODO limit size of history
			sqlHistDM.add(hist);
		}

		synchronized (sqlUi) {
			if (s.cancelWorkUnitAndQueueNewWU == null) {
				sqlUi.setFooterText(message, state == WorkUnitCallback.WU_STATE.SUCCESSFUL_FINISH);
				sqlUi.executeSqlDone();
				buttonManager.executeSqlDone(s.commitPending, s.moreRowsWaiting, s.connClosed);
				//sqlUi.currentRunningSQL = null;
				//buttonManager.commitAction.setEnabled(s.commitPending);
				//buttonManager.rollbackAction.setEnabled(s.commitPending);
				//buttonManager.getMoreResultsButton.setEnabled(s.moreRowsWaiting);
				//if (s.connClosed) {
				//	System.out.println("at 222222");
				//	buttonManager.currentWorkUnit = null;
				//}
			} else {
				buttonManager.setCurrentWorkUnit(s.cancelWorkUnitAndQueueNewWU);
				//buttonManager.currentWorkUnit = s.cancelWorkUnitAndQueueNewWU;
				s.cancelWorkUnitAndQueueNewWU.startWU();
			}
		}
	}
	
	/**
	 * param1 will be true if this is the first call with a new Query, it will be false if more results are being pulled
	 */
	@Override
	public void dataChanged(Object data, Object param1) {
		sqlUi.sqlResultsDataChanged(data, ((Boolean)param1).booleanValue());
	}
	
}
