/* Copyright (C) 2016 Graham Anderson gandersonsw@gmail.com - All Rights Reserved */
package com.graham.tree.editorui;

import javax.swing.JCheckBox;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class TreePathChangeListener implements ChangeListener {
	
	final TreeNodeForEditorui myNode;

	
	public TreePathChangeListener(final TreeNodeForEditorui nParam) {
		myNode = nParam;
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		visitNode(myNode);
	}
	
	private void visitNode(TreeNodeForEditorui n) {
		n.recomputePath();
		n.getCheckBox().setText(n.getPath());
		for (int i = 0; i < n.getChildrenCount(); i++) {
			visitNode(n.getChild(i));
		}
	}

}
