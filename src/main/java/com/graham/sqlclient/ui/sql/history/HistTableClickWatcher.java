/* Copyright (C) 2016 Graham Anderson gandersonsw@gmail.com - All Rights Reserved */
package com.graham.sqlclient.ui.sql.history;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JCheckBox;
import javax.swing.JTable;

import com.graham.sqlclient.ui.sql.SqlUIPanel;
import com.graham.sqlclient.ui.sql.SQLHistoryItem;
import com.graham.tools.DateTools;

public class HistTableClickWatcher extends MouseAdapter {
	private SqlHistoryTableModel model;
	private JTable table;
	private JCheckBox loadResultsToo;
	private SqlUIPanel sqlUi;

	public HistTableClickWatcher(SqlHistoryTableModel modelParam, JTable tableParam, SqlUIPanel sqlUiParam, JCheckBox loadResultsTooParam) {
		model = modelParam;
		table = tableParam;
		loadResultsToo = loadResultsTooParam;
		sqlUi = sqlUiParam;
	}
	
	public void mouseClicked(MouseEvent evt) {
		if (evt.getClickCount() == 2) {
		
			int row = table.getSelectedRow();
			int col = table.getSelectedColumn();
			
			SQLHistoryItem h = model.getSQLHistoryAt(row, col);

			sqlUi.addTextBlock(h.sql);
			//sqlUi.query.insert(h.sql, sqlUi.query.getCaretPosition());
			
			if (loadResultsToo.isSelected() && h.hasResults()) {
				String m = "SQL Run time: " + DateTools.formatTimeSpan(h.executionTime) + " Rows: " + h.resultCount + (h.hasMoreResults ? "+" : "");
				sqlUi.setFooterText(m + " (10 cached results from " + h.createdTime + ")", true);
				sqlUi.sqlResultsDataChanged(h.getResults(), true);
			}
		}
	}
}
