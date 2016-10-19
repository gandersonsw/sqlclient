/* Copyright (C) 2016 Graham Anderson gandersonsw@gmail.com - All Rights Reserved */
package com.graham.sqlclient.ui.sqltablebrowser;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class SqlTableNameCellRender extends DefaultTableCellRenderer {

	private static final long serialVersionUID = 1L;
	
	int startFilterOutRow = 10;
	
	public void setStartFilterOutRow(int i) {
		startFilterOutRow = i - 1;
	}
	
	public boolean isRowFilteredOut(int row) {
		return row > startFilterOutRow;
	}

	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		
		if (isRowFilteredOut(row)) {
			if (isSelected) {
				c.setBackground(Color.RED);
			} else {
				c.setBackground(Color.PINK);
			}
		} else {
			if (isSelected) {
				c.setBackground(Color.LIGHT_GRAY);
			} else {
				c.setBackground(Color.WHITE);
			}
		}
		
		return c;
	}

}
