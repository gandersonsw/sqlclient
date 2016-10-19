/* Copyright (C) 2016 Graham Anderson gandersonsw@gmail.com - All Rights Reserved */
package com.graham.sqlclient.ui.databrowser;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class DBCellRender extends DefaultTableCellRenderer {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5771014643963235929L;
	
	final private DBContext context;
	
	public DBCellRender(DBContext contextParam) {
		super();
		context = contextParam;
	}

	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

		String columnName = context.jtableModel.getValueColumnName(row, column);
		
		if (context.jtableModel.isFilteredOut(row, column)) {
			if (isSelected) {
				c.setBackground(Color.RED);
			} else {
				c.setBackground(Color.PINK);
			}
		} else if (columnName != null && context.relshipTracker.shouldHiliteValue(columnName)) {
			// Only for specific cell
				//c.setFont(/* special font */);
				// you may want to address isSelected here too
				//c.setForeground(/* special foreground color */);
			if (isSelected) {
				c.setBackground(Color.ORANGE);
			} else {
				c.setBackground(Color.YELLOW);
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
