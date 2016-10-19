/* Copyright (C) 2016 Graham Anderson gandersonsw@gmail.com - All Rights Reserved */
package com.graham.appshell.handlers;

/**
 * If first action is success, start action b
 * @author ganderson
 */
public class HandlerIfSuccess extends ChainedHandler {
	public HandlerIfSuccess(Handler a, Handler b) {
		main = a;
		branchSuccess = b;
	}
}
