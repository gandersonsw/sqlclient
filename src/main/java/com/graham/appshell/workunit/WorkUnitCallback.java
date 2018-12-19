/* Copyright (C) 2016 Graham Anderson gandersonsw@gmail.com - All Rights Reserved */
package com.graham.appshell.workunit;

public class WorkUnitCallback {
	
	static WorkUnitCallback defaultCallback = new WorkUnitCallback();

	public enum WU_STATE { RUNNING, BLOCKED, SUCCESSFUL_FINISH, ERROR }
	
	public void setStatus(String message, WU_STATE state, Object param1) {
		System.out.println("default callback: " + message);
	}
	
	public void setFinishStatus(String message, WU_STATE state, Object param1) {
		System.out.println("default callback: " + message);
	}
	
	public void dataChanged(Object data, Object param1) {
		System.out.println("default callback - data changed");
	}
}
