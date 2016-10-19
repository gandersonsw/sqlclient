/* Copyright (C) 2016 Graham Anderson gandersonsw@gmail.com - All Rights Reserved */
package com.graham.tools;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

/**
 * Created by grahamanderson on 6/8/16.
 */
public class UICheckboxListDialog<T> {

	private JDialog dialogOwner;
	private Window windowOwner;
	private int buttonState; // 0 = waiting, 1 = cancel, 2 = ok

	public UICheckboxListDialog(JDialog owner) {
		dialogOwner = owner;
	}

	public UICheckboxListDialog(Window owner) {
		windowOwner = owner;
	}

	/**
	 * will return an empty list if "Cancel" button was pressed, or all checboxes are not selected.
	 */
	public ArrayList<T> chooseItems(ArrayList<T> items, boolean[] selected, String label) {
		buttonState = 0;
		JDialog d = dialogOwner != null ? new JDialog(dialogOwner, true) : new JDialog(windowOwner, Dialog.ModalityType.APPLICATION_MODAL);
		d.getContentPane().setLayout(new BorderLayout());

		JLabel jl = new JLabel(label);
		d.getContentPane().add(jl, BorderLayout.NORTH);

		JPanel schemaCheckList = new JPanel();
		schemaCheckList.setLayout(new GridLayout(items.size(), 1));
		ArrayList<JCheckBox> cbList = new ArrayList<>();
		for (int i = 0; i < items.size(); i++) {
			JCheckBox cb = new JCheckBox(items.get(i).toString());
			if (selected != null && i < selected.length && selected[i]) {
				cb.setSelected(true);
			}
			schemaCheckList.add(cb);
			cbList.add(cb);
		}
		d.getContentPane().add(schemaCheckList, BorderLayout.CENTER);

		JPanel buttonsPanel = new JPanel(new FlowLayout());
		JButton selAll = new JButton(new selectAllActionClass(cbList));
		buttonsPanel.add(selAll);
		JButton selNone = new JButton(new selectNoneActionClass(cbList));
		buttonsPanel.add(selNone);
		JButton cancel = new JButton(new cancelActionClass(d));
		buttonsPanel.add(cancel);
		JButton ok = new JButton(new okActionClass(d));
		buttonsPanel.add(ok);
		d.getContentPane().add(buttonsPanel, BorderLayout.SOUTH);

		UITools.setFrameSizeAndCenter(d, 400, 160);
		d.pack();
		d.setVisible(true);

		ArrayList<T> filteredItems = new ArrayList<>();

		try {

			while (buttonState == 0) {
				Thread.sleep(50);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		if (buttonState == 1) {
			return filteredItems;
		}

		for (int si = 0; si < items.size(); si++) {
			if (cbList.get(si).isSelected()) {
				filteredItems.add(items.get(si));
			}
		}

		return filteredItems;
	}

	public class cancelActionClass extends AbstractAction {
		private static final long serialVersionUID = 1L;
		JDialog d;
		public cancelActionClass(JDialog paramd) {
			super("Cancel");
			d = paramd;
		}
		public void actionPerformed(ActionEvent e) {
			d.dispose();
			buttonState = 1;
		}
	}

	public class okActionClass extends AbstractAction {
		private static final long serialVersionUID = 1L;
		JDialog d;
		public okActionClass(JDialog paramd) {
			super("Continue");
			d = paramd;
		}
		public void actionPerformed(ActionEvent e) {
			d.dispose();
			buttonState = 2;
		}
	}

	public static class selectAllActionClass extends AbstractAction {
		private static final long serialVersionUID = 1L;
		ArrayList<JCheckBox> cbList;
		public selectAllActionClass(ArrayList<JCheckBox> cbListParam) {
			super("Select All");
			cbList = cbListParam;
		}
		public void actionPerformed(ActionEvent e) {
			for (JCheckBox cb : cbList) {
				cb.setSelected(true);
			}
		}
	}

	public static class selectNoneActionClass extends AbstractAction {
		private static final long serialVersionUID = 1L;
		ArrayList<JCheckBox> cbList;
		public selectNoneActionClass(ArrayList<JCheckBox> cbListParam) {
			super("Select None");
			cbList = cbListParam;
		}
		public void actionPerformed(ActionEvent e) {
			for (JCheckBox cb : cbList) {
				cb.setSelected(false);
			}
		}
	}
}
