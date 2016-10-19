/* Copyright (C) 2016 Graham Anderson gandersonsw@gmail.com - All Rights Reserved */
package com.graham.appshell.handlers;

import javax.swing.JFrame;

import com.graham.appshell.tabui.AppQuitListener;

public class FloatingFrameQuitListener implements AppQuitListener {

	private final JFrame f;

	public FloatingFrameQuitListener(final JFrame fParam) {
		f = fParam;
	}

	public boolean canQuit() {
		return true;
	}

	public void quiting() {
		f.setVisible(false);
	}

}

