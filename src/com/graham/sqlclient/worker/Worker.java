/* Copyright (C) 2016 Graham Anderson gandersonsw@gmail.com - All Rights Reserved */
package com.graham.sqlclient.worker;

public interface Worker extends Runnable {
	public WorkerInbox getWorkerInbox();
	public WorkerOutboxList getWorkerOutbox();
	public void stop();
}
