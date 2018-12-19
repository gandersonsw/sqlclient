/* Copyright (C) 2016 Graham Anderson gandersonsw@gmail.com - All Rights Reserved */
package com.graham.sqlclient.ui.sql;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Created by grahamanderson on 9/25/15.
 */
public class GlobalRunCurrentSql extends AbstractAction {

	private static final long serialVersionUID = 1L;

	private Action buttonAction; // TODO this should be tabManager

	public GlobalRunCurrentSql(Action buttonActionParam) {
		super("Run Selected SQL");
		putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		buttonAction = buttonActionParam;
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		buttonAction.actionPerformed(arg0);
	}
}
