/* Copyright (C) 2016 Graham Anderson gandersonsw@gmail.com - All Rights Reserved */
package com.graham.tools;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by grahamanderson on 6/8/16.
 */
public class UIProgressDialog {

	private JDialog d;
	private String[] tasks;
	private ArrayList<JLabel> labels;
	private boolean cancelWasPressed;
	private Date startTime;
	private JLabel totalTime;

	public void show(Frame owner, String[] tasksParam) {
		tasks = tasksParam;
		cancelWasPressed = false;

		if (owner == null)
			d = new JDialog();
		else
			d = new JDialog(owner, false);

		BoxLayout bl = new BoxLayout(d.getContentPane(), BoxLayout.Y_AXIS);
		d.getContentPane().setLayout(bl);

		labels = new ArrayList<JLabel>();
		for (String task : tasks) {
			JLabel l = new JLabel("[ - - ] " + task + "        ");
			labels.add(l);
			d.getContentPane().add(l);
		}

		totalTime = new JLabel("Task timer: 0                      ");
		d.getContentPane().add(totalTime);

		//    JButton cancel = new JButton(context);
		//   d.getContentPane().add(cancel);

		startTime = new Date();

		update(tasks[0], 0);

		d.setFocusable(true);
		d.setResizable(false);
		d.pack();
		Dimension oldSize = d.getSize();
		d.setSize(new Dimension(oldSize.width+20, oldSize.height));
		UITools.center(d);
		d.setVisible(true);

		for (Component c : d.getContentPane().getComponents()) {
			if (c instanceof JComponent) {
				Rectangle r = c.getBounds();
				r.x = 0;
				r.y = 0;
				((JComponent)c).paintImmediately(r);
			}
		}
	}

	/**
	 * @param data map returned from showProgressDialog
	 * @param task
	 * @param percentDone 0 to 100
	 * @return true if the cancel button was pressed
	 */
	public boolean update(String task, int percentDone) {
		for (int i = 0; i < tasks.length; i++) {
			if (tasks[i].equals(task)) {
				JLabel l = labels.get(i);
				l.setText("[" + percentDone + "%] " + task + "      ");
				Rectangle r = l.getBounds();
				r.x = 0;
				r.y = 0;
				//	r.width += 50;
				l.paintImmediately(r);

				break;
			}
		}
		long s = startTime.getTime();
		totalTime.setText("Task timer: " + DateTools.formatTimeSpan(System.currentTimeMillis() - s) + "               ");
		Rectangle r = totalTime.getBounds();
		r.x = 0;
		r.y = 0;
		//	r.width += 50;
		totalTime.paintImmediately(r);
		return cancelWasPressed;
	}

	public void close() {
		d.dispose();
	}

	public JDialog getDialog() {
		return d;
	}
}
