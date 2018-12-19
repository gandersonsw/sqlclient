/* Copyright (C) 2016 Graham Anderson gandersonsw@gmail.com - All Rights Reserved */
package com.graham.sqlclient.ui.set;

import java.util.ArrayList;
import java.util.List;

public class SetRecord {
	private List<String> fields;
	private String keyValue;
	private final SetUIPanelSet part;
	private String text;
	private int count;
	
	public SetRecord(final String textParam, final SetUIPanelSet partParam) {
		text = textParam;
		part = partParam;
	}
	
	public int getCount() {
		return count;
	}
	
	public void setCount(int countParam) {
		count = countParam;
	}
	
	public void incrementCount() {
		count++;
	}
	
	public String getKey() {
		if (fields == null) {
			parseFields();
		}
		return keyValue;
	}
	
	public List<String> getFields() {
		if (fields == null) {
			parseFields();
		}
		return fields;
	}
	
	/**
	 * @param fieldValue
	 * @param pos Starts at 1
	 */
	private void setFieldAt(String fieldValue, int pos) {
		if (pos-1 < getFields().size()) {
			getFields().set(pos - 1, fieldValue);
			return;
		}
		
		while (pos-1 > getFields().size()) {
			getFields().add("");
		}
		getFields().add(fieldValue);
	}
	
	private void parseFields() {
		fields = new ArrayList<String>();
		List<SetUIPanelSet.ZToken> fl = part.getFormatList();
		int startLoc = 0;
		int startLoc2 = 0;
		int endLoc = 0;
		int lastPositionForIncompleteParse = -1;
		SetUIPanelSet.ZToken lastToken = null;
		for (SetUIPanelSet.ZToken t : fl) {
			if (t.s.length() == 0) {
				endLoc = startLoc;
				startLoc2 = endLoc;
			} else {
				endLoc = text.indexOf(t.s, startLoc);
				if (endLoc == -1) {
					endLoc = startLoc;
					startLoc2 = startLoc;
				} else {
					startLoc2 = endLoc + t.s.length(); // start at the end of the delimiter
				}
			}
			if (lastToken != null) {
				String fieldValue = text.substring(startLoc, endLoc);
				if (fieldValue.length() != 0) {
					//fields.add(fieldValue);
					setFieldAt(fieldValue, lastToken.position);
					if (lastToken.position == 1) {
						keyValue = fieldValue;
					}
				} else {
					lastPositionForIncompleteParse = lastToken.position;
				}
			}
			startLoc = startLoc2;
			lastToken = t;
		}
		
		if (lastToken == null) {
			String fieldValue = text.substring(startLoc);
			fields.add(fieldValue);
			keyValue = fieldValue;
		} else {
			String fieldValue = text.substring(startLoc);
			if (lastPositionForIncompleteParse != -1) {
				setFieldAt(fieldValue, lastPositionForIncompleteParse);
			} else {
				fields.add(fieldValue);
			}
			if (lastToken.position == 1 || keyValue == null) { // if the keyValue is null, the format didn't work, so use the entire string
				keyValue = fieldValue;
			}
		}
	}
	
	public String format(final SetUIPanelSet partParam) {
		StringBuilder sb = new StringBuilder();
		List<SetUIPanelSet.ZToken> fl = partParam.getFormatList();
		
		for (int i = 0 ; i < fl.size(); i++) {
			sb.append(fl.get(i).s);
			if (fl.get(i).position <= getFields().size() ) {
				sb.append(getFields().get(fl.get(i).position-1));
			}
		}
		
		if (count > 0) {
			sb.append(" (");
			sb.append(count);
			sb.append(")");
		}
		
		return sb.toString();
	}
	
	@Override
	public String toString() {
		return text;
	}
	
	public boolean equals(Object obj) {
		if (obj instanceof SetRecord) {
			return ((SetRecord)obj).getKey().equals(getKey());
		}
		return false;
	}
	
	public int hashCode() {
		return getKey().hashCode();
	}
}
