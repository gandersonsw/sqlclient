/* Copyright (C) 2016 Graham Anderson gandersonsw@gmail.com - All Rights Reserved */
package com.graham.sqlclient.worker;

import java.util.ArrayList;
import java.util.List;

public class WorkerOutboxListImpl implements WorkerOutboxList {
	
	private List<List> data = new ArrayList<List>();
	private int totalCountSoFar = 0;
	private int nextIndex = 0;
	private int totalCount = -1; // this will not be assigned until the getter thread is done
	private WorkerChangeCallback wcc;
	private Worker nextWorker; // the one who consume this outbox - may be null if we are the last
	boolean stopFlag;

	@Override
	public List getItemByIndex(int index) {
		synchronized (data) {
			return data.get(index);
		}
	}

	@Override
	public int getTotalCountSoFar() {
		synchronized (data) {
			return totalCountSoFar;
		}
	}

	@Override
	public boolean hasNext() {
		try {
		while (!stopFlag) {
			synchronized (data) {
				if (nextIndex < totalCountSoFar) {
					return true;
				}
				if (totalCount != -1 && nextIndex >= totalCount) { // greater than in case next is called 2 times ???
					return false;
				}
			}
			try {
				synchronized (this) { // asdf
					wait(1000); // todo this number is arbitrary
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return false;
		} catch (Exception e2) {
			error("hasNext error", e2, false);
			return false;
		}
	}

	@Override
	public List next() {
		synchronized (data) {
			nextIndex++;
			return data.get(nextIndex - 1);
		}
	}
	
	void add(List<Object> row, Worker w) {
		synchronized (data) {
			data.add(row);
			totalCountSoFar++;
			if (wcc != null) {
				wcc.oneNewRowAdded(totalCountSoFar - 1);
			}
		}

		if (nextWorker != null) {
			boolean notFlushed = true;
			while (totalCountSoFar > nextIndex + 1000 && !stopFlag) { // if we are way far ahead, pause and let the inbox worker catch up
				if (notFlushed) {
					notFlushed = false;
					if (wcc != null) {
						wcc.flush();
					}
				}
				try {
					synchronized (w) {
						w.wait(1000); // todo this number is arbitrary
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	void done() {
		synchronized (data) {
			totalCount = totalCountSoFar;
			if (wcc != null) {
				wcc.done();
			}
		}
	}
	
	void reset() {
		synchronized (data) {
			data = new ArrayList<List>();
			totalCountSoFar = 0;
			nextIndex = 0;
			totalCount = -1;
			if (wcc != null) {
				wcc.reset();
			}
		}
	}
	
	void error(String notes, Exception e, boolean fatal) {
		if (wcc != null) {
			wcc.error(notes, e, fatal);
		}
	}

	@Override
	public void setChangeCallback(WorkerChangeCallback wccParam) {
		wcc = wccParam;
	}

	@Override
	public Worker getNextWorker() {
		return nextWorker;
	}

	@Override
	public void setNextWorker(Worker w) {
		nextWorker = w;
		((WorkerInboxList)w.getWorkerInbox()).setSource(this);
	}
	
}