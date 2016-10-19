/* Copyright (C) 2016 Graham Anderson gandersonsw@gmail.com - All Rights Reserved */
package com.graham.sqlclient.ui.set;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.JTextArea;

public class SetUIPanelParser {
	
	public enum ItemIndex {
		SET1,
		SET2,
		OUTPUT
	}

	private List<SetUIPanelSet> setList = new ArrayList<SetUIPanelSet>();
	
	public void addSet(SetUIPanelSet set) {
		setList.add(set);
	}

	private JTextArea getCurrentTextArea() {
		return getCurrentPart().text;
	}
	
	public SetUIPanelSet getCurrentPart() {
		for (SetUIPanelSet p : setList) {
			if (p.radio.isSelected()) {
				return p;
			}
		}
		return null;
	}
	
	public String getCurrentText() {
		JTextArea ta = getCurrentTextArea();
		if (ta == null) {
			return "";
		}
		return ta.getText();
	}

	/**
	 * textAreaText can be a list of SetRecords, or Strings
	 */
	public void setCurrentText(List<?> textAreaText, String labelText) {
		setTextInternal(getCurrentPart(), textAreaText, labelText);
	}
	
	/**
	 * textAreaText can be a list of SetRecords, or Strings
	 */
	public void setOutputText(List<?> textAreaText, String labelText) {
		setTextInternal(getSet(ItemIndex.OUTPUT), textAreaText, labelText);
	}

	/**
	 * textAreaText can be a list of SetRecords, or Strings
	 */
	private void setTextInternal(SetUIPanelSet part, Collection<?> textAreaText, String labelText) {
		//SetRecord recordArr[] = (SetRecord[])textAreaText.toArray();
		// sort
		
		String delim = part.getDelim();
		StringBuilder sb = new StringBuilder();
		for (Object item : textAreaText) {
			sb.append(part.formatRecord(item));
			sb.append(delim);
		}
		if (sb.length() > 0) {
			sb.setLength(sb.length() - delim.length()); // remove the last delim
		}
		setTextInternal(part, sb.toString(), labelText);
	}
	
	private void setTextInternal(SetUIPanelSet part, String textAreaText, String labelText) {
		if (textAreaText != null && part.text != null) {
			part.text.setText(textAreaText);
		}
		if (labelText != null && part.summary != null) {
			part.summary.setText(labelText);
		}
	}
	
	public void setCurrentText(String textAreaText, String labelText) {
		setTextInternal(getCurrentPart(), textAreaText, labelText);
	}
	
	public void setOutputText(String textAreaText, String labelText) {
		setTextInternal(getSet(ItemIndex.OUTPUT), textAreaText, labelText);
	}
	
	private SetUIPanelSet getSet(ItemIndex index) {
		if (index == ItemIndex.SET1) {
			return setList.get(0);
		}
		if (index == ItemIndex.SET2) {
			return setList.get(1);
		}
		if (index == ItemIndex.OUTPUT) {
			return setList.get(2);
		}
		System.out.println("SetUIPanelParser:getSet:warning: invalid index");
		return null;
	}

	public String getText(ItemIndex index) {
		JTextArea ta = getSet(index).text;
		if (ta == null) {
			return "";
		}
		return ta.getText();
	}
	
	public List<SetRecord> getCurrentList() {
		return getListInternal(this.getCurrentPart());
	}
	
	public List<SetRecord> getList(ItemIndex index) {
		return getListInternal(getSet(index));
	}
	
	private List<SetRecord> getListInternal(SetUIPanelSet set) {
		String txt = set.text.getText();
		String[] tokens = txt.split(set.getDelim());
		List<SetRecord> items = new ArrayList<SetRecord>();
		for (String t : tokens) {
			SetRecord rec = new SetRecord(t, set);
			items.add(rec);
		}
		return items;
	}
	
}
