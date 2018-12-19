/* Copyright (C) 2016 Graham Anderson gandersonsw@gmail.com - All Rights Reserved */
package com.graham.sqlclient.ui.set;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;

public class SetCount extends AbstractAction {
	private static final long serialVersionUID = 1L;

	private SetUIPanelParser parser;
	
	public SetCount(SetUIPanelParser parserParam) {
		super("Counts");
		parser = parserParam;
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		List<SetRecord> items = parser.getCurrentList();
		List<SetRecord> items2 = new ArrayList<SetRecord>();
		for (SetRecord item : items) {
			if (!items2.contains(item)) {
				item.setCount(1);
				items2.add(item);
			} else {
				SetRecord i3 = findRecord(items2, item);
				i3.incrementCount();
			}
		}

		parser.setOutputText(items2, items2.size() + " items with no duplicates");
	}
	
	SetRecord findRecord(List<SetRecord> items, SetRecord itemToFind) {
		for (SetRecord item : items) {
			if (item.equals(itemToFind)) {
				return item;
			}
		}
		return null;
	}
	
}
