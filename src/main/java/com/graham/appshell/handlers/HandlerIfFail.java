/* Copyright (C) 2016 Graham Anderson gandersonsw@gmail.com - All Rights Reserved */
package com.graham.appshell.handlers;

public class HandlerIfFail extends ChainedHandler {
	public HandlerIfFail(Handler a, Handler b) {
		main = a;
		branchFail = b;
	}
}
