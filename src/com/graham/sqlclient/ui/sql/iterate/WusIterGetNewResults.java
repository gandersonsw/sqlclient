/* Copyright (C) 2016 Graham Anderson gandersonsw@gmail.com - All Rights Reserved */
package com.graham.sqlclient.ui.sql.iterate;

import com.graham.appshell.*;
import com.graham.tools.*;

import javax.swing.*;
import java.io.*;
import java.sql.*;
import java.util.Date;

/**
 * Created by grahamanderson on 9/18/15.
 */
public class WusIterGetNewResults extends WusIterAbstract {

	WusIterNewResultSet newResultSetIter = new WusIterNewResultSet();

	private long startTime;

	private int csvRow;
	private FileReader csvFileReader;
	private BufferedReader csvBufferedReader;
	private String currentCSVLine;

	@Override
	public void start(WusIterContext context) throws SQLException {
		context.stmt = context.conn.createStatement();
		startTime = System.currentTimeMillis();
		context.prevSql.createdTime = new Date();
		initCSV();
		context.isFirstCsvRow = true;
	}

	@Override
	public boolean next(WusIterContext context) {
		return hasMoreCSVRows(context.hasCSV);
	}

	@Override
	public void afterNext(WusIterContext context) throws SQLException {
		context.prevSql.sql = substituteCSV(context.sql);
		context.currentCSV = currentCSV();
		context.stmt.setFetchSize(SqlTools.computeFetchSize(context.rowsPerSelect));

		context.prevSql.sql = context.prevSql.sql.trim();
		if (context.prevSql.sql.endsWith(";")) {
			context.prevSql.sql = context.prevSql.sql.substring(0, context.prevSql.sql.length() - 1);
		}

		if (context.stmt.execute(context.prevSql.sql)) {
			if (!context.workUnit.isWorkUnitCancelled()) {
				newResultSetIter.run(context);
			}
		} else {
			context.prevSql.updateCount = context.stmt.getUpdateCount();
			context.prevSql.wasUpdate = true;
		}
		context.isFirstCsvRow = false;
	}

	@Override
	public void end(WusIterContext context) {
		cleanUpCSV();
		context.prevSql.executionTime = System.currentTimeMillis() - startTime;
	}

	public boolean hasCSV(String sql) {
		return sql.indexOf("<csv1>") != -1;
	}

	private void initCSV() {
		csvRow = 0;
		csvFileReader = null;
	}

	private boolean hasMoreCSVRows(boolean hasCSV) {
		csvRow++;
		if (hasCSV) {
			if (csvFileReader == null) {
				final JFileChooser fc = new JFileChooser();
				int returnVal = fc.showOpenDialog(App.getCurrentApp().getMainFrame());

				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File csvFile = fc.getSelectedFile();
					//This is where a real application would open the file.
					System.out.println("Opening: " + csvFile.getName() + ".");

					csvFileReader = null;
					try {
						csvFileReader = new FileReader(csvFile);
					} catch (FileNotFoundException e) {
						e.printStackTrace();
						return false;
					}

					// Always wrap FileReader in BufferedReader.
					csvBufferedReader = new BufferedReader(csvFileReader);

				} else {
					System.out.println("Open command cancelled by user.");
					return false;
				}
			}

			try {
				currentCSVLine = csvBufferedReader.readLine();
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
			return currentCSVLine != null;
		} else {
			return csvRow == 1;
		}
	}

	private String substituteCSV(String sql) {
		return StringTools.replaceAll(sql, SearchContext.createReplaceAllContext("<csv1>", currentCSVLine));
	}

	private String currentCSV() {
		return currentCSVLine;
	}

	private void cleanUpCSV() {
		if (csvBufferedReader != null) {
			try {
				csvBufferedReader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		if (csvFileReader != null) {
			try {
				csvFileReader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
