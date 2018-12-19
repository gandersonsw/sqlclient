/* Copyright (C) 2016 Graham Anderson gandersonsw@gmail.com - All Rights Reserved */
package com.graham.appshell.find;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.*;

import com.graham.appshell.App;
import com.graham.appshell.handlers.*;
import com.graham.tools.*;

public class OpenFindTextDialog extends AbstractAction {
	
	private static final long serialVersionUID = 1L;
	
	private App app;
	private JFrame findTextFrame;
	
	public OpenFindTextDialog(App appParam) {
		super("Find...");
		putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_F, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		app = appParam;
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		if (findTextFrame == null) {
			findTextFrame = createFindTextFrame();
			app.addAppQuitListener(new FloatingFrameQuitListener(findTextFrame));
		} else {
			findTextFrame.setVisible(true);
			findTextFrame.toFront();
		}
	}

	public JFrame createFindTextFrame() {
		JFrame findFrame = new JFrame();
		findFrame.setTitle("Find Text");
		JPanel mainPanel = new JPanel(new FlowLayout());

		CancelAction ca = new CancelAction(findFrame);
		SysWindow2 aSymWindow = new SysWindow2(ca);
		findFrame.addWindowListener(aSymWindow);

		JLabel results = new JLabel("ready to search");

		JComboBox cb = new JComboBox();
		cb.setEditable(true);
		mainPanel.add(cb);
		mainPanel.add(new JButton(ca));
		JButton jb = new JButton(new FindAction(app, cb, results));
		jb.setToolTipText("find from the begginging");
		mainPanel.add(jb);
		jb = new JButton(new FindNextAction(app, cb, results));
		jb.setToolTipText("find starting at the current selection");
		mainPanel.add(jb);

		JLabel replaceLabel = new JLabel("Replace with");
		JTextField replaceText = new JTextField(20);
		JButton replaceButton = new JButton(new ReplaceAllAction(app, cb, results, replaceText));
		JPanel secondPanel = new JPanel(new FlowLayout());
		secondPanel.add(replaceLabel);
		secondPanel.add(replaceText);
		secondPanel.add(replaceButton);

		JPanel mainPanel2 = new JPanel(new BorderLayout());
		mainPanel2.add(mainPanel, BorderLayout.NORTH);
		mainPanel2.add(secondPanel, BorderLayout.CENTER);
		mainPanel2.add(results, BorderLayout.SOUTH);

		findFrame.add(mainPanel2);
		findFrame.pack();
		UITools.center(findFrame);
		findFrame.setVisible(true);
		return findFrame;
	}

}
