/* Copyright (C) 2016 Graham Anderson gandersonsw@gmail.com - All Rights Reserved */
package com.graham.graph;

import java.util.List;

/**
 * Note that there is only one instance of an edge. The may be many instances of nodes, but 
 * 
 * Graham Graph Edge
 *
 */
public interface GgEdge {

	/**
	 * @return 0 to 100. This will be the same for all nodes.
	 */
	int getCost();
	
	/**
	 * For example "LineItems"
	 * @return
	 */
	String getName();
	
	String getDescription();
	
	/**
	 * 
	 * @param startNode The node we are currently at.
	 * @return The node we are trying to get too. If the edge is one-way, this may throw an exception.
	 */
	GgNode getNode(GgNode startNode);
	
	/**
	 * This allows a sub-set of the records in the start node to be accessed.
	 * 
	 * @param startNode
	 * @param rowIndexes The indexes of the rows we want to find data for.
	 * @return
	 */
	GgNode getNode(GgNode startNode, List<Integer> rowIndexes);
	
}
