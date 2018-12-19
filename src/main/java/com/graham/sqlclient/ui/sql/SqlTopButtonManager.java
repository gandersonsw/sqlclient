/* Copyright (C) 2016 Graham Anderson gandersonsw@gmail.com - All Rights Reserved */
package com.graham.sqlclient.ui.sql;

import com.graham.appshell.data.DataGroup;
import com.graham.sqlclient.SqlClientApp;
import com.graham.sqlclient.WSDB;
import com.graham.appshell.workunit.CancelWorkUnit;
import com.graham.sqlclient.WorkSpaceSettings;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.*;

/**
 *
 * Manages the top panel for the SQLUI. The buttons ("commit", Rollback", db delector, row select count, stuff like that
 *
 * Created by grahamanderson on 6/17/16.
 */
public class SqlTopButtonManager {

	private JPanel buttonPanel;
	private JComboBox dbGroupField;
	private JTextField rowsPerSelectField;
	private WorkUnitSql currentWorkUnit;
	private Action runAction;
	private Action runSelectedAction;
	private Action cancelAction;
	private Action commitAction;
	private Action rollbackAction;
	private JButton getMoreResultsButton;
	private JCheckBox loadLOBs;
	private DatabaseListChangedListener dbListListener;

	public SqlTopButtonManager(SqlClientApp app, SqlUIPanel sqlUi) {
		buttonPanel = new JPanel(new FlowLayout());
		buttonPanel.add(new JLabel("DB:"));

		dbGroupField = new JComboBox();
		dbGroupField.setEditable(true);
		for (WSDB db : app.getWSS().dbList) {
			dbGroupField.addItem(db.group);
		}
		buttonPanel.add(dbGroupField);

		dbListListener = new DatabaseListChangedListener(app); // make a reference to this since the getDataManagerList only keeps a weak reference
		app.appsh.getDataManagerSingleton(WorkSpaceSettings.class).addDataChangeListener(dbListListener);

		runAction = new RunQuery(app, sqlUi, false);
		JButton runButton = new NoFocusJButton(runAction);
		runSelectedAction = new RunQuery(app, sqlUi, true);
		JButton runSelectedButton = new NoFocusJButton(runSelectedAction);
		cancelAction = new CancelWorkUnit();
		JButton cancelButton = new NoFocusJButton(cancelAction);
		cancelAction.setEnabled(false);
		buttonPanel.add(runButton);
		buttonPanel.add(runSelectedButton);
		buttonPanel.add(cancelButton);
		commitAction = new commitActionClass();
		JButton commitButton = new NoFocusJButton(commitAction);
		commitAction.setEnabled(false);
		buttonPanel.add(commitButton);
		rollbackAction = new rollbackActionClass();
		JButton rollbackButton = new NoFocusJButton(rollbackAction);
		rollbackAction.setEnabled(false);
		buttonPanel.add(rollbackButton);

		getMoreResultsButton = new NoFocusJButton(new getMoreResultsActionClass());
		getMoreResultsButton.setEnabled(false);
		buttonPanel.add(getMoreResultsButton);

		loadLOBs = new JCheckBox("Load LOBs");
		buttonPanel.add(loadLOBs);

		rowsPerSelectField = new JTextField("100");
		rowsPerSelectField.setColumns(5);

		buttonPanel.add(new JLabel("Select Count:"));
		buttonPanel.add(rowsPerSelectField);
	}

	public JPanel getPanel() {
		return buttonPanel;
	}

	public int getRowsPerSelect() {
		try {
			int i = Integer.parseInt(rowsPerSelectField.getText());
			if (i < 1) {
				return 1;
			}
			return i;
		} catch (Exception e) {
			return 100;
		}
	}

	public String getDBGroupName() {
		if (dbGroupField.getSelectedItem() != null) {
			return dbGroupField.getSelectedItem().toString();
		}
		return null;
	}

	public void setDBGroupName(String dbGroupName) {
		boolean hasMatch = false;
		for (int i = 0; i < dbGroupField.getItemCount(); i++) {
			if (dbGroupField.getItemAt(i).toString().equals(dbGroupName)) {
				hasMatch = true;
				dbGroupField.setSelectedIndex(i);
			}
		}
		if (!hasMatch) {
			dbGroupField.insertItemAt(dbGroupName, 0);
			dbGroupField.setSelectedIndex(0);
		}
	}

	public java.util.List<Action> getActionsForMenu() {
		java.util.List<Action> menuActions = new ArrayList<>();
		menuActions.add(runAction);
		menuActions.add(new GlobalRunCurrentSql(this.runSelectedAction));
		menuActions.add(cancelAction);
		menuActions.add(commitAction);
		menuActions.add(rollbackAction);
		return menuActions;
	}

	public void executeSqlDone(boolean commitPending, boolean moreRowsWaiting, boolean connClosed) {
		commitAction.setEnabled(commitPending);
		rollbackAction.setEnabled(commitPending);
		getMoreResultsButton.setEnabled(moreRowsWaiting);
		if (connClosed) {
			//System.out.println("at 222222");
			currentWorkUnit = null;
		}
	}

	public void setCurrentWorkUnit(WorkUnitSql currentWorkUnitParam) {
		currentWorkUnit = currentWorkUnitParam;
	}

	public void close() {
		dbListListener.remove();
	}

	public class RunQuery extends AbstractAction {

		private static final long serialVersionUID = 1L;
		SqlClientApp app;
		boolean selectedOnlyFlag;
		SqlUIPanel sqlUi;

		public RunQuery(SqlClientApp appParam, SqlUIPanel sqlUiParam, boolean selectedOnlyFlagParam) {
			super(selectedOnlyFlagParam ? "Run Selected" : "Run All");
			app = appParam;
			sqlUi = sqlUiParam;
			selectedOnlyFlag = selectedOnlyFlagParam;
		}

		private boolean dbGroupChanged() {
			if (currentWorkUnit != null) {
				final String oldGroupName = ((GroupDBIterator)currentWorkUnit.getDbIter()).getGroupName();
				return !getDBGroupName().equals(oldGroupName);
			}
			return true;
		}

		public void actionPerformed(ActionEvent e) {
			if (sqlUi.isSqlIdle()) {

				String currentRunningSQL = sqlUi.prepForSqlExecute(selectedOnlyFlag);

				if (currentWorkUnit == null || dbGroupChanged()) {
					SqlRunCallback callback = new SqlRunCallback(app, sqlUi, SqlTopButtonManager.this);
					WorkUnitSql newWU = new WorkUnitSql(new GroupDBIterator(app, getDBGroupName()), currentRunningSQL, callback);
					newWU.setLoadLOBs(loadLOBs.isSelected());
					newWU.setRowsPerSelect(getRowsPerSelect());
					newWU.addDisabledWhileRunning(runAction);
					newWU.addDisabledWhileRunning(runSelectedAction);
					newWU.addEnabledWhileRUnning(cancelAction);

					synchronized (sqlUi) {
						((CancelWorkUnit)cancelAction).setWorkUnit(newWU);
						if (currentWorkUnit == null) {
							currentWorkUnit = newWU;
							currentWorkUnit.startWU();
						} else {
							currentWorkUnit.cancelWorkUnitAndQueueNew(newWU);
						}
					}

				} else {
					getMoreResultsButton.setEnabled(false);

					currentWorkUnit.setLoadLOBs(loadLOBs.isSelected());
					currentWorkUnit.setRowsPerSelect(getRowsPerSelect());
					currentWorkUnit.addWork(currentRunningSQL);
				}
			} else {
				System.out.println("query already running...");
			}
		}
	}

	public class commitActionClass extends AbstractAction {
		private static final long serialVersionUID = 1L;
		public commitActionClass() {
			super("Commit");
		}
		public void actionPerformed(ActionEvent e) {
			currentWorkUnit.commit();
		}
	}

	public class rollbackActionClass extends AbstractAction {
		private static final long serialVersionUID = 1L;
		public rollbackActionClass() {
			super("Rollback");
		}
		public void actionPerformed(ActionEvent e) {
			currentWorkUnit.rollback();
		}
	}

	public class getMoreResultsActionClass extends AbstractAction {
		private static final long serialVersionUID = 1L;
		public getMoreResultsActionClass() {
			super("More Rows");
		}
		public void actionPerformed(ActionEvent e) {
			currentWorkUnit.getMoreResults();
		}
	}

	public class DatabaseListChangedListener implements ChangeListener {

		SqlClientApp app;

		public DatabaseListChangedListener(SqlClientApp appParam) {
			app = appParam;
		}

		public void stateChanged(ChangeEvent e) {
			String selectedItem = dbGroupField.getSelectedItem().toString();
			dbGroupField.removeAllItems();
			for (WSDB db : app.getWSS().dbList) {
				dbGroupField.addItem(db.group);
				if (db.group.equals(selectedItem)) {
					dbGroupField.setSelectedItem(db.group);
				}
			}
		}

		public void remove() {
			app.appsh.getDataManagerSingleton(WorkSpaceSettings.class).removeDataChangeListener(this);
		}

	}

}
