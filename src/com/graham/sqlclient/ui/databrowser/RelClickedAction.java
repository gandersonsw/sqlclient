/* Copyright (C) 2016 Graham Anderson gandersonsw@gmail.com - All Rights Reserved */
package com.graham.sqlclient.ui.databrowser;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;

import com.graham.sqlclient.plugins.methodcall.JavaMethodCall;
import com.graham.sqlclient.plugins.methodcall.JavaMethodCallManager;
import com.graham.sqlclient.plugins.methodcall.JavaMethodCallTable;
import com.graham.appshell.data.AppData;

public class RelClickedAction extends AbstractAction {
	private static final long serialVersionUID = 1L;

	private DataBrowserDefinedRelationship r2;
	private DBContext context;
	
	public RelClickedAction(
			DataBrowserDefinedRelationship r2Param,
			DBContext contextParam) {
		super(r2Param.displayName);
		
		context = contextParam;
		if (context.queryResults == null || context.queryResultsPanel == null || context.mainPanel == null) {
			throw new IllegalArgumentException();
		}
		
		r2 = r2Param;
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		String sqlPredicate = "";
		DataBrowserDefinedTable t2 = null;
		String sql = null;
		Map<String, Object> javaMethodParams = null;
		if (r2.toTable.equals(JavaMethodCall.JavaMethodCallTableName)) {

			javaMethodParams = new HashMap<String, Object>();
			
			StringTokenizer stTo = new StringTokenizer(r2.toColumn, ",");
			StringTokenizer stFrom = new StringTokenizer(r2.fromColumn, ",");
			sql = "SELECT * FROM " + r2.toTable + " WHERE ";
			
			boolean isFirst = true;
			while (stTo.hasMoreTokens() && stFrom.hasMoreTokens()) {
				String toCol = stTo.nextToken();
				String fromCol = stFrom.nextToken();
				for (int i = 0; i < context.queryResults.size(); i++) {
					if (context.queryResults.get(i).get(0).toString().equalsIgnoreCase(fromCol)) {
						Object ob7 = context.queryResults.get(i).get(context.queryResultIndex);
						if (ob7 == null) {
							return;
						}
						javaMethodParams.put(toCol, ob7.toString());
						sqlPredicate += (isFirst ? "" : " AND ") + toCol + " = '" + ob7.toString() + "'";
						isFirst = false;
					}
				}
			}
			
			
		//	Collection<AppData> tables = context.app.appsh.getDataManagerList(DataBrowserDefinedTable.CLASS_ID).getList();
		//	for (AppData t : tables) {
		//		if (((DataBrowserDefinedTable)t).tableName.equalsIgnoreCase(r2.toTable)) {
					t2 = JavaMethodCallTable.getInstance();
		//		}
		//	}
			
			if (r2.otherSQL != null && r2.otherSQL.trim().length() > 0) {
				javaMethodParams.put("otherSQL", r2.otherSQL);
			}
			
			sql = sql + sqlPredicate;
			
			
			
			
			
			
			
			
			
			
			
		} else {
			StringTokenizer stTo = new StringTokenizer(r2.toColumn, ",");
			StringTokenizer stFrom = new StringTokenizer(r2.fromColumn, ",");
			sql = "SELECT * FROM " + r2.toTable + " WHERE ";
			
			boolean isFirst = true;
			while (stTo.hasMoreTokens() && stFrom.hasMoreTokens()) {
				String toCol = stTo.nextToken();
				String fromCol = stFrom.nextToken();
				for (int i = 0; i < context.queryResults.size(); i++) {
					if (context.queryResults.get(i).get(0).toString().equalsIgnoreCase(fromCol)) {
						Object ob7 = context.queryResults.get(i).get(context.queryResultIndex);
						if (ob7 == null) {
							return;
						}
						sqlPredicate += (isFirst ? "" : " AND ") + toCol + " = '" + ob7.toString() + "'";
						isFirst = false;
					}
				}
			}
			
			
			Collection<AppData> tables = context.app.appsh.getDataManagerList(DataBrowserDefinedTable.class).getList();
			for (AppData t : tables) {
				if (((DataBrowserDefinedTable)t).tableName.equalsIgnoreCase(r2.toTable)) {
					t2 = (DataBrowserDefinedTable)t;
				}
			}
			
			if (r2.otherSQL != null && r2.otherSQL.trim().length() > 0) {
				sqlPredicate += " and " + r2.otherSQL;
			}
			
			sql = sql + sqlPredicate;
			
			
			
			
			
			
			
			
			
			
			
			
			
			
		}
		


		
		if (context.tabPane == null) {
			context.tabPane = new JTabbedPane();
			makeSubcontext2(context, t2, sql, r2, sqlPredicate, javaMethodParams); // create panel with buttons, and query results

			context.mainPanel.remove(context.queryResultsPanel);
		
			JPanel newLayout = new JPanel(new BorderLayout());
			newLayout.add(context.queryResultsPanel, BorderLayout.NORTH);
			newLayout.add(context.tabPane, BorderLayout.CENTER);
		
			context.mainPanel.add(newLayout, BorderLayout.CENTER);
		
			// set prefferend sice of query results to 100 pix
			context.queryResultsPanel.setMinimumSize(new Dimension(1,52));
			context.queryResultsPanel.setPreferredSize(new Dimension(100,52));
			context.queryResultsPanel.setMaximumSize(new Dimension(10000,52));
			context.jtableModel.setVerticalOrientationFlag(false);
		} else {
			makeSubcontext2(context, t2, sql, r2, sqlPredicate, javaMethodParams); // create panel with buttons, and query results
		}
		context.mainPanel.revalidate();
	}
	
	public static boolean makeSubcontext1(
			DBContext parentContext, 
			DataBrowserDefinedTable t, 
			String sql, 
			DataBrowserDefinedRelationship relparam,
			String sqlPredicateParam) {
		
		List<List<Object>> results = parentContext.app.runOneSelect(sql, 15, parentContext.dbGroupname);
		makeSubcontext99(parentContext, t, sql, relparam, sqlPredicateParam, results);
		return parentContext.app.isResultsNotEmpty(results);
	}
		
	public static boolean makeSubcontext2(
			DBContext parentContext, 
			DataBrowserDefinedTable t, 
			String sql, 
			DataBrowserDefinedRelationship relparam,
			String sqlPredicateParam,
			Map<String, Object> javaMethodParams) {
		
		List<List<Object>> results;
		if (javaMethodParams == null) {
			results = parentContext.app.runOneSelect(sql, 15, parentContext.dbGroupname);
		} else {
			results = JavaMethodCallManager.execute(relparam.displayName, javaMethodParams, parentContext.app);
		}
		
		makeSubcontext99(parentContext, t, sql, relparam, sqlPredicateParam, results);
		return parentContext.app.isResultsNotEmpty(results);
	}
	
	
	public static void makeSubcontext99(
			DBContext parentContext, 
			DataBrowserDefinedTable t, 
			String sql, 
			DataBrowserDefinedRelationship relparam,
			String sqlPredicateParam,
			List<List<Object>> results) {

		for (int i = 1; i < results.get(0).size(); i++) {
			parentContext.sqlTextBox.setText(sql);

			DBContext childContext = new DBContext();
			childContext.rel = relparam;
			childContext.sqlTextBox = parentContext.sqlTextBox;
			childContext.sqlPredicate = sqlPredicateParam;
			childContext.app = parentContext.app;
			childContext.queryResultIndex = i;
			childContext.queryResults = results;
			childContext.sql = sql;
			childContext.t = t;
			childContext.dbGroupname = parentContext.dbGroupname;
			
			childContext.jtableModel = new DataBrowserTableModel(results, i);
			childContext.queryResultsTable = new JTable(childContext.jtableModel);
			
			
			childContext.cellRender = new DBCellRender(childContext);
			childContext.relshipTracker = new RelationshipTracker();
			childContext.queryResultsTable.setDefaultRenderer(String.class, childContext.cellRender);

			
			childContext.jtableModel.setTable1(childContext.queryResultsTable);
			childContext.queryResultsTable.setCellSelectionEnabled(true);
			childContext.queryResultsTable.addMouseListener(new TableClickWatcher(childContext));
			
			childContext.queryResultsPanel = new JScrollPane(childContext.queryResultsTable);
			childContext.mainPanel = new JPanel(new BorderLayout());
			childContext.mainPanel.add(childContext.queryResultsPanel, BorderLayout.CENTER);
			JPanel buttons = new JPanel();
			
			childContext.columnFilterField = new JTextField("*");
			childContext.columnFilterField.setColumns(7);
			childContext.columnFilterField.setToolTipText("Column name filter");
			buttons.add(childContext.columnFilterField);
			
			
			buttons.add(new JButton(new NewRelAction(childContext.t, childContext.app)));
			childContext.mainPanel.add(buttons, BorderLayout.NORTH);
			parentContext.tabPane.add(childContext.t.shortName, childContext.mainPanel);
			RelChangeWatcher tmp = new RelChangeWatcher(buttons, childContext);
			childContext.app.appsh.getDataManagerList(DataBrowserDefinedRelationship.class).addDataChangeListener(tmp);
//TODO listenersToUnregister.add(tmp);
System.out.println("todo listenersToUnregister.add(tmp);");
			if (i == 1) {
				parentContext.tabPane.setSelectedComponent(childContext.mainPanel);
			}
			
			childContext.columnFilterField.getDocument().addDocumentListener(new DBColumnFilterChangeListener(childContext));
		}
	}
	
	public static String createNameValues(List<List<Object>> result, int i) {
		StringBuilder sb = new StringBuilder();
		for (int j = 0; j < result.size(); j++) {
			sb.append(result.get(j).get(0));
			sb.append(":");
			sb.append(result.get(j).get(i));
			sb.append("\n");
		}
		return sb.toString();
	}
	
}