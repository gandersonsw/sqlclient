/* Copyright (C) 2016 Graham Anderson gandersonsw@gmail.com - All Rights Reserved */
package com.graham.sqlclient.ui.set;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.AbstractAction;

public class ListDups extends AbstractAction {
	private static final long serialVersionUID = 1L;

	private SetUIPanelParser parser;
	
	public ListDups(SetUIPanelParser parserParam) {
		super("List Dups");
		parser = parserParam;
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		List<SetRecord> items = parser.getCurrentList();
		Set<SetRecord> items2 = new HashSet<SetRecord>();
		List<String> dups = new ArrayList<String>();
		List<SetRecord> dupsValues = new ArrayList<SetRecord>();
		for (SetRecord item : items) {
			if (items2.contains(item)) {
				if (!dups.contains(item.getKey())) {
					dups.add(item.getKey());
					dupsValues.add(item);
				}
			}
			items2.add(item);
		}
		
		parser.setOutputText(dupsValues, dupsValues.size() + " items are duplicates");
	}
	
}