/* Copyright (C) 2016 Graham Anderson gandersonsw@gmail.com - All Rights Reserved */
package com.graham.sqlclient.ui.set;

import java.awt.*;
import java.util.*;
import java.util.List;

import javax.swing.*;

import com.graham.appshell.tabui.*;
import com.graham.sqlclient.LapaeUIPanelMultipleType;
import com.graham.tools.SearchContext;
import com.graham.tools.UITools;

public class SetUIPanel extends AppUIPanelMultiple {

	private JPanel mainPanel;
	private SetUIPanelSet set1p;
	private SetUIPanelSet set2p;
	private SetUIPanelSet set3p;

	private List<Action> actionsForMenu;

	public SetUIPanel() {

		JPanel setsPanel = new JPanel(new GridLayout(1,3));
		
		set1p = createSetUIPanelSet(setsPanel);
		set2p = createSetUIPanelSet(setsPanel);
		set3p = createSetUIPanelSet(setsPanel);

		JPanel buttonPanel = new JPanel(new FlowLayout());
		
		set1p.radio = new JRadioButton("List 1");
		set1p.radio.setSelected(true);
		set2p.radio = new JRadioButton("List 2");
		set3p.radio = new JRadioButton("List 3");
		ButtonGroup bgroup = new ButtonGroup();
		bgroup.add(set1p.radio);
		bgroup.add(set2p.radio);
		bgroup.add(set3p.radio);
		
		SetUIPanelParser parser = new SetUIPanelParser();
		parser.addSet(set1p);
		parser.addSet(set2p);
		parser.addSet(set3p);
		
		buttonPanel.add(set1p.radio);
		buttonPanel.add(set2p.radio);
		buttonPanel.add(set3p.radio);

		actionsForMenu = new ArrayList<>();
		Action a = new CleanList(parser);
		actionsForMenu.add(a);
		JButton b = new JButton(a);
		b.setToolTipText("clean and sort using given format or newline or tab or space or comma");
		buttonPanel.add(b);
		a = new RemoveDups(parser);
		actionsForMenu.add(a);
		b = new JButton(a);
		b.setToolTipText("output goes to 3rd column");
		buttonPanel.add(b);
		a = new ListDups(parser);
		actionsForMenu.add(a);
		b = new JButton(a);
		b.setToolTipText("output goes to 3rd column");
		buttonPanel.add(b);
		a = new FormatForSQL(parser);
		actionsForMenu.add(a);
		b = new JButton(a);
		b.setToolTipText("output goes to 3rd column");
		buttonPanel.add(b);
		a = new Intersection(parser);
		actionsForMenu.add(a);
		b = new JButton(a);
		b.setToolTipText("list 1 AND list2, output goes to 3rd column");
		buttonPanel.add(b);
		a = new SetMinus(parser);
		actionsForMenu.add(a);
		b = new JButton(a);
		b.setToolTipText("list 1 - list 2, output goes to 3rd column");
		buttonPanel.add(b);
		a = new SetTextSearchAll(parser);
		actionsForMenu.add(a);
		b = new JButton(a);
		b.setToolTipText("for each item in list 1, search for it in the text in column 2, display each found item from list 1 in column 3");
		buttonPanel.add(b);

		mainPanel = new JPanel(new BorderLayout());
		mainPanel.add(setsPanel, BorderLayout.CENTER);
		mainPanel.add(buttonPanel, BorderLayout.NORTH);
	}
	
	private SetUIPanelSet createSetUIPanelSet(JPanel setsPanel) {
		SetUIPanelSet part = new SetUIPanelSet();
		
		JPanel setCol = new JPanel(new BorderLayout());
		
		JPanel formatPanel = new JPanel(new FlowLayout());
		formatPanel.add(new JLabel("Format:"));
		part.format = new JTextField("1\\t2\\n", 12);
		formatPanel.add(part.format);
		setCol.add(formatPanel, BorderLayout.NORTH);
		
		part.text = new JTextArea();
		Font font = new Font("Courier New", Font.PLAIN, 12);
		part.text.setFont(font);
		setCol.add(new JScrollPane(part.text), BorderLayout.CENTER);
		
		part.summary = new JLabel("0 Items");
		setCol.add(part.summary, BorderLayout.SOUTH);
		setsPanel.add(setCol);
		
		return part;
	}

	@Override
	public AppUIPanelType getUIPanelType() {
		return LapaeUIPanelMultipleType.sets;
	}

	@Override
	public HashMap<String,Object> getQuitingObjectSave() {
		HashMap<String, Object> m = new HashMap<String, Object>();
		m.put("set1", set1p.text.getText());
		m.put("set2", set2p.text.getText());
		m.put("set3", set3p.text.getText());
		m.put("set1label", set1p.summary.getText());
		m.put("set2label", set2p.summary.getText());
		m.put("set3label", set3p.summary.getText());
		m.put("format1", set1p.format.getText());
		m.put("format2", set2p.format.getText());
		m.put("format3", set3p.format.getText());
		return m;
	}

	@Override
	public void initStartingUp(HashMap<String,Object> quitingObjectSave) {
		if (quitingObjectSave != null) {
			set1p.text.setText((String)quitingObjectSave.get("set1"));
			set2p.text.setText((String)quitingObjectSave.get("set2"));
			set3p.text.setText((String)quitingObjectSave.get("set3"));
			set1p.summary.setText((String)quitingObjectSave.get("set1label"));
			set2p.summary.setText((String)quitingObjectSave.get("set2label"));
			set3p.summary.setText((String)quitingObjectSave.get("set3label"));
			if (quitingObjectSave.containsKey("format1")) {
				set1p.format.setText((String)quitingObjectSave.get("format1"));
			}
			if (quitingObjectSave.containsKey("format2")) {
				set2p.format.setText((String)quitingObjectSave.get("format2"));
			}
			if (quitingObjectSave.containsKey("format3")) {
				set3p.format.setText((String)quitingObjectSave.get("format3"));
			}
		}
	}

	public JComponent getJComponent() {
		return mainPanel;
	}

	public List<Action> getActionsForMenu() {
		return actionsForMenu;
	}

	public boolean findText(SearchContext params) {
	//	Component c = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();

		JTextArea ta = null;
		if (set1p.radio.isSelected()) {
			return UITools.findTextInTextArea(set1p.text, params);
		//	set1p.text.scroll
		} else if (set2p.radio.isSelected()) {
			return UITools.findTextInTextArea(set2p.text, params);
		} else if (set3p.radio.isSelected()) {
			return UITools.findTextInTextArea(set3p.text, params);
		}
		return false;
	}

}
