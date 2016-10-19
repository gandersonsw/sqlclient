/* Copyright (C) 2016 Graham Anderson gandersonsw@gmail.com - All Rights Reserved */
package com.graham.sqlclient.ui.sql;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class SQLCellRenderer extends DefaultTableCellRenderer {

	private static final long serialVersionUID = 1L;
	
	private boolean notCurrent = false;
	
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		if (notCurrent) {
			c.setForeground(Color.GRAY);
		} else {
			c.setForeground(Color.BLACK);
		}
		return c;
	}
	
	public void setNotCurrent(boolean b) {
		notCurrent = b;
	}

}
