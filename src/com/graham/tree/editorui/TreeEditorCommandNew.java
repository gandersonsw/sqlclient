/* Copyright (C) 2016 Graham Anderson gandersonsw@gmail.com - All Rights Reserved */
package com.graham.tree.editorui;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;

import com.graham.appshell.data.AppData;

public class TreeEditorCommandNew extends TreeEditorCommand {
	
	AppData type; // type to create

	/**
	 * @param teditParam
	 * @param typeParam
	 * @param nodeParam
	 */
	public TreeEditorCommandNew(
			TreeEditor teditParam, 
			AppData typeParam, 
			TreeNodeForEditorui nodeParam) {
		super("New " + typeParam.getClassId(), teditParam, nodeParam);
		type = typeParam;
	}
	
	public void doCommand() {
		System.out.println("TreeEditorCommandNew.doCommand:" + displayText);
		System.out.println("Node Primary Key=" + node.getValue());
		
		TreeNodeForEditorui n2 = new TreeNodeForEditorui(tedit.callback.getChildTypes(type), type.newInstance(), node.getParent());
		n2.addTailChildIfNeeded();
		
		node.getParent().addChild(n2, node);

		List<JComponent> newUIItems = new ArrayList<JComponent>();
		
		newUIItems.add(tedit.buildHeader(n2));
		
		TreeEditor.buildNodeEditor(newUIItems, n2);
		
		if (!n2.getAllowedChildTypes().isEmpty()) {
			tedit.buildListUI(newUIItems, n2);
		}
		
		int myIndex = tedit.uiItems.indexOf(node.getUIItem());
		System.out.println("index of node in tedit.uiItems: " + myIndex);
		tedit.uiItems.addAll(myIndex, newUIItems);
		
		//node = n2;
		
		tedit.rebuildMainPanel();
		
	/*	System.out.println("--- Tree ---");
		TreeNode root = n2;
		while (root.getParent() != null) {
			root = root.getParent();
		}
		root.printTree(0);
		System.out.println("-------------");
		*/
	}

}
