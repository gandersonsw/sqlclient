/* Copyright (C) 2016 Graham Anderson gandersonsw@gmail.com - All Rights Reserved */
package com.graham.sqlclient.ui.sql;

import javax.swing.JTable;
import javax.swing.table.TableModel;

public class NoFocusJTable extends JTable {
	private static final long serialVersionUID = 1L;
	
	public NoFocusJTable(TableModel dm) {
		super(dm);
	}

	public boolean isRequestFocusEnabled() {
		return false;
	}

	public boolean isFocusable() {
		return false;
	}
}
