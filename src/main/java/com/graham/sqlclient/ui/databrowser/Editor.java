/* Copyright (C) 2016 Graham Anderson gandersonsw@gmail.com - All Rights Reserved */
package com.graham.sqlclient.ui.databrowser;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.sql.SQLException;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.graham.sqlclient.SqlClientApp;
import java.sql.Clob;

import com.graham.appshell.handlers.FloatingFrameQuitListener;
import com.graham.appshell.handlers.SysWindow2;
import com.graham.tools.UITools;
import com.graham.tools.SqlTools;

public class Editor {
	SqlClientApp app;
	private JTextArea sqlOutput;
	private JTextArea stringFieldEditor;
	private EditorContext context;
	private JFrame editor;
	private String editingColumnName;
	private String idVal;
	private boolean isClob;
	private boolean isBigDec;
	private boolean isDate;

	public Editor(SqlClientApp appParam, EditorContext contextParam) {
		app = appParam;
		context = contextParam;

		Object obj = context.getEditingObject();

		editingColumnName = context.getColumnName();
		idVal = context.getId();

		editor = new JFrame();
		editor.setTitle(context.getTableName() + "[" + idVal + "] " + editingColumnName); // TODO
		cancelActionClass cac = new cancelActionClass();
		editor.addWindowListener(new SysWindow2(cac));

		JPanel bordered = new JPanel(new BorderLayout(4,4));
		JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		buttons.add(new JButton(new saveActionClass()));
		buttons.add(new JButton(cac));
		bordered.add(buttons, BorderLayout.SOUTH);
		JPanel bordered2 = new JPanel(new BorderLayout());
		bordered2.add(bordered, BorderLayout.CENTER);
		sqlOutput = new JTextArea(4, 40);
		Font font = new Font("Courier New", Font.PLAIN, 12);
		sqlOutput.setFont(font);
		sqlOutput.setEditable(false);
		bordered2.add(new JScrollPane(sqlOutput), BorderLayout.SOUTH);
		editor.setContentPane(bordered2);

		String notes = "";
		if (obj == null || obj instanceof String) {
			stringFieldEditor = new JTextArea(obj == null ? "" : (String)obj, 6, 40);
			JScrollPane sp = new JScrollPane(stringFieldEditor);
			bordered.add(sp, BorderLayout.CENTER);
			stringFieldEditor.getDocument().addDocumentListener(new textChangeListener());
			notes = "Type: String";
			updateSqlText();

		} else if (obj instanceof Clob) {
			String sql = "select " + editingColumnName + " from " + context.getTableName() +
					" where " + context.getIdColumnName() + " = '" + idVal + "'";
			try {
				obj = app.runOneSelect(sql, contextParam.getDBGroupName());
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			stringFieldEditor = new JTextArea(obj == null ? "" : obj.toString(), 6, 40);
			JScrollPane sp = new JScrollPane(stringFieldEditor);
			bordered.add(sp, BorderLayout.CENTER);
			stringFieldEditor.getDocument().addDocumentListener(new textChangeListener());
			isClob = true;
			notes = "Type: CLOB";
			updateSqlText();

		} else if (obj instanceof java.math.BigDecimal) {
			stringFieldEditor = new JTextArea(obj == null ? "" : obj.toString(), 6, 40);
			JScrollPane sp = new JScrollPane(stringFieldEditor);
			bordered.add(sp, BorderLayout.CENTER);
			stringFieldEditor.getDocument().addDocumentListener(new textChangeListener());
			isBigDec = true;
			notes = "Type: BigDecimal";
			updateSqlText();

		} else if (obj instanceof java.sql.Date) {

			String sql = "select " + editingColumnName + " from " + context.getTableName() +
					" where " + context.getIdColumnName() + " = '" + idVal + "'";
			try {
				obj = app.runOneSelectTimeStamp(sql, context.getDBGroupName());
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			//java.sql.Timestamp d = (java.sql.Timestamp)obj;
			//java.util.Date d2 = new java.util.Date(d.getTime());
			stringFieldEditor = new JTextArea(SqlTools.formatTimeStamp((java.sql.Timestamp)obj), 6, 54);
			JScrollPane sp = new JScrollPane(stringFieldEditor);
			bordered.add(sp, BorderLayout.CENTER);
			stringFieldEditor.getDocument().addDocumentListener(new textChangeListener());
			isDate = true;
			notes = "Type: Sql Date";
			updateSqlText();

		} else {
			System.out.println("other type:" + obj.getClass().getName());
		}

		bordered.add(new JLabel(notes), BorderLayout.NORTH);

		editor.pack();
		UITools.center(editor);
		editor.setVisible(true);
		app.appsh.addAppQuitListener(new FloatingFrameQuitListener(editor));
	}


	class cancelActionClass extends AbstractAction {
		private static final long serialVersionUID = 1L;
		public cancelActionClass() {
			super("Cancel");
		}
		public void actionPerformed(ActionEvent e) {
			editor.setVisible(false);
		}
	}

	class saveActionClass extends AbstractAction {
		private static final long serialVersionUID = 1L;
		public saveActionClass() {
			super("Save");
		}
		public void actionPerformed(ActionEvent e) {
			String sql = "select " + context.getIdColumnName() + " from " + context.getTableName() +
				" where " + context.getIdColumnName() + " = '" + idVal + "'";
			List<List<Object>> l = app.runOneSelect(sql, 2, context.getDBGroupName());
			if (l.size() == 0) {
				sqlOutput.setText("ERROR: no rows found for update");
			} else if (l.size() == 2) {
				sqlOutput.setText("ERROR: more than 1 row found for update");
			} else if (l.size() == 1) {
				try {
					int rowsUpdated = app.runOneUpdateAndCommit(createUpdateFromStringEditor());
					context.setEditedValue(stringFieldEditor.getText());
					editor.setVisible(false);
				} catch (SQLException e1) {
					sqlOutput.setText(e1.getMessage());
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
	}

	class textChangeListener implements DocumentListener {

		@Override
		public void changedUpdate(DocumentEvent arg0) {
			updateSqlText();
		}

		@Override
		public void insertUpdate(DocumentEvent arg0) {
			updateSqlText();
		}

		@Override
		public void removeUpdate(DocumentEvent arg0) {
			updateSqlText();
		}
	}

	void updateSqlText() {
		sqlOutput.setText(createUpdateFromStringEditor());
	}

	String createUpdateFromStringEditor() {

		String vv;
		if (isDate) {
			vv = "to_date('" + stringFieldEditor.getText() + "','" + SqlTools.getSQLDateFormatString() + "')";
		} else {
			vv = "'" + SqlTools.makeTextSqlSafe(stringFieldEditor.getText()) + "'";
		}

		return "update " + context.getTableName() +
		     "\n   set " + editingColumnName + "=" + vv +
		     "\n where " + context.getIdColumnName() + " = '" + idVal + "'";
	}
}
