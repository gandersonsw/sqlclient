/* Copyright (C) 2016 Graham Anderson gandersonsw@gmail.com - All Rights Reserved */
package com.graham.sqlclient;

import com.graham.tools.UITools;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class EditDBUrl {

	private JFrame editor;

	private JTextField urlText;
	private JTextField hostNameTF;
	private JTextField portTF;
	private JTextField sidTF;
	private JTextField serviceNameTF;

	private JRadioButton serviceNameButton;
	private JRadioButton sidButton;

	private GridBagLayout gridbag;
	private GridBagConstraints labelConstr;
	private GridBagConstraints fieldConstr;

	private JTextField driverClassTF;

	static class ParsedUrl {
		String driverClass;
		String hostName;
		String port;
		String sid;
		String serviceName;
	}

	public EditDBUrl(JTextField urlTextParam) {

		urlText = urlTextParam;

		ParsedUrl parsed = parseUrl(urlText.getText());

		JPanel gridPanel = new JPanel();
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

		int gridCompIndex = 0;
		{
			JLabel lab = new JLabel("Driver Class:");
			gridbag.setConstraints(lab, labelConstr);
			gridPanel.add(lab, gridCompIndex++);
			driverClassTF = new JTextField();
			gridbag.setConstraints(driverClassTF, fieldConstr);
			if (parsed.driverClass == null || parsed.driverClass.length() == 0) {
				driverClassTF.setText("jdbc:oracle:thin");
			} else {
				driverClassTF.setText(parsed.driverClass);
			}
			gridPanel.add(driverClassTF, gridCompIndex++);
		}
		{
			JLabel lab = new JLabel("Host Name:");
			gridbag.setConstraints(lab, labelConstr);
			gridPanel.add(lab, gridCompIndex++);
			hostNameTF = new JTextField();
			gridbag.setConstraints(hostNameTF, fieldConstr);
			hostNameTF.setText(parsed.hostName);
			gridPanel.add(hostNameTF, gridCompIndex++);
		}
		{
			JLabel lab = new JLabel("Port:");
			gridbag.setConstraints(lab, labelConstr);
			gridPanel.add(lab, gridCompIndex++);
			portTF = new JTextField();
			gridbag.setConstraints(portTF, fieldConstr);
			portTF.setText(parsed.port);
			gridPanel.add(portTF, gridCompIndex++);
		}
		ButtonGroup group = new ButtonGroup();
		{
			sidButton = new JRadioButton("SID");
			if (parsed.sid != null && parsed.sid.length() > 0) {
				sidButton.setSelected(true);
			}
			gridbag.setConstraints(sidButton, labelConstr);
			gridPanel.add(sidButton, gridCompIndex++);
			sidTF = new JTextField();
			gridbag.setConstraints(sidTF, fieldConstr);
			sidTF.setText(parsed.sid);
			gridPanel.add(sidTF, gridCompIndex++);
			group.add(sidButton);
		}
		{
			serviceNameButton = new JRadioButton("Service Name");
			if (parsed.serviceName != null && parsed.serviceName.length() > 0) {
				serviceNameButton.setSelected(true);
			}
			gridbag.setConstraints(serviceNameButton, labelConstr);
			gridPanel.add(serviceNameButton, gridCompIndex++);
			serviceNameTF = new JTextField();
			gridbag.setConstraints(serviceNameTF, fieldConstr);
			serviceNameTF.setText(parsed.serviceName);
			gridPanel.add(serviceNameTF, gridCompIndex++);
			group.add(serviceNameButton);
		}

		JPanel bottomButtonPanel = new JPanel();
		bottomButtonPanel.add(new JButton(new CancelEditDBUrl()));
		bottomButtonPanel.add(new JButton(new SaveEditDBUrl()));


		JPanel borderPanel = new JPanel(new BorderLayout());
		borderPanel.add(gridPanel, BorderLayout.CENTER);
		borderPanel.add(bottomButtonPanel, BorderLayout.SOUTH);


		editor = new JFrame();
		editor.setTitle("Edit Database Url");
		editor.setContentPane(borderPanel);

		editor.pack();
		UITools.center(editor);

		editor.setVisible(true);

		//addAppQuitListener(new FloatingFrameQuitListener(te));
	}

	private ParsedUrl parseUrl(String urlText) {
		ParsedUrl url = new ParsedUrl();
		int atLoc = urlText.indexOf("@");
		if (atLoc != -1) {
			url.driverClass = urlText.substring(0, atLoc - 1);
			urlText = urlText.substring(atLoc + 1);
		}

		int colonLoc = urlText.indexOf(":");
		if (colonLoc != -1) {
			url.hostName = urlText.substring(0, colonLoc);
			urlText = urlText.substring(colonLoc + 1);
		}

		colonLoc = urlText.indexOf(":");
		if (colonLoc != -1) {
			url.port = urlText.substring(0, colonLoc);
			url.sid = urlText.substring(colonLoc + 1);
		}

		int slashLoc = urlText.indexOf("/");
		if (slashLoc != -1) {
			url.port = urlText.substring(0, slashLoc);
			url.serviceName = urlText.substring(slashLoc + 1);
		}

		return url;
	}

	private String createUrl(ParsedUrl u) {
		StringBuilder urlStr = new StringBuilder();

		urlStr.append(u.driverClass);
		urlStr.append(":@");
		urlStr.append(u.hostName);
		if (u.port != null && u.port.length() > 0) {
			urlStr.append(":");
			urlStr.append(u.port);
		}

		if (u.sid != null && u.sid.length() > 0) {
			urlStr.append(":");
			urlStr.append(u.sid);
		}

		if (u.serviceName != null && u.serviceName.length() > 0) {
			urlStr.append("/");
			urlStr.append(u.serviceName);
		}

		return urlStr.toString();
	}

	private ParsedUrl populateUrlFromUI() {
		ParsedUrl u = new ParsedUrl();
		u.driverClass = driverClassTF.getText().trim();
		u.hostName = hostNameTF.getText().trim();
		u.port = portTF.getText().trim();
		if (serviceNameButton.isSelected()) {
			u.serviceName = serviceNameTF.getText().trim();
		}
		if (sidButton.isSelected()) {
			u.sid = sidTF.getText().trim();
		}
		return u;
	}

	public void dispose() {
		editor.setVisible(false);
	}

	class CancelEditDBUrl extends AbstractAction {
		public CancelEditDBUrl() {
			super("Cancel");
		}
		@Override
		public void actionPerformed(ActionEvent arg0) {
			editor.setVisible(false);
		}
	}

	class SaveEditDBUrl extends AbstractAction {
		public SaveEditDBUrl() {
			super("OK");
		}
		@Override
		public void actionPerformed(ActionEvent arg0) {
			ParsedUrl url = populateUrlFromUI();
			urlText.setText(createUrl(url));
			editor.setVisible(false);
		}
	}

}
