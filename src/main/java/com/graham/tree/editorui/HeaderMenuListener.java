/* Copyright (C) 2016 Graham Anderson gandersonsw@gmail.com - All Rights Reserved */
package com.graham.tree.editorui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;

public class HeaderMenuListener implements ActionListener {

	@Override
	public void actionPerformed(ActionEvent arg0) {
		System.out.println("at 101:" + arg0.getActionCommand());
		if (arg0.getSource() instanceof JComboBox) {
			JComboBox cb = (JComboBox)arg0.getSource();
			if (cb.getSelectedItem() instanceof TreeEditorCommand) {
				((TreeEditorCommand)cb.getSelectedItem()).doCommand();
			}
			cb.setSelectedIndex(0);
		}
	}

}
