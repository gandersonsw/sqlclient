/* Copyright (C) 2016 Graham Anderson gandersonsw@gmail.com - All Rights Reserved */
package com.graham.graph;

import java.util.List;

/**
 * Graham Graph Node
 *
 */
public interface GgNode {
	
	/**
	 * The name of this node
	 */
	String getName();
	
	String getDescription();

	/**
	 * The number of records at this node
	 */
	int getRecordCount();
	
	/**
	 * @param recordIndex Starts at 0
	 * @return
	 */
	List getRecord(int recordIndex);
	
	/**
	 * @param columnIndex Starts at 0
	 * @param rowIndex Starts at 0
	 * @return true if this field is editable. Call setField to change value.
	 */
	boolean isFieldEditable(int columnIndex, int rowIndex);
	
	/**
	 * Will throw an exception if this field is not editable.
	 * 
	 * @param columnIndex Starts at 0
	 * @param rowIndex Starts at 0
	 * @param value The new value.
	 */
	void setField(int columnIndex, int rowIndex, Object value);
	
	/**
	 * 
	 * @return The number of columns. All rows must have the same number of columns.
	 */
	int getColumnCount();
	
	/**
	 * @param columnIndex Starts at 0.
	 * @return The column name.
	 */
	String getColumnName(int columnIndex);
	
	/**
	 * @return The list of edges to nodes we can get to from here.
	 */
	List<GgEdge> getOutboundEdges();
	
}
