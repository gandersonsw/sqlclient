/* Copyright (C) 2016 Graham Anderson gandersonsw@gmail.com - All Rights Reserved */
package com.graham.sqlclient.ui.sql;

import com.graham.tools.SqlInfo;
import com.graham.tools.SqlTools;
import com.graham.tools.UITools;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Created by grahamanderson on 6/16/16.
 *
 * Manage everything about auto completing SQL in a JTextArea
 */
public class SqlAutoCompleteManager {

	private JTextArea sqlTextEditor;
	private JTable autoCompleteTable;
	private AbstractAction selectAutoCompleteTab;

	private autoCompleteThread currentAutoCompleteThread;
	private SQLAutoCompleteTableModel model;
	private boolean ignoreChanges;

	SqlAutoCompleteManager(JTextArea sqlTextEditorParam, JTable autoCompleteTableParam, SQLAutoCompleteTableModel modelParam, AbstractAction selectAutoCompleteTabParam) {
		sqlTextEditor = sqlTextEditorParam;
		autoCompleteTable = autoCompleteTableParam;
		model = modelParam;
		selectAutoCompleteTab = selectAutoCompleteTabParam;

		sqlTextEditor.getDocument().addDocumentListener(new queryChangeListener());
		sqlTextEditor.addKeyListener(new sqlTextAreaKeyListener());

		autoCompleteTable.addMouseListener(new AutoCompleteTableMouseApapter());
	}

	public void setIgnoreChanges(boolean ignoreChangesParam) {
		ignoreChanges = ignoreChangesParam;
	}

	public class sqlTextAreaKeyListener implements KeyListener {
		@Override
		public void keyTyped(KeyEvent e) {
		}
		@Override
		public void keyPressed(KeyEvent e){
			if(e.getKeyCode() == KeyEvent.VK_TAB && autoCompleteTable.getRowCount() > 0) {
				int sr = autoCompleteTable.getSelectedRow();
				int newSelectedRow;
				if (sr == -1) {
					newSelectedRow = 0;
				} else {
					if (e.isShiftDown()) {
						newSelectedRow = sr - 1;
						if (newSelectedRow < 0) {
							newSelectedRow = autoCompleteTable.getRowCount() - 1;
						}
					} else {
						newSelectedRow = sr + 1;
						if (newSelectedRow >= autoCompleteTable.getRowCount()) {
							newSelectedRow = 0;
						}
					}
				}
				autoCompleteTable.setRowSelectionInterval(newSelectedRow, newSelectedRow);
				String txt = (String)model.getValueAt(autoCompleteTable.getSelectedRow(), 0);
				UITools.SubString ss = model.getCurrentTokenToReplace();

				// if there is no token, don't use the tab key, incase they want to do a tab. they can still double click though
			//	if (ss.s.length() > 0) {
					ignoreChanges = true;
					//	int sstart = sqlTextEditor.getSelectionStart();
					sqlTextEditor.replaceRange(txt, ss.startIndex, ss.endIndex);
					ss.s = txt;
					ss.endIndex = ss.startIndex + txt.length();
					ignoreChanges = false;
					model.insertTokenForTab(ss);
					e.consume();
			//	}

			}
		}
		@Override
		public void keyReleased(KeyEvent e) {
		}
	}


	public class queryChangeListener implements DocumentListener {
		@Override
		public void changedUpdate(DocumentEvent arg0) {
			checkChange(false);
		}
		@Override
		public void insertUpdate(DocumentEvent e) {
			checkChange(false);
		}
		@Override
		public void removeUpdate(DocumentEvent e) {
			checkChange(e.getLength() == 1);
		}
		public void checkChange(boolean delete1) {
			if (ignoreChanges) {
				return;
			}
			String currentSql = UITools.getSelectedTextOrBlockAtInsert(sqlTextEditor, delete1);
			UITools.SubString currentToken = UITools.getTokenAtInsert(sqlTextEditor, delete1);
			if (currentAutoCompleteThread == null) {
				currentAutoCompleteThread = new autoCompleteThread();
				currentAutoCompleteThread.setLast(currentSql, currentToken);
				new Thread(currentAutoCompleteThread).start();
			} else {
				currentAutoCompleteThread.setLast(currentSql, currentToken);
			}
		}
	}


	public class autoCompleteThread implements Runnable {
		String currentSql;
		UITools.SubString currentToken;
		String lastSetSql;
		UITools.SubString lastSetToken;
		@Override
		public void run() {
			while (loadCurrentData()) {
				SqlInfo info = SqlTools.parseSQL(currentSql);
				if (model.dataChanged(info, currentToken)) {
					selectAutoCompleteTab.actionPerformed(null);
				}
			}
			currentAutoCompleteThread = null;
		}
		public synchronized boolean loadCurrentData() {
			if (lastSetSql != null) {
				currentSql = lastSetSql;
				currentToken = lastSetToken;
				lastSetSql = null;
				lastSetToken = null;
				return true;
			} else {
				return false;
			}
		}
		public synchronized void setLast(String sqlParam, UITools.SubString tokenParam) {
			lastSetSql = sqlParam;
			lastSetToken = tokenParam;
		}
	}


	class AutoCompleteTableMouseApapter extends MouseAdapter {
		public AutoCompleteTableMouseApapter() {
		}
		public void mouseClicked(MouseEvent evt) {
			if (evt.getClickCount() == 2) {
				String txt = (String)model.getValueAt(autoCompleteTable.getSelectedRow(), 0);
				sqlTextEditor.replaceRange(txt, model.getCurrentTokenToReplace().startIndex, model.getCurrentTokenToReplace().endIndex);
			}
		}
	}
}
