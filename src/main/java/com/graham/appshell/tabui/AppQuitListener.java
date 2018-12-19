/* Copyright (C) 2016 Graham Anderson gandersonsw@gmail.com - All Rights Reserved */
package com.graham.appshell.tabui;

public interface AppQuitListener {
	/**
	 * this is called first.  if everyone says it is okay to quit, we call "quiting"
	 * @return
	 */
	boolean canQuit();

	/**
	 * Last chance to do stuff before app terminates.
	 */
	void quiting();
}
