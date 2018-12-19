package com.graham.graph.test1;

import java.util.ArrayList;
import java.util.List;

import com.graham.graph.GgEdge;
import com.graham.graph.GgNode;

public class TestANode implements GgNode {

	public int getColumnCount() {
		return 2;
	}

	public String getColumnName(int columnIndex) {
		if (columnIndex == 1) {
			return "ID";
		}
		return "City";
	}

	public String getDescription() {
		return "List of citys";
	}

	public String getName() {
		return "City";
	}

	public List<GgEdge> getOutboundEdges() {
		List a = new ArrayList<GgEdge>();
		a.add(new TestAEdge());
		return a;

	}

	public List getRecord(int recordIndex) {
		List a = new ArrayList();
		if (recordIndex == 0) {
			a.add("101");
			a.add("Minneapolis");
		} else {
			a.add("102");
			a.add("St Paul");
		}
		return a;
	}

	@Override
	public int getRecordCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isFieldEditable(int columnIndex, int rowIndex) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setField(int columnIndex, int rowIndex, Object value) {
		// TODO Auto-generated method stub
		
	}

}
