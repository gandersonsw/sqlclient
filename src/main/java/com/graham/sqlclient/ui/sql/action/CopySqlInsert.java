/* Copyright (C) 2016 Graham Anderson gandersonsw@gmail.com - All Rights Reserved */
package com.graham.sqlclient.ui.sql.action;

import com.graham.sqlclient.ui.sql.SQLTableModel;
import com.graham.tools.SqlTools;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;

/**
 * Created by grahamanderson on 6/18/16.
 */
public class CopySqlInsert extends AbstractAction {
	private static final long serialVersionUID = 1L;

	final private String tableNameForInsertSql;
	final private SQLTableModel model;
	final private JTable table;

	public CopySqlInsert(String tableNameParam, final SQLTableModel modelParam, final JTable tableParam) {
		super("Copy Sql for Selected");
		tableNameForInsertSql = tableNameParam;
		model = modelParam;
		table = tableParam;
	}

	public void actionPerformed(ActionEvent e) {
		int colStart = 0;
		int rows[] = table.getSelectedRows();
		StringBuilder insertSql = new StringBuilder();

		for (int i = 0; i < rows.length; i++) {
			int row = rows[i];
			insertSql.append("\nINSERT INTO ");
			insertSql.append(tableNameForInsertSql);
			insertSql.append(" (");
			for (int col = colStart; col < model.getColumnCount(); col++) {
				String colName = model.getColumnName(col);
				insertSql.append(colName);
				if (col < model.getColumnCount() - 1) {
					insertSql.append(", ");
				}
			}
			insertSql.append(") VALUES (");

			for (int col = colStart; col < model.getColumnCount(); col++) {
				Object cellObject = model.getValueAt(row, col);
				if (cellObject != null) {
					insertSql.append("'");
					insertSql.append(SqlTools.makeTextSqlSafe(cellObject.toString()));
					insertSql.append("'");
				} else {
					insertSql.append("NULL");
				}
				if (col < model.getColumnCount() - 1) {
					insertSql.append(", ");
				}
			}
			insertSql.append(");\n");
		}

		StringSelection ss = new StringSelection(insertSql.toString());
		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(ss, null);
	}
}
