/* Copyright (C) 2016 Graham Anderson gandersonsw@gmail.com - All Rights Reserved */
package com.graham.appshell.workunit;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.*;

import com.graham.appshell.workunit.WorkUnitCallback.WU_STATE;

public abstract class WorkUnit implements Runnable {
	
	private Date originalStartTime; // when the job was first created and started
	
	private long executionTotalTimeSoFar; // if the job has been stopped and started , or paused, the total time not including the last continuous run
	private Date lastStartTime; // if the job has been stopped and started , or paused, the last resume time
	
	private List<Action> disabledWhileRunning = new ArrayList<>();
	private List<Action> enabledWhileRunning = new ArrayList<>();
	private int percentDone;
	
	protected Thread thread;
	
	private long lastUpdateOutput;
	private static long UPDATE_TIMEOUT = 300;
	
	private boolean cancelWorkUnitFlag = false;
	
	public WorkUnitCallback callbacks; // TODO make this private
	
	public void setCallbacks(WorkUnitCallback c) {
		callbacks = c;
	}
	
	/**
	 * 
	 * @return true if enough time has elapsed since the last time the outputs have been updates to reflect change in processing progress
	 */
	public boolean shouldUpdateOutput() {
		long now = System.currentTimeMillis();

		if (lastUpdateOutput + UPDATE_TIMEOUT < now) {
			lastUpdateOutput = now;
			return true;
		}
		
		return false;
	}
	
	public void cancelWorkUnit() {
		cancelWorkUnitFlag = true;
		cancelWorkUnitInternal();
		//ending();
	}

	public boolean isWorkUnitCancelled() {
		return cancelWorkUnitFlag;
	}

	public void resetCancel() {
		cancelWorkUnitFlag = false;
	}
	
	/**
	 * sub classes should override to if they have additional stuff to do
	 */
	public void cancelWorkUnitInternal() {
		
	}
	
	/**
	 * 
	 * @return true is this can be paused, application and be exited, and then work can be resumed from where to was paused
	 */
	public boolean isPausable() {
		return false;
	}
	
	/**
	 * this is not guaranteed to stop job right away
	 */
	//public abstract void stop();
	
	public void startWU() {
		cancelWorkUnitFlag = false;
		thread = new Thread(this);
		thread.start();
	}
	
	public void starting(String message, Object param1) {
		callbacks.setStatus(message, WorkUnitCallback.WU_STATE.RUNNING, param1);
		for (Action c : disabledWhileRunning) {
			c.setEnabled(false);
		}
		for (Action c : enabledWhileRunning) {
			c.setEnabled(true);
		}
		percentDone = 0;
		originalStartTime = new Date();
	}
	
	public void ending(String message, WU_STATE state, Object param1) {
		callbacks.setFinishStatus(message, state, param1);
		for (Action c : disabledWhileRunning) {
			c.setEnabled(true);
		}
		for (Action c : enabledWhileRunning) {
			c.setEnabled(false);
		}
		percentDone = 100;
	}
	
	public void pause() {
		throw new IllegalArgumentException("this unit is not pausible");
	}
	
	public int getPercentDone() {
		return percentDone;
	}
	
	public void addDisabledWhileRunning(Action item) {
		disabledWhileRunning.add(item);
	}
	
	public void addEnabledWhileRUnning(Action item) {
		enabledWhileRunning.add(item);
	}
}
