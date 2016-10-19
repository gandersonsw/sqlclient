/* Copyright (C) 2016 Graham Anderson gandersonsw@gmail.com - All Rights Reserved */
package com.graham.sqlclient.worker;

public interface WorkerInboxList extends WorkerInbox {

	/**
	 * connect this worker to another workers outbox
	 * @param outboxParam
	 */
	void setSource(WorkerOutboxList outboxParam);
}
