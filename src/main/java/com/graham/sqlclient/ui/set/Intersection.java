/* Copyright (C) 2016 Graham Anderson gandersonsw@gmail.com - All Rights Reserved */
package com.graham.sqlclient.ui.set;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;

public class Intersection extends AbstractAction {
	private static final long serialVersionUID = 1L;

	private SetUIPanelParser parser;
	
	public Intersection(SetUIPanelParser parserParam) {
		super("Intersection");
		parser = parserParam;
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		List<SetRecord> items1 = parser.getList(SetUIPanelParser.ItemIndex.SET1);
		List<SetRecord> items2 = parser.getList(SetUIPanelParser.ItemIndex.SET2);
		List<SetRecord> output = new ArrayList<SetRecord>();
		
		for (SetRecord item : items1) {
			if (items2.contains(item)) {
				output.add(item);
			}
		}
		
		parser.setOutputText(output, output.size() + " items in intersection");
	}
	
}