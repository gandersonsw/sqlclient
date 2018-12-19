/* Copyright (C) 2016 Graham Anderson gandersonsw@gmail.com - All Rights Reserved */
package com.graham.sqlclient.ui.sql.iterate;

import java.sql.*;

/**
 *
 * Work Unit SQL Iterator
 *
 * Created by grahamanderson on 9/18/15.
 */
public interface WusIter {

	/**
	 * Run once before iteration starts
	 *
	 */
	void start(WusIterContext context) throws SQLException;

	/**
	 * Run before "next" is called. Will always be called at least once
	 */
	void beforeNext(WusIterContext context) throws SQLException;

	/**
	 * Test if there is an item for an iteration loop
	 */
	boolean next(WusIterContext context) throws SQLException;

	/**
	 * Will only be called if "next" returns true
	 */
	void afterNext(WusIterContext context) throws SQLException;

	/**
	 * Run once after iteration is done.
	 */
	void end(WusIterContext context) throws SQLException;

	/**
	 * Run the entire iteration loop.
	 */
	void run(WusIterContext context) throws SQLException;

}
