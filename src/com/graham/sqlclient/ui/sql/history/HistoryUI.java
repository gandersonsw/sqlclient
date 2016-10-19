/* Copyright (C) 2016 Graham Anderson gandersonsw@gmail.com - All Rights Reserved */
package com.graham.sqlclient.ui.sql.history;

import java.awt.BorderLayout;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;

import com.graham.sqlclient.ui.sql.SqlUIPanel;
import com.graham.appshell.App;


public class HistoryUI {
	
	private JTextField sqlHistoryFilterField;
	private JCheckBox loadResultsToo;
	private SqlHistoryTableModel model;
	
	public HistoryUI() {
	}
// TODO
	public JPanel createHistoryPanel(App appParam, SqlUIPanel sqlUi) {
		JPanel p = new JPanel(new BorderLayout());
		loadResultsToo = new JCheckBox("Dbl-Clk Load Results Also");
		p.add(createHistPanel(appParam, sqlUi), BorderLayout.CENTER);
		p.add(createTopPanel(), BorderLayout.NORTH);
		model.setFilterTextField(sqlHistoryFilterField);
		return p;
	}
	
	private JScrollPane createHistPanel(App appParam, SqlUIPanel sqlUi) {
		
		model = new SqlHistoryTableModel(appParam);
		JTable hist = new JTable(model);
		model.setTable1(hist);
		hist.addMouseListener(new HistTableClickWatcher(model, hist, sqlUi, loadResultsToo));
		JScrollPane scollHist = new JScrollPane(hist);
		return scollHist;
	}
	
	private JPanel createTopPanel() {
		JPanel top = new JPanel();
		
		top.add(new JLabel("Filter:"));
		sqlHistoryFilterField = new JTextField("");
		sqlHistoryFilterField.setColumns(15);
		top.add(sqlHistoryFilterField);
		sqlHistoryFilterField.getDocument().addDocumentListener(model.getTextFilterCL());
		
		
		top.add(loadResultsToo);
		
		return top;
	}

	public boolean close() {
		model.close();
		return true;
	}
	
}
