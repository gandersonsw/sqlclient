/* Copyright (C) 2016 Graham Anderson gandersonsw@gmail.com - All Rights Reserved */
package com.graham.appshell.find;

import javax.swing.JComboBox;
import javax.swing.JLabel;

import com.graham.appshell.App;

public class FindNextAction extends FindAction {
    private static final long serialVersionUID = 1L;
    
    public FindNextAction(App appParam, JComboBox tfParam, JLabel resultsParam) {
		super(appParam, tfParam, resultsParam, "Find Next");
	}

	boolean findFromStart() {
		return false;
	}

}