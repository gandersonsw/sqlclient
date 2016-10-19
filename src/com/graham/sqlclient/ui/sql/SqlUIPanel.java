/* Copyright (C) 2016 Graham Anderson gandersonsw@gmail.com - All Rights Reserved */
package com.graham.sqlclient.ui.sql;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.List;

import javax.swing.*;

import com.graham.appshell.tabui.*;
import com.graham.sqlclient.LapaeUIPanelMultipleType;
import com.graham.sqlclient.SqlClientApp;
import com.graham.sqlclient.ui.sql.history.HistoryUI;
import com.graham.tools.SearchContext;
import com.graham.tools.UITools;

public class SqlUIPanel extends AppUIPanelMultiple {

	private SqlAutoCompleteManager autoCompleteManager;
	private SqlTopButtonManager buttonManager;
	private SqlDataTableManager dataTableManager;
	private JTextArea query;
	private String currentRunningSQL;
	private JLabel footer;
	private boolean footerTextShouldBeSaved = true;
	private JTabbedPane lowerTabbed;
	private SqlClientApp app;
	private JSplitPane split;
	private JPanel main;

	@Override
	public AppUIPanelType getUIPanelType() {
		return LapaeUIPanelMultipleType.sql;
	}
	
	public SqlUIPanel(SqlClientApp appParam) {
		app = appParam;
		JScrollPane sqlTextEditor = createSqlTextEditor();
		dataTableManager = new SqlDataTableManager(app, this);
		JScrollPane scrolledResults = new JScrollPane(dataTableManager.getTable());
		
		lowerTabbed = new JTabbedPane();
		lowerTabbed.addTab("Data", scrolledResults);
		lowerTabbed.addTab("Auto-complete", createAutoCompletePanel());
		lowerTabbed.addTab("History", new HistoryUI().createHistoryPanel(app.appsh, this));
		
		split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, sqlTextEditor, lowerTabbed);
		split.setDividerLocation(110);
		
		main = new JPanel(new BorderLayout());
		main.add(split, BorderLayout.CENTER);
		buttonManager = new SqlTopButtonManager(app, this);
		main.add(buttonManager.getPanel(), BorderLayout.NORTH);
		footer = new JLabel(" ");
		main.add(footer, BorderLayout.SOUTH);
	}
	
	private JScrollPane createSqlTextEditor() {
		query = new JTextArea();
		Font font = new Font("Courier New", Font.PLAIN, 12);
		query.setFont(font);

		return new JScrollPane(query);
	}
	
	private JScrollPane createAutoCompletePanel() {
		SQLAutoCompleteTableModel model2 = new SQLAutoCompleteTableModel(app);
		JTable autoCompleteTable = new NoFocusJTable(model2);
		model2.setTable1(autoCompleteTable);
		autoCompleteTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		autoCompleteManager = new SqlAutoCompleteManager(query, autoCompleteTable, model2, new SelectAutoCompleteTabAction());
		return new JScrollPane(autoCompleteTable);
	}

	/**
	 * add a block of text to the editor.
	 */
	public void addTextBlock(final String s) {
		query.append("\n\n");
		query.append(s);
		query.append("\n");
	}

	public List<List<Object>> getData() {
		return dataTableManager.getData();
	}

	public String getDBGroupName() {
		return buttonManager.getDBGroupName();
	}

	public void sqlResultsDataChanged(Object dataParam, boolean firstTime) {
		int rowCount = dataTableManager.dataChanged(dataParam, firstTime);
		setFooterText(rowCount + " Rows so far...", false);
	}

	public boolean isSqlIdle() {
		// return true if there is no SQL rexecuting right now
		return currentRunningSQL == null;
	}

	public boolean findText(SearchContext params) {
		return dataTableManager.findText(params);
	}

	public String prepForSqlExecute(boolean selectedOnlyFlag) {
		selectQueryResultsTab();

		if (selectedOnlyFlag) {
			currentRunningSQL = UITools.getSelectedTextOrBlockAtInsert(query, false);
		} else {
			currentRunningSQL = query.getText();
		}
		dataTableManager.setSqlThatGoesWithResultSet(currentRunningSQL);
		//sqlThatGoesWithResultSet = currentRunningSQL;
		return currentRunningSQL;
	}

	public void executeSqlDone() {
		currentRunningSQL = null;
	}

	public class SelectAutoCompleteTabAction extends AbstractAction {
		private static final long serialVersionUID = 1L;
		public SelectAutoCompleteTabAction() {
			super("Select Auto Complete");
		}
		public void actionPerformed(ActionEvent e) {
			selectAutoCompleteTab();
		}
	}

	@Override
	public HashMap<String,Object> getQuitingObjectSave() {
		HashMap<String,Object> m = new HashMap<String,Object>();
		m.put("query", query.getText());
		m.put("split", split.getDividerLocation());
		m.put("dbGroup", buttonManager.getDBGroupName());
		dataTableManager.populateQuitingObjectSave(m);
		if (footerTextShouldBeSaved) {
			m.put("footer_text", footer.getText());
		}
		return m;
	}

	@Override
	public void initStartingUp(HashMap<String,Object> quitingObjectSave) {
		if (quitingObjectSave != null) {
			autoCompleteManager.setIgnoreChanges(true);
			query.setText((String)quitingObjectSave.get("query"));
			split.setDividerLocation((Integer)quitingObjectSave.get("split"));
			if (quitingObjectSave.containsKey("dbGroup")) {
				buttonManager.setDBGroupName( (String)quitingObjectSave.get("dbGroup"));
			}
			dataTableManager.initStartingUp(quitingObjectSave);
			if (quitingObjectSave.containsKey("footer_text")) {
				footer.setText((String)quitingObjectSave.get("footer_text"));
				footerTextShouldBeSaved = true;
			} else {
				footer.setText(" ");
			}
			autoCompleteManager.setIgnoreChanges(false);
		}
	}

	public void selectQueryResultsTab() {
		lowerTabbed.setSelectedIndex(0);
	}
	
	public void selectAutoCompleteTab() {
		lowerTabbed.setSelectedIndex(1);
	}

	public void setFooterText(final String text, final boolean shouldBeSaved) {
		footer.setText(text);
		footerTextShouldBeSaved = shouldBeSaved;
	}

	public List<Action> getActionsForMenu() {
		return buttonManager.getActionsForMenu();
	}

	public JComponent getJComponent() {
		return main;
	}

	public boolean close() {
		buttonManager.close();
		return true;
	}

}
