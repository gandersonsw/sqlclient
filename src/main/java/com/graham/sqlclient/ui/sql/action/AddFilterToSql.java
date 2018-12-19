/* Copyright (C) 2016 Graham Anderson gandersonsw@gmail.com - All Rights Reserved */
package com.graham.sqlclient.ui.sql.action;

import com.graham.sqlclient.ui.sql.SQLTableModel;
import com.graham.sqlclient.ui.sql.SqlUIPanel;
import com.graham.tools.SqlInfo;
import com.graham.tools.SqlTools;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by grahamanderson on 6/18/16.
 */
public class AddFilterToSql extends AbstractAction {
	private static final long serialVersionUID = 1L;

	final private String tableNameForFilter;
	final private boolean filterOut;
	final private SqlInfo sqlInfo;
	final private SQLTableModel model;
	final private JTable table;
	final private SqlUIPanel sqlUi;

	/**
	 * @param tableNameParam
	 * @param filterOutParam  If true, filter out the selected values, if false, filter out all other values
	 */
	public AddFilterToSql(final String tableNameParam, final boolean filterOutParam, final SqlInfo sqlInfoParam, final SQLTableModel modelParam, final JTable tableParam, final SqlUIPanel sqlUiParam) {
		super((filterOutParam ? "Filter Out (" : "Filter Others Out (") + tableNameParam +")");
		tableNameForFilter = tableNameParam;
		filterOut = filterOutParam;
		sqlInfo = sqlInfoParam;
		model = modelParam;
		table = tableParam;
		sqlUi = sqlUiParam;
	}

	public void actionPerformed(ActionEvent e) {
		final int rows[] = table.getSelectedRows();

		if (rows.length < 1) {
			return; // if less than 1, can't do anything
		}

		final int columns[] = table.getSelectedColumns();

		final List<Set<String>> valuesToFilter = new ArrayList<Set<String>>();

		for (int j = 0; j < columns.length; j++) {
			valuesToFilter.add(new HashSet<String>());
		}

		for (int i = 0; i < rows.length; i++) {
			int row = rows[i];

			for (int j = 0; j < columns.length; j++) {
				int col = columns[j];
				Object cellObject = model.getValueAt(row, col);
				valuesToFilter.get(j).add(cellObject.toString());
			}
		}

		String tableShortcut = "";
		if (sqlInfo.tableShortcuts.containsValue(tableNameForFilter)) {
			for (Map.Entry<String, String> etry : sqlInfo.tableShortcuts.entrySet()) {
				if (etry.getValue().equals(tableNameForFilter)) {
					tableShortcut = etry.getKey() + ".";
				}
			}
		}

		final StringBuffer outputSql = new StringBuffer();
	//	outputSql.append("\n\n");
		outputSql.append(sqlInfo.sql.trim());
		for (int j = 0; j < columns.length; j++) {
			if (sqlInfo.hasWhere || j > 1) {
				outputSql.append(" AND ");
			} else {
				outputSql.append(" WHERE ");
			}

			int col = columns[j];
			String colName = model.getColumnName(col);

			outputSql.append(tableShortcut);
			outputSql.append(colName);
			if (filterOut) {
				outputSql.append(" NOT IN (");
			} else {
				outputSql.append(" IN (");
			}

			for (final String val : valuesToFilter.get(j)) {
				outputSql.append("'");
				outputSql.append(SqlTools.makeTextSqlSafe(val));
				outputSql.append("',");
			}

			// remove last comma
			outputSql.setLength(outputSql.length() - 1);

			outputSql.append(")");
		}

	//	outputSql.append("\n");

		sqlUi.addTextBlock(outputSql.toString());

	//	query.append(outputSql.toString());
	}
}
