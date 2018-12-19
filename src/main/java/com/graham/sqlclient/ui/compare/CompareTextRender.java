/* Copyright (C) 2016 Graham Anderson gandersonsw@gmail.com - All Rights Reserved */
package com.graham.sqlclient.ui.compare;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import com.graham.sqlclient.ui.compare.CompareUIPanel.CompareTextModel;

public class CompareTextRender extends DefaultTableCellRenderer {

	static final Color group1 = Color.WHITE;
	static final Color group1Sel = new Color(184, 207, 229);
	static final Color group1NoMatch = Color.YELLOW;
	static final Color group1NoMatchSelected = new Color(200, 200, 0);
	
	static final Color group1NoMatchAny = new Color(255, 100, 0);
	static final Color group1NoMatchAnySelected = new Color(200, 50, 0);
	
	static final Color matched = Color.GREEN;

	private static final long serialVersionUID = 1L;
	
	private CompareTextModel model;
	
	public CompareTextRender() {
	}
	
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

		if (model != null) {
			if (model.isMatched(row, column)) {
				c.setBackground(Color.GREEN);
			} else if (model.isTheSame(row, column)) {
				c.setBackground(isSelected ? group1Sel : group1);
			} else if (model.isTheSameAnywhere(row, column)) {
				c.setBackground(isSelected ? group1NoMatchSelected : group1NoMatch);
			} else {
				c.setBackground(isSelected ? group1NoMatchAnySelected : group1NoMatchAny);
			}
		}
		
		return c;
	}

	public void setModel(CompareTextModel modelParam) {
		model = modelParam;
	}

}
