/* Copyright (C) 2016 Graham Anderson gandersonsw@gmail.com - All Rights Reserved */
package com.graham.appshell.tabui;

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
import com.graham.appshell.data.*;
import com.graham.appshell.handlers.RemoveTabAction;

public class AppSettingsUIPanel extends AppUIPanelSingleton {
	
	private JTextField dDir;
	private App app;
	private DataUIList gl;
	private JPanel mainPanel;

	public AppSettingsUIPanel(App appParam) {
		app = appParam;
		JPanel p = new JPanel();
		
		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints labelConstr = new GridBagConstraints();
		labelConstr.weightx = 0.0;
		labelConstr.gridx = 1;
		labelConstr.anchor = GridBagConstraints.WEST;
		labelConstr.insets = new Insets(3,3,3,3);
		GridBagConstraints fieldConstr = new GridBagConstraints();
		fieldConstr.fill = GridBagConstraints.HORIZONTAL;
		fieldConstr.gridx = 2;
		fieldConstr.weightx = 1.0;
		fieldConstr.insets = new Insets(3,3,3,3);
		p.setLayout(gridbag);
		
		JLabel lab;
		


		
		lab = new JLabel("Base Directory:");
		gridbag.setConstraints(lab, labelConstr);
		p.add(lab);
		dDir = new JTextField();
		gridbag.setConstraints(dDir, fieldConstr);
		dDir.setText(AppScopeImpl.getPrefsAppHomePath());
		p.add(dDir);
		
		mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout(5, 5));

		mainPanel.add(p, BorderLayout.NORTH);
		
		JPanel p3 = new JPanel();
		p3.setLayout(new FlowLayout());
		
		p3.add(new JButton(new EditGroups()));
		p3.add(new JButton(new RemoveTabAction(appParam, AppUIPanelSingletonTypeImpl.AppSettings)));
		p3.add(new JButton(new AppSettingsOK()));
		mainPanel.add(p3, BorderLayout.SOUTH);
	}
	
	@Override
	public AppUIPanelType getUIPanelType() {
		return AppUIPanelSingletonTypeImpl.AppSettings;
	}
	
	public class AppSettingsOK extends AbstractAction {
		private static final long serialVersionUID = 1L;
		public AppSettingsOK() {
			super("OK");
		}
		@Override
		public void actionPerformed(ActionEvent arg0) {
			app.getTabManager().removeTab(AppUIPanelSingletonTypeImpl.AppSettings.getKey());
			if (!dDir.getText().equals(AppScopeImpl.getPrefsAppHomePath())) {
				AppScopeImpl.setPrefsAppHomePath(dDir.getText());
				System.exit(0);
			}
		}
	}
	
	public class EditGroups extends AbstractAction {
		private static final long serialVersionUID = 1L;
		public EditGroups() {
			super("Edit Scopes");
		}
		@Override
		public void actionPerformed(ActionEvent arg0) {
			List<AppDataScope> scopes = new ArrayList<AppDataScope>();
			scopes.add(app.getAppScope());

			if (gl == null || !gl.isVisibile()) {
				gl = new DataUIList(app.getDataManagerList(DataGroup.class), app, scopes);
				gl.initEditListWithEditItemUIWindow();
			} else {
				gl.toFront();
			}
		}
	}

	@Override
	public HashMap<String,Object> getQuitingObjectSave() {
		return new HashMap<String,Object>();
	}

	@Override
	public void initStartingUp(HashMap<String,Object> quitingObjectSave) {
		
	}

	public JComponent getJComponent() {
		return mainPanel;
	}

}
