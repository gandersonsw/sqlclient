/* Copyright (C) 2016 Graham Anderson gandersonsw@gmail.com - All Rights Reserved */
package com.graham.appshell.handlers;

public class HandlerIfSuccessElse extends ChainedHandler {
	public HandlerIfSuccessElse(Handler a, Handler b, Handler c) {
		main = a;
		branchSuccess = b;
		branchFail = c;
	}
}
