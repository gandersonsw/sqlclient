/* Copyright (C) 2016 Graham Anderson gandersonsw@gmail.com - All Rights Reserved */
package com.graham.sqlclient.ui.sqlperformance;

import javax.swing.table.*;

public class PerfResultsDataModel extends AbstractTableModel {

	final public static int MAX_SQL_TO_TEST = 5;

	// rowIndex is which test
	// colIndex is which SQL (times 3 fields)
	Object resultsArr[][];

	public PerfResultsDataModel() {
	}

	public void setOneTestResult(int testIndex, int sqlIndex, long queryTime, long queryTimeAndLoadTime, String message) {
		long queryTimeM = queryTime / 1000000L;
		long queryTimeAndLoadTimeM = queryTimeAndLoadTime / 1000000L;

		boolean addedRow = false;
		if (testIndex >= resultsArr.length) {
			Object newData[][] = new Object[testIndex + 1][MAX_SQL_TO_TEST*3];
			for (int i = 0; i < testIndex; i++) {
				for (int j = 0; j < MAX_SQL_TO_TEST*3; j++) {
					newData[i][j] = resultsArr[i][j];
				}
			}
			resultsArr = newData;
			addedRow = true;
		}

		int cellStartIndex = sqlIndex * 3;

		resultsArr[testIndex][cellStartIndex] = new Double(queryTimeM / 1000.0);
		resultsArr[testIndex][cellStartIndex+1] = new Double(queryTimeAndLoadTimeM / 1000.0);
		resultsArr[testIndex][cellStartIndex+2] = message;

		if (addedRow) {
			fireTableDataChanged();
		} else {
			fireTableCellUpdated(testIndex, cellStartIndex);
			fireTableCellUpdated(testIndex, cellStartIndex+1);
			fireTableCellUpdated(testIndex, cellStartIndex+2);
		}
	}

	public void clearResults() {
		resultsArr = new Object[1][MAX_SQL_TO_TEST*3];
		for (int i = 0; i < MAX_SQL_TO_TEST; i++) {
			resultsArr[0][i] = "";
		}
	}

	@Override
	public int getRowCount() {
		return resultsArr.length + 1; // last row is avg
	}

	@Override
	public int getColumnCount() {
		return resultsArr[0].length;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		if (rowIndex >= resultsArr.length) {
			double total = 0.0;
			for (int i = 0; i < resultsArr.length; i++) {
				if (resultsArr[i][columnIndex] instanceof Double) {
					total += (Double)resultsArr[i][columnIndex];
				}

			}
			double t = 1000.0 * total / resultsArr.length;
			long t1 = (long)t;
			return (double)t1 / 1000.0;
		}
		return resultsArr[rowIndex][columnIndex];
	}

	@Override
	public String getColumnName(int index) {
		int testIndex = index / 3 + 1;
		int fieldNumber = index % 3;
		if (fieldNumber == 0) {
			return "SQL" + testIndex + " s";
		} else if (fieldNumber == 1) {
			return "S+D" + testIndex + " s";
		} else if (fieldNumber == 2) {
			return testIndex + " notes";
		}
		return "rtyu";
	}
}