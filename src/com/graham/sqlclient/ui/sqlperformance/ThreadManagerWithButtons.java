/* Copyright (C) 2016 Graham Anderson gandersonsw@gmail.com - All Rights Reserved */
package com.graham.sqlclient.ui.sqlperformance;

import javax.swing.*;

/**
 * Created by grahamanderson on 5/25/16.
 */
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

	public void updateCompletePercentage(int testIndex, int numberOfTestsToRun, int sqli) {
		if (statusText != null) {
			int max1 = numberOfTestsToRun;
			int current1 = testIndex;
			if (numberOfTestsToRun > 20) {
				max1 = 20;
				current1 = current1 * 20 / numberOfTestsToRun;
			}
			StringBuilder sb = new StringBuilder("running [");
			for (int i = 0; i < max1; i++) {
				if (i > current1) {
					sb.append(' ');
					sb.append(' ');
				} else if (i == current1) {
					sb.append(Integer.toString(sqli+1));
				} else {
					sb.append('*');
				}
			}
			sb.append(']');
			statusText.setText(sb.toString());
		}
	}

	public boolean isRunning() {
		return runFlag;
	}

	public boolean isKillThread() {
		return killThread;
	}

}
