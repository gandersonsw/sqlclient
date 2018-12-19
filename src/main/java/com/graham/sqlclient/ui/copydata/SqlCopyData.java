/* Copyright (C) 2016 Graham Anderson gandersonsw@gmail.com - All Rights Reserved */
package com.graham.sqlclient.ui.copydata;

import com.graham.appshell.data.AppData;
import com.graham.sqlclient.LapaeUIPanelMultipleType;
import com.graham.sqlclient.SqlClientApp;
import com.graham.sqlclient.WSDB;
import com.graham.appshell.tabui.AppUIPanelMultiple;
import com.graham.appshell.tabui.AppUIPanelType;
import com.graham.sqlclient.ui.sqltablebrowser.DBMetaSchema;
import com.graham.tools.StringTools;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.*;
import java.util.List;

public class SqlCopyData extends AppUIPanelMultiple {

	private SqlClientApp app;
	private JTabbedPane tabPane;
	private JPanel mainPanel;
	private JTextArea outputText;
	private JTextArea metaDataText;
	private JComboBox schemaField;
	private JComboBox sourceDbGroupField;
	private JComboBox destinationDbGroupField;
	private JTextField tableName;
	private JTextField tableFieldName;
	private JTextArea ids;
	private JTextArea onlyInsertIntoTables;
	private JTextArea columnNamesToRandomize;
	private JTextArea refsToReplace;
	private JLabel statusText;

	private Action runAction;
	private Action stopAction;
	private Action destReplacementsAction;

	private SqlCopyThread copyThread;
	private ThreadManagerWithButtons copyThreadManager;

	public SqlCopyData(SqlClientApp appParam) {
		app = appParam;

		JPanel topPanel = new JPanel();
		topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.PAGE_AXIS));
		statusText = new JLabel("Ready to do SQL data copy");

		{
			JPanel flowP = new JPanel(new FlowLayout(FlowLayout.LEFT));
			flowP.add(new JLabel("Schema:"));
			schemaField = new JComboBox();
			Collection<AppData> schemas = app.appsh.getDataManagerList(DBMetaSchema.class).getList();
			for (AppData sch : schemas) {
				schemaField.addItem(sch);
			}
			flowP.add(schemaField);
			topPanel.add(flowP);
		}
		{
			JPanel flowP = new JPanel(new FlowLayout(FlowLayout.LEFT));
			flowP.add(new JLabel("Source DB:"));
			sourceDbGroupField = new JComboBox();
			//dbGroupField.setEditable(true);
			for (WSDB db : app.getWSS().dbList) {
				sourceDbGroupField.addItem(db.group);
			}
			flowP.add(sourceDbGroupField);
			topPanel.add(flowP);
		}
		{
			JPanel flowP = new JPanel(new FlowLayout(FlowLayout.LEFT));
			flowP.add(new JLabel("Destination DB:"));
			destinationDbGroupField = new JComboBox();
			//dbGroupField.setEditable(true);
			for (WSDB db : app.getWSS().dbList) {
				destinationDbGroupField.addItem(db.group);
			}
			flowP.add(destinationDbGroupField);
			topPanel.add(flowP);
		}
		{
			JPanel flowP = new JPanel(new FlowLayout(FlowLayout.LEFT));
			flowP.add(new JLabel("Table Name:"));
			tableName = new JTextField("", 50);
			flowP.add(tableName);
			topPanel.add(flowP);
		}
		{
			JPanel flowP = new JPanel(new FlowLayout(FlowLayout.LEFT));
			flowP.add(new JLabel("Field Name:"));
			tableFieldName = new JTextField("", 50);
			flowP.add(tableFieldName);
			topPanel.add(flowP);
		}
		{
			JPanel flowP = new JPanel(new FlowLayout(FlowLayout.LEFT));
			flowP.add(new JLabel("IDs:"));
			ids = new JTextArea(6, 50);
			flowP.add(ids);
			topPanel.add(flowP);
		}

		{
			JPanel flowP = new JPanel(new FlowLayout(FlowLayout.LEFT));
			flowP.add(new JLabel("Limit to Tables:"));
			onlyInsertIntoTables = new JTextArea(6, 50);
			flowP.add(onlyInsertIntoTables);
			topPanel.add(flowP);
		}


		{
			JPanel flowP = new JPanel(new FlowLayout(FlowLayout.LEFT));
			flowP.add(new JLabel("Randomize Columns:"));
			columnNamesToRandomize = new JTextArea(6, 50);
			flowP.add(columnNamesToRandomize);
			topPanel.add(flowP);
		}


		{
			JPanel flowP = new JPanel(new FlowLayout(FlowLayout.LEFT));
			flowP.add(new JLabel("Replace Columns:"));
			refsToReplace = new JTextArea(6, 50);
			flowP.add(refsToReplace);
			topPanel.add(flowP);
		}

		{
			JPanel flowP = new JPanel(new FlowLayout(FlowLayout.LEFT));
			runAction = new runActionClass();
			JButton runButton = new JButton(runAction);
			flowP.add(runButton);
			stopAction = new stopActionClass();
			JButton stopButton = new JButton(stopAction);
			flowP.add(stopButton);
			copyThreadManager = new ThreadManagerWithButtons(runAction, stopAction, statusText);
			topPanel.add(flowP);
		}

		tabPane = new JTabbedPane();
		JScrollPane topScroll = new JScrollPane(topPanel);
		tabPane.add("Setup", topScroll);

		outputText = new JTextArea();
		JScrollPane outputScroll = new JScrollPane(outputText);
		tabPane.add("Inserts", outputScroll);

		{
			metaDataText = new JTextArea();
			JScrollPane metaDataScroll = new JScrollPane(metaDataText);

			JPanel flowP = new JPanel(new FlowLayout(FlowLayout.LEFT));
			destReplacementsAction = new getDestReplacementsActionClass();
			JButton runButton = new JButton(destReplacementsAction);
			flowP.add(runButton);

			JPanel borderLayout = new JPanel(new BorderLayout());
			borderLayout.add(metaDataScroll, BorderLayout.CENTER);
			borderLayout.add(flowP, BorderLayout.SOUTH);

			tabPane.add("Meta Data", borderLayout);
		}

		mainPanel = new JPanel(new BorderLayout());
		mainPanel.add(tabPane, BorderLayout.CENTER);
		mainPanel.add(statusText, BorderLayout.SOUTH);
	}

	@Override
	public JComponent getJComponent() {
		return mainPanel;
	}

	@Override
	public HashMap<String, Object> getQuitingObjectSave() {
		HashMap<String,Object> data = new HashMap<String,Object>();

		data.put("sdb", sourceDbGroupField.getSelectedItem());
		data.put("ddb", destinationDbGroupField.getSelectedItem());
		data.put("table", tableName.getText());
		data.put("tableFieldName", tableFieldName.getText());
		data.put("ids", ids.getText());
		data.put("onlyInsertIntoTables", onlyInsertIntoTables.getText());
		data.put("columnNamesToRandomize", columnNamesToRandomize.getText());
		data.put("refsToReplace", refsToReplace.getText());

	//	data.put("outputText", outputText.getText());
		return data;
	}

	@Override
	public void initStartingUp(HashMap<String, Object> quitingObjectSave) {
		if (quitingObjectSave == null) {
			return;
		}

		if (quitingObjectSave.get("sdb") != null) {
			sourceDbGroupField.setSelectedItem(quitingObjectSave.get("sdb"));
		}
		if (quitingObjectSave.get("ddb") != null) {
			destinationDbGroupField.setSelectedItem(quitingObjectSave.get("ddb"));
		}

		if (StringTools.isStringTrimSpace(quitingObjectSave.get("table"))) {
			tableName.setText((String) quitingObjectSave.get("table"));
		}
		if (StringTools.isStringTrimSpace(quitingObjectSave.get("tableFieldName"))) {
			tableFieldName.setText((String) quitingObjectSave.get("tableFieldName"));
		}
		if (StringTools.isStringTrimSpace(quitingObjectSave.get("onlyInsertIntoTables"))) {
			onlyInsertIntoTables.setText((String) quitingObjectSave.get("onlyInsertIntoTables"));
		}
		if (StringTools.isStringTrimSpace(quitingObjectSave.get("columnNamesToRandomize"))) {
			columnNamesToRandomize.setText((String) quitingObjectSave.get("columnNamesToRandomize"));
		}
		if (StringTools.isStringTrimSpace(quitingObjectSave.get("refsToReplace"))) {
			refsToReplace.setText((String) quitingObjectSave.get("refsToReplace"));
		}

		if (StringTools.isStringTrimSpace(quitingObjectSave.get("ids"))) {
			ids.setText((String)quitingObjectSave.get("ids"));
		}
	}

	@Override
	public AppUIPanelType getUIPanelType() {
		return LapaeUIPanelMultipleType.sqlCopy;
	}

	public class getDestReplacementsActionClass extends AbstractAction {
		private static final long serialVersionUID = 1L;

		public getDestReplacementsActionClass() {
			super("Calculate Refs Skipped Replacements");
		}

		synchronized public void actionPerformed(ActionEvent evt) {
			String ddb = destinationDbGroupField.getSelectedItem().toString();
			DestReplacementsThread t = new DestReplacementsThread(app.getConnPool(ddb), metaDataText);
			t.start();
		}
	}

	public class runActionClass extends AbstractAction {
		private static final long serialVersionUID = 1L;
		public runActionClass() {
			super("Copy");
		}
		synchronized public void actionPerformed(ActionEvent evt) {
			if (copyThreadManager.isRunning()) {
				return;
			}

			String schema = schemaField.getSelectedItem().toString();
			String sdb = sourceDbGroupField.getSelectedItem().toString();
			String ddb = destinationDbGroupField.getSelectedItem().toString();

			List<String> idsList = new ArrayList<>();
			for (String id : ids.getText().split("\n")) {
				String s = id.trim();
				if (s.length() > 0) {
					idsList.add(id);
				}
			}

			Set<String> onlyInsertIntoTablesSet = new HashSet<>();
			for (String id : onlyInsertIntoTables.getText().toUpperCase().split("\n")) {
				String s = id.trim();
				if (s.length() > 0) {
					onlyInsertIntoTablesSet.add(id);
				}
			}
			Set<String> columnNamesToRandomizeSet = new HashSet<>();
			for (String id : columnNamesToRandomize.getText().toUpperCase().split("\n")) {
				String s = id.trim();
				if (s.length() > 0) {
					columnNamesToRandomizeSet.add(id);
				}
			}
			Map<String,String> refsToReplaceMap = new HashMap<>();
			for (String id : refsToReplace.getText().toUpperCase().split("\n")) {
				int ci = id.lastIndexOf(":");
				if (ci > -1) {
					String key = id.substring(0, ci);
					String newId = id.substring(ci+1);
					refsToReplaceMap.put(key, newId);
				}
			}

			copyThreadManager.run();
			copyThread = new SqlCopyThread(
				schema,
				app.getConnPool(sdb),
				app.getConnPool(ddb),
				tableName.getText().trim().toUpperCase(),
				tableFieldName.getText().trim().toUpperCase(),
				idsList,
				outputText,
				copyThreadManager,
				onlyInsertIntoTablesSet,
				columnNamesToRandomizeSet,
				refsToReplaceMap,
				metaDataText);
			copyThread.start();
		}
	}

	public class stopActionClass extends AbstractAction {
		private static final long serialVersionUID = 1L;
		public stopActionClass() {
			super("Stop Copy");
		}
		public void actionPerformed(ActionEvent e) {
			copyThreadManager.stop();
		}
	}

	public List<Action> getActionsForMenu() {
		List<Action> menuActions = new ArrayList<>();
		menuActions.add(runAction);
		menuActions.add(stopAction);
		return menuActions;
	}
}
