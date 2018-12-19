/* Copyright (C) 2016 Graham Anderson gandersonsw@gmail.com - All Rights Reserved */
package com.graham.appshell.handlers;

import java.awt.event.ActionEvent;

/**
 * This class is not instantiated, use HandlerIfFail, handlerIfSuccess, HandlerIfSuccessElse, or HandlerNext.
 * 
 * Examples:
 * 
 *      new HandlerSuccess(
				handler1,
				handler2).start(event);
This example will first call 1, then call 2 if 1 was a success.
Tree diagram:
1 ->
  -> 2
		
		
		new HandlerIfSuccessElse(
				handler1, 
				handler2, 
				new HandlerSuccess(
						handler3,
						handler4)).start(event);
This example will first call 1. If 1 was a success, it will call 2.  
If 1 was a fail, then it will call 3.  If 3 was a success, it will call 4.
Tree diagram:
1 -> 2
  -> 3 -> 4
       ->


		new HandlerIfSuccessElse(
				h1,
				new HandlerIfSuccessElse(
						h2,
						h4,
						h5),
				new HandlerIfSuccessElse(
						h3,
						h6,
						new HandlerIfSuccessElse(
								h7,
								h8,
								h9))).start(event);
Tree diagram:
1 -> 2 -> 4
       -> 5
  -> 3 -> 6
       -> 7 -> 8
            -> 9
 * 
 * 
 * @author ganderson
 *
 */
public class ChainedHandler implements Handler {

	static ChainedHandler endOfChain = new ChainedHandler();
	Handler main, branchSuccess, branchFail;
	
	ChainedHandler() {
		
	}

	public void next(ActionEvent e, int previousResult) {
		if (previousResult == Handler.RESULT_SUCCESS) {
			if (branchSuccess != null)
				branchSuccess.handleEvent(e, endOfChain);
		} else if (previousResult == Handler.RESULT_FAIL) {
			if (branchFail != null)
				branchFail.handleEvent(e, endOfChain);
		}
	}

	/**
	 * this call starts it all off
	 * @param e
	 */
	public void start(ActionEvent e) {
		main.handleEvent(e, this);
	}

	@Override
	public void handleEvent(ActionEvent e, ChainedHandler nextHandler) {
		System.out.println("reached end of chain");
	}
}
