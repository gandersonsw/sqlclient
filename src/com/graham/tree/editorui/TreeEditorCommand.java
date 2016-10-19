/* Copyright (C) 2016 Graham Anderson gandersonsw@gmail.com - All Rights Reserved */
package com.graham.tree.editorui;

public class TreeEditorCommand {
	
	String displayText;
	TreeEditor tedit;
	TreeNodeForEditorui node;

	public TreeEditorCommand(final String displayTextParam, final TreeEditor teditParam, final TreeNodeForEditorui nodeParam) {
		displayText = displayTextParam;
		tedit = teditParam;
		node = nodeParam;
	}
	
	public void doCommand() {
		// This should not be called, should be overridden
		System.out.println("doCommand:" + displayText);
	}
	
	@Override
	public String toString() {
		return displayText;
	}
	
}
