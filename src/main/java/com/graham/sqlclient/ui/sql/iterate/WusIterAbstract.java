/* Copyright (C) 2016 Graham Anderson gandersonsw@gmail.com - All Rights Reserved */
package com.graham.sqlclient.ui.sql.iterate;

import java.sql.SQLException;

/**
 * Created by grahamanderson on 9/18/15.
 */
abstract public class WusIterAbstract implements WusIter {
	@Override
	public void start(WusIterContext context) throws SQLException {

	}

	@Override
	public void beforeNext(WusIterContext context) throws SQLException {

	}

	@Override
	public void afterNext(WusIterContext context) throws SQLException {

	}

	@Override
	public void end(WusIterContext context) throws SQLException {

	}

	@Override
	public void run(WusIterContext context) throws SQLException {
		boolean running = true;
		start(context);
		while (running) {
			beforeNext(context);
			running = next(context);
			if (running) {
				afterNext(context);
			}
		}
		end(context);
	}
}
