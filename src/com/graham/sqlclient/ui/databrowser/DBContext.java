/* Copyright (C) 2016 Graham Anderson gandersonsw@gmail.com - All Rights Reserved */
package com.graham.sqlclient.ui.databrowser;

import java.util.List;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;

import com.graham.sqlclient.SqlClientApp;

/**
 * Context for when an editor is created from the data browser
 *
 */
public class DBContext {
	SqlClientApp app;
	JPanel                         mainPanel; // panel with NORTH=panelWithButtons CENTER=results and SOUTH=tabPanel
	int                            queryResultIndex;
	List<List<Object>>             queryResults;
	JScrollPane                    queryResultsPanel;
	String                         sql;
	DataBrowserDefinedTable        t;
	JTabbedPane                    tabPane;
	DataBrowserTableModel          jtableModel;
	JTable                         queryResultsTable;
	DataBrowserDefinedRelationship rel; // can be null
	String                         sqlPredicate; // the sql string used to find this row
	JTextField                     sqlTextBox;
	JTextField                     columnFilterField;
	RelationshipTracker            relshipTracker;
	DBCellRender                   cellRender;
	String						dbGroupname;
}
