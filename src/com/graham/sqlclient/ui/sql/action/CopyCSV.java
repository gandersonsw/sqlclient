/* Copyright (C) 2016 Graham Anderson gandersonsw@gmail.com - All Rights Reserved */
package com.graham.sqlclient.ui.sql.action;

import com.graham.sqlclient.ui.sql.SQLTableModel;
import com.graham.tools.SearchContext;
import com.graham.tools.StringTools;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;

/**
 * Created by grahamanderson on 6/18/16.
 */
public class CopyCSV extends AbstractAction {
	private static final long serialVersionUID = 1L;

	final private SQLTableModel model;

	public CopyCSV(final SQLTableModel modelParam) {
		super("Copy CSV for All");
		model = modelParam;
	}

	private String makeCSVSafe(String s) {
		return StringTools.replaceAll(StringTools.replaceAll(s, SearchContext.createReplaceAllContext("\n", "")), SearchContext.createReplaceAllContext(",", ""));
	}

	public void actionPerformed(ActionEvent e) {
		int colStart = 0;
		int rowStart = 0;
		StringBuilder csv = new StringBuilder();

		for (int col = colStart; col < model.getColumnCount(); col++) {
			String colName = model.getColumnName(col);
			csv.append(makeCSVSafe(colName));
			csv.append(',');
		}
		csv.append('\n');

		for (int row = rowStart; row < model.getRowCount(); row++) {
			for (int col = colStart; col < model.getColumnCount(); col++) {
				Object cellObject = model.getValueAt(row, col);
				if (cellObject != null) {
					csv.append(makeCSVSafe(cellObject.toString()));
				}
				csv.append(',');
			}
			csv.append('\n');
		}

		StringSelection ss = new StringSelection(csv.toString());
		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(ss, null);
	}
}
