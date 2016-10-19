package com.graham.graph.test1;

import java.util.List;

import com.graham.graph.GgEdge;
import com.graham.graph.GgNode;

public class TestAEdge implements GgEdge {

	public int getCost() {
		return 1;
	}

	public String getDescription() {
		return "Test a DESC";
	}

	public String getName() {
		return "A";
	}

	public GgNode getNode(GgNode startNode) {
		// TODO Auto-generated method stub
		return null;
	}

	public GgNode getNode(GgNode startNode, List<Integer> rowIndexes) {
		// TODO Auto-generated method stub
		return null;
	}

}
