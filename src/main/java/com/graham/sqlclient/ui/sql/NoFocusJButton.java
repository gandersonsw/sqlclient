/* Copyright (C) 2016 Graham Anderson gandersonsw@gmail.com - All Rights Reserved */
package com.graham.sqlclient.ui.sql;

import javax.swing.Action;

import javax.swing.JButton;

public class NoFocusJButton extends JButton {
		private static final long serialVersionUID = 1L;
		
		public NoFocusJButton(Action a) {
			super(a);
		}

		public boolean isRequestFocusEnabled() {
			return false;
		}

		public boolean isFocusable() {
			return false;
		}

}
