/* Copyright (C) 2016 Graham Anderson gandersonsw@gmail.com - All Rights Reserved */
package com.graham.sqlclient.ui.set;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.graham.tools.StringTools;

/**
 * One set within the set panel UI.
 */
public class SetUIPanelSet {
	
	class ZToken {
		String s;
		int position;
		ZToken(String posParam, String textParam) {
			position = Integer.parseInt(posParam);
			s = StringTools.unescapeTabsAndNewlines(textParam);
		}
	}

	// TODO make these private ???
	JTextField format;
	JTextArea text;
	JLabel summary;
	JRadioButton radio;
	private String parsedFormat;
	private String delim;
	private List<ZToken> formatList;
	
	String getDelim() {
		if (parsedFormat == null || !parsedFormat.equals(format.getText())) {
			parseFormat();
		}
		return delim;
	}
	
	List<ZToken> getFormatList() {
		if (parsedFormat == null || !parsedFormat.equals(format.getText())) {
			parseFormat();
		}
		return formatList;
	}
	
	void parseFormat() {
		formatList = new ArrayList<ZToken>();
		parsedFormat = format.getText();	
		int i;
		String curNumber = "";
		String curText = "";
		int state = 0; // 0 = undefined, 1 = in number, 2 = not in number
		int newState = 0;
		char c;
		for (i = 0; i < parsedFormat.length(); i++) {
			c = parsedFormat.charAt(i);
			newState = Character.isDigit(c) ? 1 : 2;
			if (state == 1 && newState == 2) { // have a number, and its done
				formatList.add(new ZToken(curNumber, curText));
				curNumber = "";
				curText = "";
			}
			
			if (newState == 1) {
				curNumber += c;
			} else if (newState == 2) {
				curText += c;
			}
			state = newState;
		}
		
		// curNumber.length() should be 0 here because delim should be last
		
		if (curNumber.length() > 0) {
			System.out.println("warning: ignoring number:" + curNumber);
		}

		delim = StringTools.unescapeTabsAndNewlines(curText);
	}
	
	public String formatRecord(Object rec) {
		if (rec instanceof String) {
			return (String)rec;
		} else if (rec instanceof SetRecord) {
			return ((SetRecord)rec).format(this);
		}
		return rec.toString();
	}
	
}
