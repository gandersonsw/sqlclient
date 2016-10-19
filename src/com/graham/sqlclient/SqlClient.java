/* Copyright (C) 2016 Graham Anderson gandersonsw@gmail.com - All Rights Reserved */
package com.graham.sqlclient;

import com.graham.appshell.App;
import com.graham.appshell.workspace.WorkSpaceDirFrame;

public class SqlClient {
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {

		SqlClientApp sqlApp = new SqlClientApp();
		if (App.getActiveWorkspaceStatic(sqlApp) == null) {
			String userHome = System.getProperty("user.home");
			String appWorkpathDefault = userHome + System.getProperty("file.separator") + "SqlClientWorkSpace";
			new WorkSpaceDirFrame(
					appWorkpathDefault + System.getProperty("file.separator") + "Work1",
				sqlApp,
					appWorkpathDefault);
			// WorkSpaceDirOk will call App.init
		} else {
			new App().init(sqlApp, null);
		}
	}
}
