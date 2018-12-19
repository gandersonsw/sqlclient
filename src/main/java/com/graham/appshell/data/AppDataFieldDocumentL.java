/* Copyright (C) 2016 Graham Anderson gandersonsw@gmail.com - All Rights Reserved */
package com.graham.appshell.data;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

/**
 * Simple listener that will set the field on a AppData type every time the JTextField is edited.
 * 
 * @author ganderson
 *
 */
public class AppDataFieldDocumentL implements DocumentListener {
	
	final private AppData data;
	final private String fieldName;
	
	public AppDataFieldDocumentL(final AppData dataParam, final String fieldNameParam) {
		data = dataParam;
		fieldName = fieldNameParam;
	}

	@Override
	public void changedUpdate(DocumentEvent arg0) {
		
		setValue(arg0.getDocument());
	}

	@Override
	public void insertUpdate(DocumentEvent arg0) {
		setValue(arg0.getDocument());
	}

	@Override
	public void removeUpdate(DocumentEvent arg0) {
		setValue(arg0.getDocument());
	}
	
	private void setValue(Document doc) {
		try {
			data.setField(fieldName, doc.getText(0, doc.getLength()));
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
