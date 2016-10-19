/* Copyright (C) 2016 Graham Anderson gandersonsw@gmail.com - All Rights Reserved */
package com.graham.sqlclient.ui.databrowser;

import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.AbstractAction;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

public class TableClickWatcher  extends MouseAdapter {
	final private DBContext context;
	
	public TableClickWatcher(final DBContext contextParam) {
		context = contextParam;
	}
	
	public void mouseClicked(MouseEvent evt) {
		if (SwingUtilities.isLeftMouseButton(evt)) {
			if (evt.getClickCount() == 2) {
				int row = context.queryResultsTable.getSelectedRow();
				int col = context.queryResultsTable.getSelectedColumn();
				if (context.jtableModel.isSqlValueEditable(row, col)) {
					new Editor(context.app, new EditorContextFromDataBrowser(context, row, col));
				}
			}
		} else if (SwingUtilities.isRightMouseButton(evt) || evt.isPopupTrigger()) {
			JPopupMenu contextMenu;
			contextMenu = new JPopupMenu();

			int row;
			int col;
			if (context.jtableModel.getVerticalOrientationFlag()) {
				row = context.queryResultsTable.rowAtPoint(evt.getPoint());
				col = 1;
			} else {
				row = 1;
				col = context.queryResultsTable.columnAtPoint(evt.getPoint());
			}
			
			String  editingColumnName = context.jtableModel.getSqlColumnName(row, col);
			if (context.jtableModel.isSqlValueEditable(row, col)) {
				contextMenu.add(new editActionClass("Edit " + editingColumnName, row, col));
			}
			
			for (DataBrowserDefinedRelationship r : context.relshipTracker.getRelationships(editingColumnName)) {
				contextMenu.add(new RelClickedAction(r, context));
			}

			contextMenu.show(evt.getComponent(), evt.getX(), evt.getY());
		}
	}
	
	public class editActionClass extends AbstractAction {
		private static final long serialVersionUID = 1L;
		
		int row2;
		int col2;

		public editActionClass(final String title, final int row2Param, final int col2Param) {
			super(title);
			row2 = row2Param;
			col2 = col2Param;
		}

		public void actionPerformed(ActionEvent e) {
			new Editor(context.app, new EditorContextFromDataBrowser(context, row2, col2));
		}
	}

}
