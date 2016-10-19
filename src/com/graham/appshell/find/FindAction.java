/* Copyright (C) 2016 Graham Anderson gandersonsw@gmail.com - All Rights Reserved */
package com.graham.appshell.find;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JComboBox;
import javax.swing.JLabel;

import com.graham.appshell.App;
import com.graham.tools.SearchContext;

public class FindAction extends AbstractAction {
    private static final long serialVersionUID = 1L;

	App app;
    JComboBox tf;
    JLabel results;
    
    public FindAction(App appParam, JComboBox tfParam, JLabel resultsParam) {
		this(appParam, tfParam, resultsParam, "Find");
	}

	protected FindAction(App appParam, JComboBox tfParam, JLabel resultsParam, String actionName) {
		super(actionName);
		app = appParam;
		tf = tfParam;
		results = resultsParam;
	}

	boolean findFromStart() {
		return true;
	}
    
	public void actionPerformed(ActionEvent e) {
		String s = tf.getSelectedItem().toString();
		
		if (app.getTabManager().getSelectedTabUIPanel() != null) {
			SearchContext params = SearchContext.createSearchContext(s, findFromStart());

			//System.out.println("todo caseSensitive startFromBegining" );

			if (app.getTabManager().getSelectedTabUIPanel().findText(params)) {
				results.setText("found");
			} else {
				results.setText("no matching text found");
			}
		}

		boolean noMatch = true;
		for (int i = 0; i < tf.getItemCount(); i++) {
			if (tf.getItemAt(i).equals(tf.getSelectedItem())) {
				noMatch = false;
			}
		}
		
		if (noMatch) {
			tf.insertItemAt(tf.getSelectedItem(), 0);
		}
	}
}