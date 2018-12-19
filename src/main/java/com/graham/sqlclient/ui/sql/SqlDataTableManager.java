/* Copyright (C) 2016 Graham Anderson gandersonsw@gmail.com - All Rights Reserved */
package com.graham.sqlclient.ui.sql;

import com.graham.sqlclient.SqlClientApp;
import com.graham.sqlclient.ui.databrowser.DataBrowserUIPanel;
import com.graham.sqlclient.ui.sql.action.AddFilterToSql;
import com.graham.sqlclient.ui.sql.action.CompareDataRows;
import com.graham.sqlclient.ui.sql.action.CopyCSV;
import com.graham.sqlclient.ui.sql.action.CopySqlInsert;
import com.graham.tools.SearchContext;
import com.graham.tools.SqlInfo;
import com.graham.tools.SqlTools;
import oracle.sql.CLOB;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Created by grahamanderson on 6/18/16.
 */
public class SqlDataTableManager {

	private SQLCellRenderer cellRend;
	private SQLTableModel model;
	private JTable results;
	private String sqlThatGoesWithResultSet;

	public SqlDataTableManager(final SqlClientApp app, final SqlUIPanel sqlUi) {
		model = new SQLTableModel();
		results = new JTable(model);

		cellRend = new SQLCellRenderer();
		results.setDefaultRenderer(Object.class, cellRend);

		results.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		results.setCellSelectionEnabled(true);
		results.addMouseListener(new TableMouseApapter(app, sqlUi));
	}

	public int dataChanged(Object dataParam, boolean firstTime) {
		cellRend.setNotCurrent(dataParam == null);
		if (dataParam == null) {
			results.repaint();
			return -1; // todo not sure if this is handled correctly
		}
		model.dataChanged(dataParam, firstTime);
		if (firstTime) {
		//	data = (List<List<Object>>)dataParam;
		//	columnCount = data.size();
		//	rowCount = data.get(0).size() - 1;

		//	fireTableStructureChanged();

			int columnCount = model.getColumnCount();

			for (int colIndex = 0; colIndex < columnCount; colIndex++) {
				results.getColumnModel().getColumn(colIndex).setPreferredWidth(120);
			}
		} //else {
		//	int oldRowCount = rowCount + 1;
		//	rowCount = data.get(0).size() - 1;
		//	this.fireTableRowsInserted(oldRowCount, rowCount);
		//}
		//sqlUi.setFooterText(rowCount + " Rows so far...", false);
		return model.getRowCount();
	}

	public JTable getTable() {
		return results;
	}

	public List<List<Object>> getData() {
		return model.getData();
	}

	//public String getSqlThatGoesWithResultSet() {
	//	return sqlThatGoesWithResultSet;
	//}

	public void setSqlThatGoesWithResultSet(String sql) {
		sqlThatGoesWithResultSet = sql;
	}

	public boolean findText(SearchContext params) { //String text, boolean startFromBegining, boolean caseSensitive) {
		int colStart = 0;
		int rowStart = 0;
		if (!params.isStartFromBegining()) {
			colStart = results.getSelectedColumn() + 1;
			rowStart = results.getSelectedRow();
		}

		String searchText;
		if (!params.isCaseSensitive()) {
			searchText = params.getSearchText().toLowerCase();
		} else {
			searchText = params.getSearchText();
		}

		for (int row = rowStart; row < model.getRowCount(); row++) {
			for (int col = colStart; col < model.getColumnCount(); col++) {
				Object cellObject = model.getValueAt(row, col);

				if (cellObject != null) {
					String cellText = cellObject.toString();
					if (!params.isCaseSensitive()) {
						cellText = cellText.toLowerCase();
					}

					if (cellText.indexOf(searchText) != -1) {
						results.setCellSelectionEnabled(true);
						results.changeSelection(row, col, false, false);
						return true;
					}
				}

			}
			colStart = 0;
		}

		return false;
	}

	public void populateQuitingObjectSave(HashMap<String, Object> m) {
		m.put("model", model.getSerilizableData());
		m.put("lastSql", sqlThatGoesWithResultSet);
	}

	public void initStartingUp(HashMap<String, Object> quitingObjectSave) {
		if (quitingObjectSave.containsKey("model")) {
			dataChanged(quitingObjectSave.get("model"), true);
		}
		if (quitingObjectSave.containsKey("lastSql")) {
			sqlThatGoesWithResultSet = (String) quitingObjectSave.get("lastSql");
		}
	}

	class TableMouseApapter extends MouseAdapter {
		final SqlClientApp app;
		final SqlUIPanel sqlUi;
		public TableMouseApapter(final SqlClientApp appParam, final SqlUIPanel sqlUiParam) {
			app = appParam;
			sqlUi = sqlUiParam;
		}
		public void mouseClicked(MouseEvent evt) {
			if (SwingUtilities.isLeftMouseButton(evt)) {

				if (evt.getClickCount() == 2) {
					String colName = model.getColumnName(results.getSelectedColumn());
					Object obj = model.getValueAtTyped(results.getSelectedRow(), results.getSelectedColumn());
					if (obj == null) {
						return;
					}

					if (obj instanceof CLOB) {
						try {
							obj = SqlTools.getCLOBString((CLOB) obj);
						} catch (SQLException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (IOException e2) {
							// TODO Auto-generated catch block
							e2.printStackTrace();
						}
					}

					app.appsh.openTextEditor(
						colName + ":" + (results.getSelectedRow() + 1),
						obj.toString());
				}
			} else if (SwingUtilities.isRightMouseButton(evt) || evt.isPopupTrigger()) {
				JPopupMenu contextMenu;
				contextMenu = new JPopupMenu();
				contextMenu.add(new CopyCSV(model));

				int row = results.rowAtPoint(evt.getPoint());
				int col = results.columnAtPoint(evt.getPoint());

				String colName = model.getColumnName(col);
				Object obj = model.getValueAtTyped(row, col);

				SqlInfo sqlInfo = null;
				if (sqlThatGoesWithResultSet != null) {
					sqlInfo = SqlTools.parseSQL(sqlThatGoesWithResultSet);
				}

				if (sqlInfo != null) {
					Iterator<String> tableNames = sqlInfo.tables.iterator();
					if (tableNames.hasNext()) {
						contextMenu.add(new CopySqlInsert(tableNames.next(), model, results));
					}
				}

				if (sqlInfo != null && obj != null) {
					SqlDataElementContext dataContext = new SqlDataElementContext(sqlUi.getDBGroupName(), colName, obj.toString(), model, row, col, sqlInfo);
					List<AbstractAction> openDataBrowserActions = DataBrowserUIPanel.getOpenAction(app, dataContext);
					for (AbstractAction a : openDataBrowserActions) {
						contextMenu.add(a);
					}
				}

				contextMenu.add(new CompareDataRows(app, "qwerty", model, results));

				if (sqlInfo != null) {
					Iterator<String> tableNamesIter = sqlInfo.tables.iterator();
					while (tableNamesIter.hasNext()) {
						String tableName = tableNamesIter.next();
						contextMenu.add(new AddFilterToSql(tableName, false, sqlInfo, model, results, sqlUi));
						contextMenu.add(new AddFilterToSql(tableName, true, sqlInfo, model, results, sqlUi));
					}
				}

				contextMenu.show(evt.getComponent(), evt.getX(), evt.getY());
			}
		}
	}

}
