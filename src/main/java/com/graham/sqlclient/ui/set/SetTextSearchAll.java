/* Copyright (C) 2016 Graham Anderson gandersonsw@gmail.com - All Rights Reserved */
package com.graham.sqlclient.ui.set;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;

public class SetTextSearchAll extends AbstractAction {
	private static final long serialVersionUID = 1L;

	private SetUIPanelParser parser;

	public SetTextSearchAll(SetUIPanelParser parserParam) {
		super("Search");
		parser = parserParam;
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		List<SetRecord> items1 = parser.getList(SetUIPanelParser.ItemIndex.SET1);
		String text = parser.getText(SetUIPanelParser.ItemIndex.SET2);
		List<SetRecord> output = new ArrayList<SetRecord>();
		
		for (SetRecord item : items1) {
			if (text.indexOf(item.getKey()) != -1) {
				output.add(item);
			}
		}
		
		parser.setOutputText(output, output.size() + " items found by search");
	}
	
}