/* Copyright (C) 2016 Graham Anderson gandersonsw@gmail.com - All Rights Reserved */
package com.graham.appshell.find;

import com.graham.appshell.App;
import com.graham.tools.SearchContext;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Created by grahamanderson on 6/21/16.
 */
public class ReplaceAllAction extends FindAction {

	private JTextField replaceText;

	public ReplaceAllAction(App appParam, JComboBox tfParam, JLabel resultsParam, JTextField replaceTextParam) {
		super(appParam, tfParam, resultsParam, "Replace All");
		replaceText = replaceTextParam;
	}

	public void actionPerformed(ActionEvent e) {
		String s = tf.getSelectedItem().toString();

		if (app.getTabManager().getSelectedTabUIPanel() != null) {
			SearchContext params = SearchContext.createReplaceAllContext(s, replaceText.getText());

			//System.out.println("todo caseSensitive startFromBegining" );

			if (app.getTabManager().getSelectedTabUIPanel().findText(params)) {
				results.setText("replaced " + params.getReplaceCount() + " occurences");
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
