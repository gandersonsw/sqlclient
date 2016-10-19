/* Copyright (C) 2016 Graham Anderson gandersonsw@gmail.com - All Rights Reserved */
package com.graham.sqlclient.ui.copydata;

import javax.swing.*;

public class ThreadManagerWithButtons {

	private Action runButton;
	private Action stopButton;
	private JLabel statusText;

	private boolean killThread;
	private boolean runFlag;

	/**
	 * @param runButtonParam required
	 * @param stopButtonParam required
	 * @param statusTextParamoptional
	 */
	public ThreadManagerWithButtons(Action runActionParam, Action stopActionParam, JLabel statusTextParam) {
		runButton = runActionParam;
		stopButton = stopActionParam;
		statusText = statusTextParam;
		stopButton.setEnabled(false);
	}

	public void run() {
		runFlag = true;
		killThread = false;
		runButton.setEnabled(false);
		stopButton.setEnabled(true);
		if (statusText != null) {
			statusText.setText("running");
		}
	}

	public void stop() {
		runFlag = false;
		//runButton.setEnabled(false);
		stopButton.setEnabled(false);
		if (statusText != null) {
			statusText.setText("stopping ... ");
		}
		killThread = true;
	}

	public void killed() {
		runFlag = false;
		killThread = false;
		runButton.setEnabled(true);
		stopButton.setEnabled(false);
		if (statusText != null) {
			statusText.setText("stopped");
		}
	}

	public void completed() {
		runButton.setEnabled(true);
		stopButton.setEnabled(false);
		if (statusText != null) {
			statusText.setText("completed");
		}
		killThread = false;
		runFlag = false;
	}

	public void updateCompletePercentage(int totalInsertsCreated) {
		if (statusText != null) {
			if (totalInsertsCreated < 10 || totalInsertsCreated % 100 == 0) {

				statusText.setText("insert count: " + totalInsertsCreated);
			//	lastInsertCount = totalInsertsCreated;
			}
		}
	}

	public boolean isRunning() {
		return runFlag;
	}

	public boolean isKillThread() {
		return killThread;
	}
}
