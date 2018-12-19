/* Copyright (C) 2016 Graham Anderson gandersonsw@gmail.com - All Rights Reserved */
package com.graham.sqlclient.ui.compare;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class CompareCellRender extends DefaultTableCellRenderer {

	static final Color group1 = Color.WHITE;
	static final Color group1Sel = new Color(184, 207, 229);
	static final Color group1NoMatch = Color.YELLOW;
	static final Color group1NoMatchSelected = Color.ORANGE;

	static final Color group2 = Color.LIGHT_GRAY;
	static final Color group2Sel = Color.GRAY;
	static final Color group2NoMatch = new Color(255 - 40, 255 - 40, 0);
	static final Color group2NoMatchSelected = new Color(255 - 40, 200 - 40, 0);
	
	
	private static final long serialVersionUID = 1L;
	
	private CompareUIPanel.CompareDataModel model;
	
	public CompareCellRender(CompareUIPanel.CompareDataModel modelParam) {
		model = modelParam;
	}
	
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

		if (column == 0) {
			// do something for labels?
			c.setBackground(isSelected ? group2Sel : group2);
			c.setFont(c.getFont().deriveFont(Font.BOLD));
			return c;
		}
		
		if (isGroupIndexEven(column)) {
			if (isMatching(row, column)) {
				c.setBackground(isSelected ? group1Sel : group1);
			} else {
				c.setBackground(isSelected ? group1NoMatchSelected : group1NoMatch);
			}
		} else {
			if (isMatching(row, column)) {
				c.setBackground(isSelected ? group2Sel : group2);
			} else {
				c.setBackground(isSelected ? group2NoMatchSelected : group2NoMatch);
			}
		}
		
		return c;
	}
	
	private boolean isGroupIndexEven(int column) {
		return model.getGroupIndex(column) % 2 == 0;
	}
	
	private boolean isMatching(int row, int column) {
		return model.isMatching(row, column);
	}

}
