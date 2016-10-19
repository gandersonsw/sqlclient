/* Copyright (C) 2016 Graham Anderson gandersonsw@gmail.com - All Rights Reserved */
package com.graham.tree.editorui;

import java.util.List;

import javax.swing.JCheckBox;
import javax.swing.JComponent;

import com.graham.appshell.data.AppData;
import com.graham.tree.TreeNode;

public class TreeNodeForEditorui extends TreeNode {
	
	private JComponent itemThatContainsMeInUIItemsList; // Will be null until UI is constructed. After that, never should be null. The item that contains the header for this tree item.
	private JCheckBox cb;
	
	public TreeNodeForEditorui(List<AppData> allowedChildTypesParam) {
		super(allowedChildTypesParam);
	}
	
	public TreeNodeForEditorui(final List<AppData> allowedChildTypesParam, final AppData valueParam, final TreeNode parentParam) {
		super(allowedChildTypesParam,valueParam,parentParam);
	}
	
	public void setUIItem(final JComponent c) {
		itemThatContainsMeInUIItemsList = c;
	}
	
	public JComponent getUIItem() {
		return itemThatContainsMeInUIItemsList;
	}
	
	public JCheckBox getCheckBox() {
		return cb;
	}
	
	public void setCheckBox(final JCheckBox cbParam) {
		cb = cbParam;
	}
	
	@Override
	public TreeNodeForEditorui getChild(final int i) {
		return (TreeNodeForEditorui)super.getChild(i);
	}
	
	@Override
	protected TreeNode createTailNode(final List<AppData> types) {
		return new TreeNodeForEditorui(types, null, this);
	}

}
