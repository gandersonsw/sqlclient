/* Copyright (C) 2016 Graham Anderson gandersonsw@gmail.com - All Rights Reserved */
package com.graham.tree;

import java.util.ArrayList;
import java.util.List;

import com.graham.appshell.data.AppData;

public class TreeNode {
	
	private List<TreeNode> children = new ArrayList<TreeNode>();
	private AppData value; // this can be null. which means this is the last item in the list of the parents children. it is used as a place-holder for actions that work on the end of the list
	private TreeNode parent; // the only node with this null is root. There is 1 root, however, it is not represented in the UI. It just exists to contain the root level items.
	private int depth;
	private List<AppData> allowedChildTypes;
	private String path;
	
	/**
	 * Construct a root node.
	 * 
	 * @param allowedChildTypesParam
	 */
	public TreeNode(final List<AppData> allowedChildTypesParam) {
		allowedChildTypes = allowedChildTypesParam;
	}
	
	/**
	 * Construct a non-root node.
	 * 
	 * @param valueParam Pass null if this is a tail node.
	 * @param parentParam
	 */
	public TreeNode(final List<AppData> allowedChildTypesParam, final AppData valueParam, final TreeNode parentParam) {
		allowedChildTypes = allowedChildTypesParam;
		value = valueParam;
		parent = parentParam;
		depth = computeDepth(this);
		path = computePath(this);
	}
	
	static private int computeDepth(final TreeNode n) {
		if (n.parent == null) {
			return 0;
		} else {
			return 1 + computeDepth(n.parent);
		}
	}
	
	
	// TODO not tested
	static private String computePath(final TreeNode n) {
		if (n.parent == null) {
			return "";
		} else {
			if (n.parent.path == null) {
				n.parent.path = computePath(n.parent);
			}
			if (n.value == null) {
				return n.parent.path;
				//return computePath(n.parent);
			} else {
				return n.parent.path + "/" + n.value.getPrimaryKey();
				//return computePath(n.parent) + "/" + n.value.getPrimaryKey();
			}
		}
	}
	
	/**
	 * 
	 * 
	 * @param newChild New node to add to <this> children.
	 * @param addedBy The node it should be added after. Required.
	 */
	public void addChild(final TreeNode newChild, final TreeNode addedBy) {
		final int i = children.indexOf(addedBy);
		if (i == -1) {
			throw new IllegalArgumentException("addedBy node must be in the children list!");
		}
		children.add(i, newChild);
	}
	
	public TreeNode getParent() {
		return parent;
	}
	
	public void setParent(TreeNode parentParam) {
		if (parent != null) {
			throw new RuntimeException("Parent already set");
		}
		parent = parentParam;
	}

	public int getChildrenCount() {
		return children.size();
	}
	
	public TreeNode getChild(final int i) {
		return children.get(i);
	}
	
	public AppData getValue() {
		return value;
	}
	
	public int getDepth() {
		return depth;
	}
	
	public List<AppData> getAllowedChildTypes() {
		return allowedChildTypes;
	}
	
	public String getPath() {
		return path;
	}
	
	public void recomputePath() {
		path = computePath(this);
	}
	
	public void addTailChildIfNeeded() {
		if (getChildrenCount() == 0) {
			if (!allowedChildTypes.isEmpty()) {
				final TreeNode tailNode = createTailNode(allowedChildTypes);
				children.add(tailNode);
			}
		} else if (!isTailChildExist()) {
			final TreeNode tailNode = createTailNode(getChild(0).allowedChildTypes);
			children.add(tailNode);
		}
	}
	
	protected TreeNode createTailNode(final List<AppData> types) {
		return new TreeNode(types, null, this);
	}
	
	private boolean isTailChildExist() {
		return getChild(getChildrenCount()).getValue() == null;
		
	}
	public void printTree(int level) {
		String s = "";
		for (int k = 0; k < level; k++) {
			s = s + "   ";
		}
		if (value == null) {
			System.out.println(s + "[TAIL]");
		} else {
			System.out.println(s + value.getPrimaryKey());
		}
		for (int i = 0; i < getChildrenCount(); i++) {
			getChild(i).printTree(level + 1);
		}
	}

}
