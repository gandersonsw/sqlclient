/* Copyright (C) 2016 Graham Anderson gandersonsw@gmail.com - All Rights Reserved */
package com.graham.sqlclient.ui.databrowser;

import com.graham.appshell.data.AppData;
import com.graham.sqlclient.SqlClientApp;
import com.graham.sqlclient.ui.sql.SqlDataElementContext;
import com.graham.sqlclient.ui.sqltablebrowser.DBMetaSchema;
import com.graham.sqlclient.ui.sqltablebrowser.DBMetaTable;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Created by grahamanderson on 10/16/16.
 */
public class OpenBrowserForSqlDataElementAction extends AbstractAction {

	private SqlClientApp app;
	private SqlDataElementContext context;
	private boolean openEditor;

	public OpenBrowserForSqlDataElementAction(SqlClientApp appParam, SqlDataElementContext contextParam, boolean openEditorParam) {
		super((openEditorParam ? "Open Editor for \"" : "Open Browser for \"") + contextParam.fieldValue + "\"");
		context = contextParam;
		openEditor = openEditorParam;
		app = appParam;
	}

	public void actionPerformed(ActionEvent e) {

		DataBrowserUIPanel browser = null;;

		Iterator<String> tableNamesIter = context.sqlInfo.tables.iterator();
		while (tableNamesIter.hasNext()) {
			String tableName = tableNamesIter.next();

			boolean foundColumn = false;

			Collection<AppData> schemas = app.appsh.getDataManagerList(DBMetaSchema.class).getList();
			for (AppData sappd : schemas) {
				DBMetaSchema schema = (DBMetaSchema)sappd;
				DBMetaTable tableMD = (DBMetaTable) app.appsh.getDataManagerList(DBMetaTable.class).getByPrimaryKey(DBMetaTable.generatePrimaryKey(schema.name, tableName));
				if (tableMD != null) {
					for (int i = 0; i < tableMD.columns.size(); i++) {
						if (tableMD.columns.get(i).name.equalsIgnoreCase(context.columnName)) {
							foundColumn = true;
						}
					}
					if (foundColumn) {
						DataBrowserDefinedTable dbdt = (DataBrowserDefinedTable) app.appsh.getDataManagerList(DataBrowserDefinedTable.class).getByPrimaryKey(tableName);

						int[] columnIndexArr = context.model.getColumnWithName(dbdt.idColumn);
						for (int columnIndex : columnIndexArr) {
							Object indexFieldValue = context.model.getValueAtTyped(context.rowIndex, columnIndex);
							if (indexFieldValue != null) {

								if (openEditor) {
									tryOpenEditor(dbdt, indexFieldValue);
								} else {
									if (browser == null) {
										browser = app.getOrCreateDataBrowser();
									}
									boolean f = browser.doLookupFromExternal(context.dbGroupName, dbdt, indexFieldValue.toString(), context.columnName, context.fieldValue);
								}

							}
						}
					}
				}
			}

		}

	}

	private void tryOpenEditor(DataBrowserDefinedTable dbdt, Object indexFieldValue) {
		String sql = DataBrowserUIPanel.createSql(dbdt, indexFieldValue.toString(), context.columnName, context.fieldValue);
		List<List<Object>> results = app.runOneSelect(sql, 3, context.dbGroupName);
		if (app.isResultsNotEmpty(results)) {
			new Editor(app, new EditorContextFromSql(context, indexFieldValue.toString(), dbdt));
		}
	}
}
