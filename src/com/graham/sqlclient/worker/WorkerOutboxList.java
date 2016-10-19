/* Copyright (C) 2016 Graham Anderson gandersonsw@gmail.com - All Rights Reserved */
package com.graham.sqlclient.worker;

import java.util.List;

public interface WorkerOutboxList extends WorkerOutbox {

	/**
	 * This will block the thread if there might be another one, but it is not ready yet
	 * @return true if there is another item for processing
	 */
	boolean hasNext();
	
	/**
	 * @return the next item to process.
	 */
	List next();
	
	/**
	 * @return the total we have retrieved so far.  Not the same as the number of times next was called, and not the same as the total number that next could be called
	 */
	int getTotalCountSoFar();
	
	/**
	 * @param index should be in the range 0 to (getTotalCountSoFar()-1)
	 * @return
	 */
	List getItemByIndex(int index);
	
	void setChangeCallback(WorkerChangeCallback wcc);
	
	/**
	 * @return next worker in the chain - or null if this is the last
	 */
	Worker getNextWorker();
	
	void setNextWorker(Worker w);
	
}
