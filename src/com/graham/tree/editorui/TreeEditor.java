/* Copyright (C) 2016 Graham Anderson gandersonsw@gmail.com - All Rights Reserved */
package com.graham.tree.editorui;

import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JLabel;

import com.graham.appshell.data.AppData;
import com.graham.appshell.data.DataUI;
import com.graham.tree.TreeNode;

/**
 * Class for creation and management of general purpose editor for hieracical data.
 * 
 * @author ganderson
 *
 */
public class TreeEditor {
	
	int headerID = 1;
	
	TreeEditorCallback callback;
	List<JComponent> uiItems;
	
	/**
	 * The data, kept in sync with structure.
	 */
	TreeNodeForEditorui root;
	
	/**
	 * The panel that contains all UI elements we create. Will be a grid, 1 wide.
	 */
	JPanel mainPanel;

	/**
	 * 
	 */
	public TreeEditor() {
		
	}
	
	/**
	 * External client calls this.
	 * 
	 * @param dataParam Current data to build the UI from, can be an empty list. Note that structure is used directly and not copied. Do not make changes to this after calling buildUI.
	 * @param rootTypes The allowed types in the root of the tree. Note that the tree model allows multiple items in the root.
	 * @param callbackParam Class for inversion of control.
	 * @return
	 */
	public JPanel buildUI(TreeNode rootParam, /*List<TreeNode> dataParam, List<AppData> rootTypes, */ TreeEditorCallback callbackParam) {
		root = checkAndFormatNodes(rootParam);
		root.printTree(0);
		callback = callbackParam;
		uiItems = new ArrayList<JComponent>();
		buildListUI(uiItems, root);
		
		mainPanel = new JPanel(new GridLayout(uiItems.size(),1,3,3));
		for (JComponent c : uiItems) {
			mainPanel.add(c);
		}
		
		return mainPanel;
	}
	
	private static TreeNodeForEditorui checkAndFormatNodes(TreeNode node) {
		TreeNodeForEditorui retNode = new TreeNodeForEditorui(node.getAllowedChildTypes(), node.getValue(), null);
		retNode.addTailChildIfNeeded();
		TreeNodeForEditorui tailNode = retNode.getChild(0);
		for (int i = 0; i < node.getChildrenCount(); i++) {
			TreeNodeForEditorui childNode = checkAndFormatNodes(node.getChild(i));
			childNode.setParent(retNode);
			retNode.addChild(childNode, tailNode);
		}
		
		

		return retNode;
	}
	
	/**
	 * Build the children of a node in the tree. dataParam is the children to build. Will recursively build children of these also.
	 * @param parent The parent, we will build UI for the children only.
	 */
	void buildListUI(List<JComponent> uiItems, TreeNodeForEditorui parent) {
		for (int i = 0; i < parent.getChildrenCount(); i++) {
			TreeNodeForEditorui node = parent.getChild(i);
			uiItems.add(buildHeader(node));
			
			buildNodeEditor(uiItems, node);
			
			if (!node.getAllowedChildTypes().isEmpty()) {
				buildListUI(uiItems, node);
			}
			
		//	currentPath = path + "[" + i + "]";
		}
	}
	
	private static void addIndent(final JPanel panel, final int treeDepth) {
		if (treeDepth > 1) {
			String s = "";
			for (int i = 1; i < treeDepth; i++) {
				s += "    ";
			}
			panel.add(new JLabel(s));
		}
	}
	
	/**
	 * Header is made up of min/max control, action widget, and checkbox with path.
	 */
	JComponent buildHeader(TreeNodeForEditorui node) {
		JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT));
		
		node.setUIItem(header);
		
		addIndent(header, node.getDepth());
		
		header.add(new JLabel("id=" + (headerID++)));
		
		JButton minMaxButton = new JButton("-");
		header.add(minMaxButton);
		
		JComboBox command = new JComboBox();
		command.addItem("Action");
		for (AppData type : node.getParent().getAllowedChildTypes()) {
			TreeEditorCommandNew ncmd = new TreeEditorCommandNew(this, type, node);
			command.addItem(ncmd);
		}
		command.addItem(new TreeEditorCommand("Delete", this, node)); // TODO for last item, this should be disabled
		command.addActionListener(new HeaderMenuListener());
		header.add(command);
		
		JCheckBox cb = new JCheckBox(node.getPath());
		header.add(cb);
		node.setCheckBox(cb);
		
		if (node.getValue() != null) {
			//ChangeListener
			TreePathChangeListener l = new TreePathChangeListener(node);
			node.getValue().addChangeListener(l, node.getValue().getPrimaryKeyFieldNames());
		}
		
		return header;
	}
	
	/**
	 * Build editor for all items in appData type. Will add row to UI for each field of appData. Each row is: indent, label (optional), and editor.
	 */
	static void buildNodeEditor(List<JComponent> uiItems, TreeNode node) {
		if (node.getValue() == null) {
			// this will happen if the node is a place-holder for the last action menu in the list.
			return;
		}
		System.out.println("buildNodeEditor:1");
		for (String fieldName : node.getValue().getFieldNames()) {
			final JPanel editorPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
			
			addIndent(editorPanel, node.getDepth());

			JComponent c = DataUI.getFieldLabel(node.getValue(), fieldName);
			if (c != null) {
				editorPanel.add(c);
			}
			final boolean addListener = node.getValue().getPrimaryKeyFieldNames().contains(fieldName);
			c = DataUI.getFieldEditor(node.getValue(), fieldName, addListener);
			if (c != null) {
				editorPanel.add(c);
			}
			uiItems.add(editorPanel);
		}
	}
	
	/**
	 * Get the data, and will cause all UI elements to be stored in data structure.
	 * @return
	 */
	public TreeNode getData() {
		// TODO read UI elements.
		return root;
	}
	
	/**
	 * After adding-to/deleting-from the uiItems data, call this to refresh the mainPanel UI.
	 */
	void rebuildMainPanel() {
		mainPanel.removeAll();
		mainPanel.setLayout(new GridLayout(uiItems.size(),1,3,3));

		for (JComponent c : uiItems) {
			mainPanel.add(c);
		}
	}

}
