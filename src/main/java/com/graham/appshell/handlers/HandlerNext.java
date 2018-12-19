/* Copyright (C) 2016 Graham Anderson gandersonsw@gmail.com - All Rights Reserved */
package com.graham.appshell.handlers;

public class HandlerNext extends ChainedHandler {
	public HandlerNext(Handler a, Handler b) {
		main = a;
		branchSuccess = b;
		branchFail = b;
	}
}
