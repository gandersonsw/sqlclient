/* Copyright (C) 2016 Graham Anderson gandersonsw@gmail.com - All Rights Reserved */
package com.graham.sqlclient.ui.set;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;

import javax.swing.AbstractAction;

public class CleanList extends AbstractAction {
	private static final long serialVersionUID = 1L;

	private SetUIPanelParser parser;
	
	public CleanList(SetUIPanelParser parserParam) {
		super("Clean List");
		parser = parserParam;
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		String text = parser.getCurrentText();
		String token = parser.getCurrentPart().getDelim();
		
		if (text.indexOf(token) != -1) {
			
		} else if (text.indexOf('\n') != -1) {
			token = "\n";
		} else if (text.indexOf('\t') != -1) {
			token = "\t";
		} else if (text.trim().indexOf(' ') != -1) {
			token = " ";
		} else {
			token = ",";
		}
		
		StringTokenizer st = new StringTokenizer(text, token);
		
		List<String> items = new ArrayList<String>();
		
		while (st.hasMoreTokens()) {
			String s = st.nextToken().trim();
			if (!s.equals("")) {
				items.add(s);
			}
		}
		
		Object itemsArray[] = items.toArray();
		Arrays.sort(itemsArray);
		
		List<Object> itemsArrayAsList = Arrays.asList(itemsArray);
		
		parser.setCurrentText(itemsArrayAsList, itemsArrayAsList.size() + " Items");
	}
	
}