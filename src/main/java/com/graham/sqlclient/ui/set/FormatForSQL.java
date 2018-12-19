/* Copyright (C) 2016 Graham Anderson gandersonsw@gmail.com - All Rights Reserved */
package com.graham.sqlclient.ui.set;

import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.AbstractAction;

import com.graham.tools.SearchContext;
import com.graham.tools.StringTools;

public class FormatForSQL extends AbstractAction {
	private static final long serialVersionUID = 1L;

	private SetUIPanelParser parser;

	public FormatForSQL(SetUIPanelParser parserParam) {
		super("Format For SQL");
		parser = parserParam;
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		List<SetRecord> items = parser.getCurrentList();
		StringBuilder sb = new StringBuilder();
		for (SetRecord item : items) {
			sb.append("'");
			sb.append(StringTools.replaceAll(item.toString(), SearchContext.createReplaceAllContext("'", "''")));
			sb.append("',\n");
		}
		
		sb.setLength(sb.length() - 2); // remove the last newline and comma
		
		parser.setOutputText(sb.toString(), items.size() + " items");
	}
	
}
