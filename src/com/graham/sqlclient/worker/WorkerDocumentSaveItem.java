/* Copyright (C) 2016 Graham Anderson gandersonsw@gmail.com - All Rights Reserved */
package com.graham.sqlclient.worker;

import java.io.Serializable;
import java.util.HashMap;

public class WorkerDocumentSaveItem implements Serializable {
	private static final long serialVersionUID = 1L;
	public String workerName;
	public HashMap<String, Object> params;
}