/* Copyright (C) 2016 Graham Anderson gandersonsw@gmail.com - All Rights Reserved */
package com.graham.tools;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnPool {
	
	static final String JDBC_DRIVER = "oracle.jdbc.driver.OracleDriver";
	
	private String dbUrl;
	private String userName;
	private String password;
	
	public ConnPool(String dbUrlParam, String userNameParam, String passwordParam) {
		dbUrl = dbUrlParam;
		userName = userNameParam;
		password = passwordParam;
	}
	
	/**
	 * Check that since this instance was created, these values have not changed.  
	 * If they have, mark the existing connections that they should not be used
	 */
	public void checkParamsCurrent(String dbUrlParam, String userNameParam, String passwordParam) {
		dbUrl = dbUrlParam;
		userName = userNameParam;
		password = passwordParam;
	}
	
	public Connection getConn() throws ClassNotFoundException, SQLException {
		Connection dwConn;
		// url should take the form of:
		// jdbc:<subprotocol>:<subname> where subprotocol is the name of the
		// connectivity mechanism used by the driver
		// i.e., "jdbc:oracle:thin:@dc1db11:1584:cdwprd02"
		Class.forName(JDBC_DRIVER);
		dwConn = DriverManager.getConnection(dbUrl, userName, password);
		dwConn.setAutoCommit(false); // turn off auto commit
		return dwConn;
	}
	
	public void releaseConn(Connection conn) throws SQLException {
		conn.close();
	}
	
	public String toString() {
		return dbUrl;
	}

}
