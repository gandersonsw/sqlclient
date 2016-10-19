/* Copyright (C) 2016 Graham Anderson gandersonsw@gmail.com - All Rights Reserved */
package com.graham.sqlclient;

import java.io.Serializable;

/**
 * Work Space Data Base
 * 
 * @author ganderson
 *
 */
public class WSDB implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public String databaseUrl; // the primary database
	public String databaseUserName; // if null, use the same as primary db
	public String databasePassword; // if null, use the same as primary db
	public String group;
}
