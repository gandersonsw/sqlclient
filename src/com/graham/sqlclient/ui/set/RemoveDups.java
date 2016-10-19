/* Copyright (C) 2016 Graham Anderson gandersonsw@gmail.com - All Rights Reserved */
package com.graham.sqlclient.ui.set;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;

public class RemoveDups extends AbstractAction {
	private static final long serialVersionUID = 1L;

	private SetUIPanelParser parser;
	
	public RemoveDups(SetUIPanelParser parserParam) {
		super("Remove Dups");
		parser = parserParam;
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		List<SetRecord> items = parser.getCurrentList();
		List<SetRecord> items2 = new ArrayList<SetRecord>();
		for (SetRecord item : items) {
			if (!items2.contains(item)) {
				items2.add(item);
			}
		}

		parser.setOutputText(items2, items2.size() + " items with no duplicates");
	}
	
}
