/* Copyright (C) 2016 Graham Anderson gandersonsw@gmail.com - All Rights Reserved */
package com.graham.sqlclient.ui.sqlperformance;

import com.graham.tools.*;

import java.sql.*;
import java.util.*;

/**
 * Created by grahamanderson on 5/25/16.
 */
public class SqlPerfTestThread extends Thread {

	private int numberOfTestsToRun;
	private int resultRowsLimit;
	private int fetchSize;
	private List<String> sqls;
	private List<String> preAndPostSqls;
	private ConnPool cp;
	private PerfResultsDataModel resultsTableModel;
	private ThreadManagerWithButtons testThreadManager;

	public SqlPerfTestThread(int numberOfTestsToRunParam, int resultRowsLimitParam, int fetchSizeParam, List<String> sqlsParam, List<String> preAndPostSqlsParam, ConnPool cpParam, PerfResultsDataModel resultsTableModelParam, ThreadManagerWithButtons testThreadManagerParam) {
		numberOfTestsToRun = numberOfTestsToRunParam;
		resultRowsLimit = resultRowsLimitParam;
		fetchSize = fetchSizeParam;
		sqls = sqlsParam;
		preAndPostSqls = preAndPostSqlsParam;
		cp = cpParam;
		resultsTableModel = resultsTableModelParam;
		testThreadManager = testThreadManagerParam;
	}

	public void run() {
		resultsTableModel.clearResults();
		resultsTableModel.fireTableDataChanged();
		testThreadManager.updateCompletePercentage(0, numberOfTestsToRun, 0);
		for (int testIndex = 0; testIndex < numberOfTestsToRun; testIndex++) {
			for (int sqli = 0; sqli < sqls.size(); sqli++) {
				String sql = sqls.get(sqli);

				if (testThreadManager.isKillThread()) {
					testThreadManager.killed();
					return;
				}

				Connection conn = null;
				Statement stmt = null;
				ResultSet rs = null;
				boolean testSuccessful = false;
				try {
					if (testIndex == 0 && sqli == 0) {
						conn = cp.getConn();
						// the first time through, run the POST sql before the first test is run
						for (String postSql : preAndPostSqls) {
							stmt = conn.createStatement();
							stmt.execute(postSql);
							stmt.close();
							stmt = null;
						}
						cp.releaseConn(conn);
						conn = null;
					}

					conn = cp.getConn();
					stmt = conn.createStatement();
				//	stmt.setFetchSize(resultRowsLimit);
					stmt.setFetchSize(fetchSize);

					long startTime = System.nanoTime();
					rs = stmt.executeQuery(sql);
					long queryTime = System.nanoTime() - startTime;
					ResultSetMetaData md = rs.getMetaData();
					int colCount = md.getColumnCount();

					int rowIndex = 0;
					while (rowIndex < resultRowsLimit && rs.next()) {
						for (int colIndex = 1; colIndex <= colCount; colIndex++) {
							Object o = rs.getObject(colIndex);
						}
						rowIndex++;
					}
					long queryTimeAndLoadTime = System.nanoTime() - startTime;

					resultsTableModel.setOneTestResult(testIndex, sqli, queryTime, queryTimeAndLoadTime, "rows=" + rowIndex);
					testSuccessful = true;

					rs.close();
					rs = null;
					stmt.close();
					stmt = null;

					for (String postSql : preAndPostSqls) {
						stmt = conn.createStatement();
						stmt.execute(postSql);
						stmt.close();
						stmt = null;
					}

				} catch (Exception e) {
					e.printStackTrace();
					if (!testSuccessful) {
						resultsTableModel.setOneTestResult(testIndex, sqli, 0, 0, e.getMessage());
					}
				} finally {
					try {
						if (rs != null)
							rs.close();
					} catch (SQLException e1) {
						//e1.printStackTrace();
					}
					try {
						if (stmt != null)
							stmt.close();
					} catch (SQLException e1) {
						//e1.printStackTrace();
					}
					try {
						if (conn != null)
							cp.releaseConn(conn);
					} catch (SQLException e1) {
						//e1.printStackTrace();
					}
				}

				testThreadManager.updateCompletePercentage(testIndex, numberOfTestsToRun, sqli);

			}
		}

		testThreadManager.completed();
		resultsTableModel.fireTableDataChanged();
	}

}
