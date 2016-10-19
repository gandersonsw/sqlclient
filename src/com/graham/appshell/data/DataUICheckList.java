/* Copyright (C) 2016 Graham Anderson gandersonsw@gmail.com - All Rights Reserved */
package com.graham.appshell.data;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 * for creating a list of check boxes for inclusion of data items
 *
 */
public class DataUICheckList {
	
	private DataManagerList dm;
	private HashMap<String, JCheckBox> cbs;

	public DataUICheckList(DataManagerList dmParam) {
		dm = dmParam;
	}
	
	public JScrollPane initUICheckList(List<String> keyList) {
		
		JPanel uiList = new JPanel(new GridLayout(dm.getList().size(), 1, 4, 4));
		
		cbs = new HashMap<String, JCheckBox>();
		for (AppData d : dm.getList()) {
			String k = d.getPrimaryKey();
			JCheckBox cb = new JCheckBox(d.toString());
			cb.setSelected(keyList.contains(k));
			uiList.add(cb);
			cbs.put(k, cb);
		}
		
		JScrollPane sp = new JScrollPane(uiList);
		sp.setPreferredSize(new Dimension(100, 100));
		
		return sp;
	}
	
	public List<String> getCheckedKeyList() {
		ArrayList<String> lst = new ArrayList<String>();
		for (String k : cbs.keySet()) {
			if (cbs.get(k).isSelected()) {
				lst.add(k);
			}
		}
		return lst;
	}
	
}
