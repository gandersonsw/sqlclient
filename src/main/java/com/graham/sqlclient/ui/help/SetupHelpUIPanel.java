/* Copyright (C) 2016 Graham Anderson gandersonsw@gmail.com - All Rights Reserved */
package com.graham.sqlclient.ui.help;

import com.graham.appshell.data.AppData;
import com.graham.appshell.tabui.AppUIPanelSingleton;
import com.graham.appshell.tabui.AppUIPanelType;
import com.graham.sqlclient.LapaeUIPanelSingletonType;
import com.graham.sqlclient.SqlClientApp;
import com.graham.sqlclient.WSDB;
import com.graham.sqlclient.WorkSpaceSettings;
import com.graham.sqlclient.ui.databrowser.DataBrowserDefinedTable;
import com.graham.sqlclient.ui.sqltablebrowser.DBMetaTable;
import com.graham.tools.ConnPool;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.HashMap;

public class SetupHelpUIPanel extends AppUIPanelSingleton {

	final static private int STEP_SETUPDBCONN = 0;
	final static private int STEP_CREATEMETADATA = 1;
	final static private int STEP_CREATEBROWSERDATA = 2;
	final static private int STEP_COMPLETE = 3;

	private SqlClientApp app;
	private JScrollPane helpPanel;

	static class HelpStepUIItem {
		JLabel statusTxt;
		JCheckBox complete;
		JButton verify;
		JLabel title;
		JTextArea helpText;
	}

	private JPanel panelInsideScroll;
	private GridBagLayout gridbag;
	private GridBagConstraints labelConstr;
	private GridBagConstraints fieldConstr;

	private HelpStepUIItem[] helpSteps;

	private int lastCompletedHelpStep = -1;

	public SetupHelpUIPanel(SqlClientApp appParam) {
		app = appParam;

		gridbag = new GridBagLayout();
		labelConstr = new GridBagConstraints();
		labelConstr.weightx = 0.0;
		labelConstr.gridx = 1;
		labelConstr.anchor = GridBagConstraints.WEST;
		labelConstr.insets = new Insets(3,3,16,3);
		fieldConstr = new GridBagConstraints();
		fieldConstr.fill = GridBagConstraints.HORIZONTAL;
		fieldConstr.gridx = 2;
		fieldConstr.weightx = 1.0;
		fieldConstr.insets = new Insets(3,3,20,3);

		panelInsideScroll = new JPanel();
		panelInsideScroll.setLayout(gridbag);

		helpSteps = new HelpStepUIItem[4];

		helpSteps[STEP_SETUPDBCONN] = createHelpStep(
			" 1. Setup database connections ",
			"The first step is to set up the database connections. Go to the \"Project\" menu and select \"SQL Settings\". Enter a Url, Username and Password for the new database connection. Also enter a Connection Name, this is how the database will be identified within SQLClient.  If you want to set up more connections, click on \"Add DB\" at the bottom of the window. ",
			new VerifyConnectionAction()
		);
		helpSteps[STEP_CREATEMETADATA] = createHelpStep(
			" 2. Create local metadata ",
			"This optional step will create a local copy of database metadata. This allows for autocomplete and easy access to information about table columns and indexes. Go to the \"Project\" menu and select \"Table Browser\". Click on \"Build Local DBMD\" near the top-left of the window. This may take anywhere from 1 minute to 30 minutes depending on the size of the database. It may be cancelled at anytime.",
			new VerifyDBMD()
		);
		helpSteps[STEP_CREATEBROWSERDATA] = createHelpStep(
			" 3. Create local browser data ",
			"This optional step will create a local copy of table relationship data. This allows for browsing and updating tables. Go to the \"Project\" menu and select \"New Browser\". Click on \"Import Relationships\" near the center-left of the window. This may take anywhere from 1 minute to 30 minutes depending on the size of the database. It may be cancelled at anytime.",
			new VerifyRelationships()
		);
		helpSteps[STEP_COMPLETE] = createHelpStep(
			" 4. Setup Complete! ",
			"Setup is complete. Try going to a SQL window and running some sql. Select \"New SQL\" from the \"Project\" menu.",
			new CompleteAction()
		);
		helpSteps[STEP_COMPLETE].complete.setVisible(false);

		for (int i = 1; i < helpSteps.length; i++) {
			setHelpStepEnabled(i, false);
		}

		JPanel panelForTopJustification = new JPanel(new BorderLayout());
		panelForTopJustification.add(panelInsideScroll, BorderLayout.NORTH);

		helpPanel = new JScrollPane(panelForTopJustification);
	}

	private HelpStepUIItem createHelpStep(String stepTitle, String stepHelpText, AbstractAction verifyAction) {

		HelpStepUIItem hs = new HelpStepUIItem();

		JPanel bottonsPanel = new JPanel();
		bottonsPanel.setLayout(new BoxLayout(bottonsPanel, BoxLayout.Y_AXIS));
		hs.title = new JLabel(stepTitle);
		bottonsPanel.add(hs.title);
		hs.complete = new JCheckBox("complete");
		hs.complete.setEnabled(false);
		bottonsPanel.add(hs.complete);
		hs.verify = new JButton(verifyAction);
		bottonsPanel.add(hs.verify);

		hs.helpText = new JTextArea();
		hs.helpText.setEditable(false);
		hs.helpText.setFont(hs.helpText.getFont().deriveFont(12.0f));
		hs.helpText.setPreferredSize(new Dimension(300, 100));
		hs.helpText.setBorder(new EmptyBorder(new Insets(3, 3, 3, 3)));
		hs.helpText.setLineWrap(true);
		hs.helpText.setWrapStyleWord(true);
		hs.helpText.setText(stepHelpText);

		hs.statusTxt = new JLabel(" ");
		hs.statusTxt.setBorder(new EmptyBorder(new Insets(3, 3, 3, 3)));

		JPanel helpTextAndStatus = new JPanel(new BorderLayout());
		helpTextAndStatus.add(hs.helpText, BorderLayout.CENTER);
		helpTextAndStatus.add(hs.statusTxt, BorderLayout.SOUTH);

		gridbag.setConstraints(bottonsPanel, labelConstr);
		panelInsideScroll.add(bottonsPanel);
		gridbag.setConstraints(helpTextAndStatus, fieldConstr);
		panelInsideScroll.add(helpTextAndStatus);

		return hs;
	}

	private void setHelpStepEnabled(int helpStepIndex, boolean enabledFlag) {
		helpSteps[helpStepIndex].statusTxt.setEnabled(enabledFlag);
		helpSteps[helpStepIndex].verify.setEnabled(enabledFlag);
		helpSteps[helpStepIndex].title.setEnabled(enabledFlag);
		helpSteps[helpStepIndex].helpText.setEnabled(enabledFlag);
	}

	private void helpStepSuccessful(int hsIndex) {
		helpSteps[hsIndex].complete.setSelected(true);
		lastCompletedHelpStep = hsIndex;
		setHelpStepEnabled(hsIndex, false);
		if (hsIndex < helpSteps.length - 1) {
			setHelpStepEnabled(hsIndex + 1, true);
		}
	}

	@Override
	public JComponent getJComponent() {
		return helpPanel;
	}

	@Override
	public HashMap<String, Object> getQuitingObjectSave() {
		HashMap<String, Object> ret = new HashMap<>();
		ret.put("lastStep", lastCompletedHelpStep);
		return ret;
	}

	@Override
	public void initStartingUp(HashMap<String, Object> quitingObjectSave) {
		lastCompletedHelpStep = (Integer)quitingObjectSave.get("lastStep");
		for (int hsIndex = 0; hsIndex <= lastCompletedHelpStep; hsIndex++) {
			setHelpStepEnabled(hsIndex, false);
			helpSteps[hsIndex].complete.setSelected(true);
		}
		setHelpStepEnabled(lastCompletedHelpStep + 1, true);
	}

	@Override
	public AppUIPanelType getUIPanelType() {
		return LapaeUIPanelSingletonType.setupHelp;
	}


	public class VerifyConnectionAction extends AbstractAction {
		public VerifyConnectionAction() {
			super("Verify Connection");
		}
		public void actionPerformed(ActionEvent evt) {
			ConnPool connPool;

			final HelpStepUIItem hs = helpSteps[STEP_SETUPDBCONN];

			hs.statusTxt.setText("testing connection...");

			WorkSpaceSettings s = app.getWSS();
			if (s.dbList.size() == 0) {
				hs.statusTxt.setText("verify failed - there are not any connections set up yet. Go to the SQL Settings to create a new database connection");
				return;
			}

			if (s.dbList.size() > 0) {
				WSDB db = s.dbList.get(0);

				if (db.group == null || db.group.trim().length() == 0) {
					hs.statusTxt.setText("verify failed -  a connection name is required. please add a connection name");
					return;
				}

				connPool = new ConnPool(db.databaseUrl, db.databaseUserName, db.databasePassword);
				Connection conn = null;
				try {
					try {
						conn = connPool.getConn();
					} catch (ClassNotFoundException cne) {
						hs.statusTxt.setText("verify failed - database driver was not found");
						return;
					} catch (SQLException sqle) {
						hs.statusTxt.setText("verify failed -  sql exception:" + sqle.getMessage() + ":" + sqle.getErrorCode());
						return;
					}

					Statement stmt = null;
					ResultSet rs = null;
					try {
						stmt = conn.createStatement();
						rs = stmt.executeQuery("select * from dual");
						hs.statusTxt.setText("Connection verified");
						helpStepSuccessful(STEP_SETUPDBCONN);
					} catch (SQLException sqle) {
						hs.statusTxt.setText("verify failed - sql exception:" + sqle.getMessage() + ":" + sqle.getErrorCode());
						return;
					} finally {
						try {
							if (rs != null) {
								rs.close();
							}
						} catch (SQLException sqle) { }
						try {
							if (stmt != null) {
								stmt.close();
							}
						} catch (SQLException sqle) { }
					}
				} finally {
					try {
						if (conn != null) {
							connPool.releaseConn(conn);
						}
					} catch (SQLException sqle) { }
				}
			}
		}
	}

	public class VerifyDBMD extends AbstractAction {
		public VerifyDBMD() {
			super("Verify Meta Data");
		}
		public void actionPerformed(ActionEvent evt) {
			Collection<AppData> tables = app.appsh.getDataManagerList(DBMetaTable.class).getList();
			if (tables.size() > 0) {
				helpSteps[STEP_CREATEMETADATA].statusTxt.setText("database meta data successfully created with " + tables.size() + " tables");
				helpStepSuccessful(STEP_CREATEMETADATA);
				//helpSteps[STEP_CREATEMETADATA].complete.setSelected(true);
			} else {
				helpSteps[STEP_CREATEMETADATA].statusTxt.setText("verify failed - metadata was not created");
			}
		}
	}

	public class VerifyRelationships extends AbstractAction {
		public VerifyRelationships() {
			super("Verify table relationship data");
		}
		public void actionPerformed(ActionEvent evt) {
			Collection<AppData> tables = app.appsh.getDataManagerList(DataBrowserDefinedTable.class).getList();
			if (tables.size() > 0) {
				helpSteps[STEP_CREATEBROWSERDATA].statusTxt.setText("database relationship data successfully created with " + tables.size() + " tables");
				//helpSteps[STEP_CREATEBROWSERDATA].complete.setSelected(true);
				helpStepSuccessful(STEP_CREATEBROWSERDATA);
			} else {
				helpSteps[STEP_CREATEBROWSERDATA].statusTxt.setText("verify failed - metadata was not created");
			}
		}
	}

	public class CompleteAction extends AbstractAction {
		public CompleteAction() {
			super("Close Setup Help");
		}
		public void actionPerformed(ActionEvent evt) {
			app.appsh.getTabManager().removeTab(LapaeUIPanelSingletonType.setupHelp.getKey());
		}
	}

}
