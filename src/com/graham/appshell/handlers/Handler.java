/* Copyright (C) 2016 Graham Anderson gandersonsw@gmail.com - All Rights Reserved */
package com.graham.appshell.handlers;

import java.awt.event.ActionEvent;

public interface Handler {
	int NO_RESULT = 0; // this indicates it the first in the chain
	int RESULT_SUCCESS = 1;
	int RESULT_FAIL = 2;
	
	void handleEvent(ActionEvent e, ChainedHandler nextHandler);

}
