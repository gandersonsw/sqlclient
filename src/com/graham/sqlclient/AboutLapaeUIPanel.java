/* Copyright (C) 2016 Graham Anderson gandersonsw@gmail.com - All Rights Reserved */
package com.graham.sqlclient;

import com.graham.appshell.tabui.AppUIPanelSingleton;
import com.graham.appshell.tabui.AppUIPanelSingletonTypeImpl;
import com.graham.appshell.tabui.AppUIPanelType;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.util.HashMap;

/**
 * Created by grahamanderson on 10/15/16.
 */
public class AboutLapaeUIPanel extends AppUIPanelSingleton {

	final public static String EMAIL = "gandersonsw@gmail.com";

	static class CopyEmail extends AbstractAction {
		public CopyEmail() {
			super("Copy Email");
		}
		public void actionPerformed(ActionEvent e) {
			StringSelection ss = new StringSelection(EMAIL);
			Toolkit.getDefaultToolkit().getSystemClipboard().setContents(ss, null);
		}
	}

	private JPanel mainPanel;

	public AboutLapaeUIPanel() {
		mainPanel = new JPanel(new BorderLayout());

		JPanel p55 = new JPanel();
		p55.setLayout(new BoxLayout(p55, BoxLayout.PAGE_AXIS));
		p55.add(new JLabel(" "));

		JLabel l = new JLabel("SqlClient 1.0");
		l.setHorizontalAlignment(SwingConstants.CENTER);
		l.setFont(l.getFont().deriveFont(20.0f));
		JPanel cp = new JPanel(new BorderLayout());
		cp.add(l, BorderLayout.CENTER);
		p55.add(cp);
		p55.add(new JLabel(" "));

		l = new JLabel("Copyright © 2016 Graham Anderson.");
		l.setHorizontalAlignment(SwingConstants.CENTER);
		cp = new JPanel(new BorderLayout());
		cp.add(l, BorderLayout.CENTER);
		p55.add(cp);

		l = new JLabel("All rights reserved.");
		l.setHorizontalAlignment(SwingConstants.CENTER);
		cp = new JPanel(new BorderLayout());
		cp.add(l, BorderLayout.CENTER);
		p55.add(cp);
		p55.add(new JLabel(" "));

		l = new JLabel("Contact author at: " + EMAIL);
		l.setHorizontalAlignment(SwingConstants.CENTER);
		cp = new JPanel(new BorderLayout());
		cp.add(l, BorderLayout.CENTER);
		p55.add(cp);
		p55.add(new JLabel(" "));

		JButton ce = new JButton(new CopyEmail());
		cp = new JPanel();
		cp.add(ce);
		p55.add(cp);

		mainPanel.add(p55, BorderLayout.NORTH);
	}

	@Override
	public JComponent getJComponent() {
		return mainPanel;
	}

	@Override
	public HashMap<String, Object> getQuitingObjectSave() {
		HashMap<String,Object> data = new HashMap<String,Object>();
		return data;
	}

	@Override
	public void initStartingUp(HashMap<String, Object> quitingObjectSave) {

	}

	@Override
	public AppUIPanelType getUIPanelType() {
		return AppUIPanelSingletonTypeImpl.AppAbout;
	}
}
