/* Copyright (C) 2016 Graham Anderson gandersonsw@gmail.com - All Rights Reserved */
package com.graham.sqlclient.ui.set;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;

public class SetMinus extends AbstractAction {
	private static final long serialVersionUID = 1L;

	private SetUIPanelParser parser;
	
	public SetMinus(SetUIPanelParser parserParam) {
		super("Minus");
		parser = parserParam;
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		List<SetRecord> items1 = parser.getList(SetUIPanelParser.ItemIndex.SET1);
		List<SetRecord> items2 = parser.getList(SetUIPanelParser.ItemIndex.SET2);
		List<SetRecord> output = new ArrayList<SetRecord>(items1);
		
		for (SetRecord item : items2) {
			if (items1.contains(item)) {
				output.remove(item);
			}
		}
		
		parser.setOutputText(output, output.size() + " items in minus");
	}
	
}