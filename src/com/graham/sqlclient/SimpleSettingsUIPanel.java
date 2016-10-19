/* Copyright (C) 2016 Graham Anderson gandersonsw@gmail.com - All Rights Reserved */
package com.graham.sqlclient;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.*;

import com.graham.appshell.*;
import com.graham.appshell.data.DataUICheckList;
import com.graham.appshell.data.DataGroup;
import com.graham.appshell.data.DataManagerSingleton;
import com.graham.appshell.tabui.*;

public class SimpleSettingsUIPanel extends AppUIPanelSingleton {
	
	App app;
	List<JTextField> dUrl = new ArrayList<JTextField>();
	List<JTextField> dUserName = new ArrayList<JTextField>();
	List<JPasswordField> dPassword = new ArrayList<JPasswordField>();
	List<JTextField> dGroup = new ArrayList<JTextField>();
	JTextField dWebUrl;
	//JTextField dFTPUrl;
	//JTextField dFTPUserName;
	//JPasswordField dFTPPassword;
	DataUICheckList dGroups;
	
	int addDBCompIndex = 0;
	JPanel gridPanel;
	GridBagLayout gridbag;
	GridBagConstraints labelConstr;
	GridBagConstraints fieldConstr;
	WorkSpaceSettings settings;

	private JPanel mainPanel;

	private List<EditDBUrl> editorPanels = new ArrayList<>();
	
	public void addDB(final WSDB db) {
		JLabel lab;
		JTextField tf;
		
		lab = new JLabel("Database:");
		gridbag.setConstraints(lab, labelConstr);
		gridPanel.add(lab, addDBCompIndex++);
		lab = new JLabel("");
		gridbag.setConstraints(lab, fieldConstr);
		gridPanel.add(lab, addDBCompIndex++);
		
		lab = new JLabel("Url:");
		gridbag.setConstraints(lab, labelConstr);
		gridPanel.add(lab, addDBCompIndex++);

		tf = new JTextField();
		JPanel buttonAndUrl = new JPanel(new BorderLayout());
		buttonAndUrl.add(tf, BorderLayout.CENTER);
		buttonAndUrl.add(new JButton(new EditDBUrlAction(tf)), BorderLayout.EAST);

		gridbag.setConstraints(buttonAndUrl, fieldConstr);
		tf.setText(db.databaseUrl);
		tf.setToolTipText("The first Database is the default one. To remove a Database, delete the Url and click save");

		gridPanel.add(buttonAndUrl, addDBCompIndex++);
		dUrl.add(tf);
		
		lab = new JLabel("User name:");
		gridbag.setConstraints(lab, labelConstr);
		gridPanel.add(lab, addDBCompIndex++);
		tf = new JTextField();
		gridbag.setConstraints(tf, fieldConstr);
		tf.setText(db.databaseUserName);
		gridPanel.add(tf, addDBCompIndex++);
		dUserName.add(tf);
		
		lab = new JLabel("Password:");
		gridPanel.add(lab, addDBCompIndex++);
		gridbag.setConstraints(lab, labelConstr);
		JPasswordField pw = new JPasswordField ();
		gridbag.setConstraints(pw, fieldConstr);
		pw.setText(db.databasePassword);
		gridPanel.add(pw, addDBCompIndex++);
		dPassword.add(pw);
		
		lab = new JLabel("Connection Name:");
		gridPanel.add(lab, addDBCompIndex++);
		gridbag.setConstraints(lab, labelConstr);
		tf = new JTextField();
		gridbag.setConstraints(tf, fieldConstr);
		tf.setText(db.group);
		gridPanel.add(tf, addDBCompIndex++);
		dGroup.add(tf);
	}

	public SimpleSettingsUIPanel(App appParam) {
		app = appParam;
		settings = (WorkSpaceSettings)app.getDataManagerSingleton(WorkSpaceSettings.class).get();
		gridPanel = new JPanel();
		
		gridbag = new GridBagLayout();
		labelConstr = new GridBagConstraints();
		labelConstr.weightx = 0.0;
		labelConstr.gridx = 1;
		labelConstr.anchor = GridBagConstraints.WEST;
		labelConstr.insets = new Insets(3,3,3,3);
		fieldConstr = new GridBagConstraints();
		fieldConstr.fill = GridBagConstraints.HORIZONTAL;
		fieldConstr.gridx = 2;
		fieldConstr.weightx = 1.0;
		fieldConstr.insets = new Insets(3,3,3,3);
		gridPanel.setLayout(gridbag);
		
		JLabel lab;
		
		if (settings.dbList.size() == 0) {
			addDB(new WSDB());
		} else {
			for (WSDB db : settings.dbList) {
				addDB(db);
			}
		}
		
		//  ****************** Web Server ********************
		lab = new JLabel("Web Server:");
		gridbag.setConstraints(lab, labelConstr);
		gridPanel.add(lab);
		lab = new JLabel("");
		gridbag.setConstraints(lab, fieldConstr);
		gridPanel.add(lab);
		
		lab = new JLabel("Base Url:");
		gridbag.setConstraints(lab, labelConstr);
		gridPanel.add(lab);
		dWebUrl = new JTextField();
		gridbag.setConstraints(dWebUrl, fieldConstr);
		dWebUrl.setText(settings.webUrl);
		gridPanel.add(dWebUrl);

		/*
		//  ****************** FTP ********************
		lab = new JLabel("FTP:");
		gridbag.setConstraints(lab, labelConstr);
		gridPanel.add(lab);
		lab = new JLabel("");
		gridbag.setConstraints(lab, fieldConstr);
		gridPanel.add(lab);
		
		lab = new JLabel("Url:");
		gridbag.setConstraints(lab, labelConstr);
		gridPanel.add(lab);
		dFTPUrl = new JTextField();
		gridbag.setConstraints(dFTPUrl, fieldConstr);
		dFTPUrl.setText(settings.ftpUrl);
		gridPanel.add(dFTPUrl);
		
		lab = new JLabel("User name:");
		gridbag.setConstraints(lab, labelConstr);
		gridPanel.add(lab);
		dFTPUserName = new JTextField();
		gridbag.setConstraints(dFTPUserName, fieldConstr);
		dFTPUserName.setText(settings.ftpUserName);
		gridPanel.add(dFTPUserName);
		
		lab = new JLabel("Password:");
		gridPanel.add(lab);
		gridbag.setConstraints(lab, labelConstr);
		dFTPPassword = new JPasswordField ();
		gridbag.setConstraints(dFTPPassword, fieldConstr);
		dFTPPassword.setText(settings.ftpPassword);
		gridPanel.add(dFTPPassword);
		*/
		
		
		lab = new JLabel("Scopes:");
		gridPanel.add(lab);
		gridbag.setConstraints(lab, labelConstr);
		dGroups = new DataUICheckList(app.getDataManagerList(DataGroup.class));
		JScrollPane sp = dGroups.initUICheckList(settings.groups);
		gridbag.setConstraints(sp, fieldConstr);
		gridPanel.add(sp);



		mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout(5,5));
		
		JScrollPane gridPanelScroll = new JScrollPane(gridPanel);

		mainPanel.add(gridPanelScroll, BorderLayout.CENTER);
		
		JPanel p3 = new JPanel();
		p3.setLayout(new FlowLayout());
		p3.add(new JButton(new SimpleSettingsNewDB()));
		p3.add(new JButton(new SimpleSettingsCancel()));
		p3.add(new JButton(new SimpleSettingsOK2()));
		mainPanel.add(p3, BorderLayout.SOUTH);
	}

	@Override
	public AppUIPanelType getUIPanelType() {
		return LapaeUIPanelSingletonType.simpleSettings;
	}
	
	public class SimpleSettingsOK2 extends AbstractAction {
		
		private static final long serialVersionUID = 1L;


		public SimpleSettingsOK2() {
			super("OK");
		}
		
		@Override
		public void actionPerformed(ActionEvent arg0) {
			DataManagerSingleton dm = app.getDataManagerSingleton(WorkSpaceSettings.class);
			WorkSpaceSettings as = (WorkSpaceSettings)dm.get();
			
			List<WSDB> dbList = new ArrayList<WSDB>();
			for (int i = 0; i < dUrl.size(); i++) {
				String url = dUrl.get(i).getText();
				if (url.trim().length() > 0) {
					WSDB db = new WSDB();
					db.databaseUrl = url;
					db.databaseUserName = dUserName.get(i).getText();
					db.databasePassword = dPassword.get(i).getText();
					db.group = dGroup.get(i).getText();
					dbList.add(db);
				}
			}
			as.dbList = dbList;

			as.webUrl = dWebUrl.getText();
		//	as.ftpUrl = dFTPUrl.getText();
		//	as.ftpUserName = dFTPUserName.getText();
		//	as.ftpPassword = dFTPPassword.getText();
			as.groups = dGroups.getCheckedKeyList();
			dm.save();
			app.getTabManager().removeTab(LapaeUIPanelSingletonType.simpleSettings.getKey());

			for (EditDBUrl e : editorPanels) {
				e.dispose();
			}
		}

	}

	public class SimpleSettingsCancel extends AbstractAction {
		private static final long serialVersionUID = 1L;
		public SimpleSettingsCancel() {
			super("Cancel");

		}
		@Override
		public void actionPerformed(ActionEvent arg0) {
			app.getTabManager().removeTab(LapaeUIPanelSingletonType.simpleSettings.getKey());
			for (EditDBUrl e : editorPanels) {
				e.dispose();
			}
		}
	}

	public class EditDBUrlAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		JTextField tf;

		public EditDBUrlAction(JTextField tfParam) {
			super("Edit");
			tf = tfParam;
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			editorPanels.add(new EditDBUrl(tf));
		}
	}
	
	public class SimpleSettingsNewDB extends AbstractAction {
		private static final long serialVersionUID = 1L;
		
		public SimpleSettingsNewDB() {
			super("Add DB");
		}
		
		@Override
		public void actionPerformed(ActionEvent arg0) {
			addDB(new WSDB());
			gridPanel.revalidate();
		}
	}

	@Override
	public HashMap<String,Object> getQuitingObjectSave() {
		// TODO Auto-generated method stub
		return new HashMap<String,Object>();
	}

	@Override
	public void initStartingUp(HashMap<String,Object> quitingObjectSave) {
		// TODO Auto-generated method stub
		
	}

	public JComponent getJComponent() {
		return mainPanel;
	}

	public List<Action> getActionsForMenu() {
		List<Action> a = new ArrayList<>();
		a.add(new SimpleSettingsNewDB());
		return a;
	}
}
