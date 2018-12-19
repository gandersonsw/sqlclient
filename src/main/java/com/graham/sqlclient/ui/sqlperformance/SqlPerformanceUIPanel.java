/* Copyright (C) 2016 Graham Anderson gandersonsw@gmail.com - All Rights Reserved */
package com.graham.sqlclient.ui.sqlperformance;

import com.graham.appshell.tabui.*;
import com.graham.sqlclient.LapaeUIPanelMultipleType;
import com.graham.sqlclient.SqlClientApp;
import com.graham.sqlclient.WSDB;
import com.graham.tools.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

public class SqlPerformanceUIPanel extends AppUIPanelMultiple {

	final static int MAX__POST_SQL = 3;

	private SqlClientApp app;
	private JPanel mainBoxPanel;
	private JComboBox dbGroupField;
	private JTextField maxRows;
	private JTextField maxTests;
	private JTextField fetchSize;
	private JLabel statusText;
	private List<JTextArea> sqlTexts;
	private List<JTextField> postSqlTexts;

	private SqlPerfTestThread testThread;
	private ThreadManagerWithButtons testThreadManager;

	private PerfResultsDataModel resultsTableModel = new PerfResultsDataModel();
	private JTable resultsTable;

	private Action runAction;
	private Action stopAction;

	private JPanel mainPanel;

	public SqlPerformanceUIPanel(SqlClientApp appParam) {
		app = appParam;
		mainBoxPanel = new JPanel();
		mainBoxPanel.setLayout(new BoxLayout(mainBoxPanel, BoxLayout.PAGE_AXIS));
		statusText = new JLabel("Ready to do SQL Performance testing");
		{
			JPanel flowP = new JPanel(new FlowLayout(FlowLayout.LEFT));
			flowP.add(new JLabel("DB:"));
			dbGroupField = new JComboBox();
			//dbGroupField.setEditable(true);
			for (WSDB db : app.getWSS().dbList) {
				dbGroupField.addItem(db.group);
			}
			flowP.add(dbGroupField);
			mainBoxPanel.add(flowP);
		}
		{
			JPanel flowP = new JPanel(new FlowLayout(FlowLayout.LEFT));
			flowP.add(new JLabel("Number of Tests:"));
			maxTests = new JTextField("7", 20);
			flowP.add(maxTests);
			mainBoxPanel.add(flowP);
		}
		{
			JPanel flowP = new JPanel(new FlowLayout(FlowLayout.LEFT));
			flowP.add(new JLabel("Max Rows:"));
			maxRows = new JTextField("100", 20);
			flowP.add(maxRows);
			mainBoxPanel.add(flowP);
		}
		{
			JPanel flowP = new JPanel(new FlowLayout(FlowLayout.LEFT));
			flowP.add(new JLabel("JDBC Fetch Size:"));
			fetchSize = new JTextField("0", 10);
			flowP.add(fetchSize);
			mainBoxPanel.add(flowP);
		}
		{
			sqlTexts = new ArrayList<>();
			for (int i = 1; i <= PerfResultsDataModel.MAX_SQL_TO_TEST; i++) {
				JPanel flowP = new JPanel(new FlowLayout(FlowLayout.LEFT));
				flowP.add(new JLabel("SQL " + i + ":"));
				JTextArea ta = new JTextArea(3, 50);
				sqlTexts.add(ta);
				flowP.add(ta);
				mainBoxPanel.add(flowP);
			}
		}
		{
			postSqlTexts = new ArrayList<>();
			for (int i = 1; i <= MAX__POST_SQL; i++) {
				JPanel flowP = new JPanel(new FlowLayout(FlowLayout.LEFT));
				flowP.add(new JLabel("Post SQL " + i + ":"));
				String text = "";
				if (i == 1) {
					text = "alter system flush buffer_cache";
				} else if (i == 2) {
					text = "alter system flush shared_pool";
				}
				JTextField tf = new JTextField(text, 50);
				postSqlTexts.add(tf);
				flowP.add(tf);
				mainBoxPanel.add(flowP);
			}
		}
		{
			JPanel flowP = new JPanel(new FlowLayout(FlowLayout.LEFT));
			runAction = new runActionClass();
			JButton runButton = new JButton(runAction);
			flowP.add(runButton);
			stopAction = new stopActionClass();
			JButton stopButton = new JButton(stopAction);
			flowP.add(stopButton);
			testThreadManager = new ThreadManagerWithButtons(runAction, stopAction, statusText);
			mainBoxPanel.add(flowP);
		}

		JScrollPane mainScroll = new JScrollPane(mainBoxPanel);

		mainPanel = new JPanel(new BorderLayout());
		mainPanel.add(mainScroll, BorderLayout.CENTER);
		mainPanel.add(statusText, BorderLayout.SOUTH);
	}

	private void initResultsTable() {
		if (resultsTableModel.resultsArr == null) {
			resultsTableModel.clearResults();
		}

		resultsTable = new JTable(resultsTableModel);

		JPanel tablePanel = new JPanel(new BorderLayout());
		tablePanel.add(resultsTable, BorderLayout.CENTER);
		tablePanel.add(resultsTable.getTableHeader(), BorderLayout.NORTH);

		mainBoxPanel.add(tablePanel);
	}

	@Override
	public AppUIPanelType getUIPanelType() {
		return LapaeUIPanelMultipleType.sqlPerformance;
	}

	@Override
	public HashMap<String,Object> getQuitingObjectSave() {
		HashMap<String,Object> data = new HashMap<String,Object>();

		data.put("db", dbGroupField.getSelectedItem());
		data.put("maxTests", maxTests.getText());
		data.put("maxRows", maxRows.getText());
		data.put("fetchSize", fetchSize.getText());
		for (int i = 0; i < sqlTexts.size(); i++) {
			data.put("sql" + i, sqlTexts.get(i).getText());
		}
		for (int i = 0; i < postSqlTexts.size(); i++) {
			data.put("post" + i, postSqlTexts.get(i).getText());
		}
		data.put("results", resultsTableModel.resultsArr);

		return data;
	}

	@Override
	public void initStartingUp(HashMap<String,Object> quitingObjectSave) {
		if (quitingObjectSave == null) {
			return;
		}

		if (quitingObjectSave.get("db") != null) {
			dbGroupField.setSelectedItem(quitingObjectSave.get("db"));
		}
		if (StringTools.isStringTrimSpace(quitingObjectSave.get("maxTests"))) {
			maxTests.setText((String) quitingObjectSave.get("maxTests"));
		}
		if (StringTools.isStringTrimSpace(quitingObjectSave.get("maxRows"))) {
			maxRows.setText((String)quitingObjectSave.get("maxRows"));
		}
		if (StringTools.isStringTrimSpace(quitingObjectSave.get("fetchSize"))) {
			fetchSize.setText((String)quitingObjectSave.get("fetchSize"));
		}
		for (int i = 0; i < sqlTexts.size(); i++) {
			Object sql = quitingObjectSave.get("sql" + i);
			if (StringTools.isStringTrimSpace(sql)) {
				sqlTexts.get(i).setText((String)sql);
			}
		}
		for (int i = 0; i < postSqlTexts.size(); i++) {
			Object post = quitingObjectSave.get("post" + i);
			if (post != null && post instanceof String) {
				postSqlTexts.get(i).setText((String)post);
			}
		}
		resultsTableModel.resultsArr = (Object[][])quitingObjectSave.get("results");
		initResultsTable();
	}


	public class runActionClass extends AbstractAction {
		private static final long serialVersionUID = 1L;
		public runActionClass() {
			super("Run Tests");
		}
		synchronized public void actionPerformed(ActionEvent evt) {
			if (testThreadManager.isRunning()) {
				return;
			}
			List<String> sqls = new ArrayList<>();
			for (int sqlIndex = 0; sqlIndex < PerfResultsDataModel.MAX_SQL_TO_TEST; sqlIndex++) {
				String sql = sqlTexts.get(sqlIndex).getText().trim();
				if (sql.length() > 0) {
					sqls.add(sql);
				}
			}
			List<String> preAndPostSqls = new ArrayList<>();
			for (int i = 0; i < MAX__POST_SQL; i++) {
				String sql = postSqlTexts.get(i).getText().trim();
				if (sql.length() > 0) {
					preAndPostSqls.add(sql);
				}
			}
			String db = dbGroupField.getSelectedItem().toString();
			int mt = Integer.parseInt(maxTests.getText());
			int mr = Integer.parseInt(maxRows.getText());
			int fs = Integer.parseInt(fetchSize.getText());

			testThreadManager.run();
			testThread = new SqlPerfTestThread(mt, mr, fs, sqls, preAndPostSqls, app.getConnPool(db), resultsTableModel, testThreadManager);
			testThread.start();
		}
	}

	public class stopActionClass extends AbstractAction {
		private static final long serialVersionUID = 1L;
		public stopActionClass() {
			super("Stop Tests");
		}
		public void actionPerformed(ActionEvent e) {
			testThreadManager.stop();
		}
	}

	public List<Action> getActionsForMenu() {
		List<Action> menuActions = new ArrayList<>();
		menuActions.add(runAction);
		menuActions.add(stopAction);
		return menuActions;
	}

	public JComponent getJComponent() {
		return mainPanel;
	}
}
