/* Copyright (C) 2016 Graham Anderson gandersonsw@gmail.com - All Rights Reserved */
package com.graham.sqlclient;

import java.io.IOException;
import java.io.Reader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.graham.appshell.handlers.ShowAppUIPanel;
import com.graham.sqlclient.ui.copydata.SqlCopyData;
import com.graham.appshell.*;
import com.graham.appshell.tabui.*;
import com.graham.sqlclient.ui.help.SetupHelpUIPanel;
import com.graham.sqlclient.ui.sqlperformance.SqlPerformanceUIPanel;
import com.graham.tools.SqlTools;
import oracle.sql.CLOB;

import com.graham.sqlclient.ui.compare.CompareUIPanel;
import com.graham.sqlclient.ui.databrowser.DataBrowserDefinedRelationship;
import com.graham.sqlclient.ui.databrowser.DataBrowserDefinedTable;
import com.graham.sqlclient.ui.databrowser.DataBrowserUIPanel;
import com.graham.sqlclient.ui.set.SetUIPanel;
import com.graham.sqlclient.ui.sql.GroupDBIterator;
import com.graham.sqlclient.ui.sql.SqlUIPanel;
import com.graham.sqlclient.ui.sql.SQLHistoryItem;
import com.graham.sqlclient.ui.sqltablebrowser.DBMetaSchema;
import com.graham.sqlclient.ui.sqltablebrowser.DBMetaTable;
import com.graham.sqlclient.ui.sqltablebrowser.TableBrowserUIPanel;
import com.graham.sqlclient.worker.WorkerUIPanel;
import com.graham.appshell.data.AppDataScope;
import com.graham.appshell.data.DataGroup;
import com.graham.appshell.data.DataManagerList;
import com.graham.appshell.data.DataManagerSingleton;
import com.graham.appshell.workunit.WorkUnitsUIPanel;
import com.graham.tools.ConnPool;

public class SqlClientApp implements AppExtensions {
	
	private ConnPool connPool;
	
	public App appsh;
	
	public List<AppDataScope> acessableScopes;
	
	public AppUIPanelType getStartupUIPanelType() {
		return LapaeUIPanelSingletonType.setupHelp;
	}

	public AppUIPanel createAppUIPanel(AppUIPanelType panelType) {
		if (panelType.equals(LapaeUIPanelSingletonType.setupHelp)) {
			return new SetupHelpUIPanel(this);
		} else if (panelType.equals(LapaeUIPanelSingletonType.simpleSettings)) {
			return new SimpleSettingsUIPanel(appsh);
		} else if (panelType.equals(LapaeUIPanelSingletonType.workUnits)) {
			return new WorkUnitsUIPanel(appsh);
		} else if (panelType.equals(LapaeUIPanelSingletonType.tableBrowser)) {
			return new TableBrowserUIPanel(this);
		} else if (panelType.equals(LapaeUIPanelMultipleType.sql)) {
			return new SqlUIPanel(this);
		} else if (panelType.equals(LapaeUIPanelMultipleType.dataBrowser)) {
			return new DataBrowserUIPanel(this);
		} else if (panelType.equals(LapaeUIPanelMultipleType.worker)) {
			return new WorkerUIPanel(this);
		} else if (panelType.equals(LapaeUIPanelMultipleType.sets)) {
			return new SetUIPanel();
		} else if (panelType.equals(LapaeUIPanelMultipleType.compare)) {
			return new CompareUIPanel();
		} else if (panelType.equals(LapaeUIPanelMultipleType.sqlPerformance)) {
			return new SqlPerformanceUIPanel(this);
		} else if (panelType.equals(LapaeUIPanelMultipleType.sqlCopy)) {
			return new SqlCopyData(this);
		} else if (panelType.equals(AppUIPanelSingletonTypeImpl.AppAbout)) {
			return new AboutLapaeUIPanel();
		}
		return null;
	}

	public WorkSpaceSettings getWSS() {
		return (WorkSpaceSettings)appsh.getDataManagerSingleton(WorkSpaceSettings.class).get();
	}
	
	public ConnPool getConnPool() {
		if (connPool == null) {
			WorkSpaceSettings s = getWSS();
			if (s.dbList.size() > 0) {
				WSDB db = s.dbList.get(0);
				connPool = new ConnPool(db.databaseUrl, db.databaseUserName, db.databasePassword);
			}
		}
		return connPool;
	}
	
	public ConnPool getConnPool(String dbGroupName) {
		if (dbGroupName == null) {
			return getConnPool();
		}
		
		GroupDBIterator iter = new GroupDBIterator(this, dbGroupName);
		if (iter.hasMore()) {
			return iter.next();
		} else {
			System.out.println("ERROR101:no db connections found");
			return getConnPool();
		}
	}

	public boolean isResultsNotEmpty(List<List<Object>> results) {
		return results.get(0).size() > 1;
	}

	/**
	 * returns list of list.  First item in sub-lists are the column names. 
	 * Main list if list of columns. Sub lists are list of rows in the colum.
	 */
	public List<List<Object>> runOneSelect(String sql, int resultRowsLimit, String dbGroupName) {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		List<List<Object>> ret = new ArrayList<List<Object>>();
		try {
			conn = getConnPool(dbGroupName).getConn();
			stmt = conn.createStatement();
			stmt.setFetchSize(SqlTools.computeFetchSize(resultRowsLimit));
//System.out.println("sql=" + sql);
			rs = stmt.executeQuery(sql);
			ResultSetMetaData md = rs.getMetaData();
			int colCount = md.getColumnCount() ;
			
			int rowIndex = 0;
			for (int colIndex = 1; colIndex <= colCount; colIndex++) {
				List<Object> a = new ArrayList<Object>();
				a.add(md.getColumnName(colIndex));
				ret.add(a);
			}
			while (rowIndex < resultRowsLimit && rs.next()) {
				for (int colIndex = 1; colIndex <= colCount; colIndex++) {
					ret.get(colIndex-1).add(rs.getObject(colIndex));
				}
				rowIndex++;
			}
		} catch (ClassNotFoundException e) {
			 e.printStackTrace();
		} catch (SQLException e) {
			 e.printStackTrace();
		} finally {
			try {
				if (rs != null)
					rs.close();
			} catch (SQLException e1) {
				//e1.printStackTrace();
			}
			try {
				if (stmt != null)
					stmt.close();
			} catch (SQLException e1) {
				//e1.printStackTrace();
			}
			try {
				if (conn != null)
					getConnPool().releaseConn(conn);
			} catch (SQLException e1) {
				//e1.printStackTrace();
			}
		}
		return ret;
	}
	
	/**
	 * this may fail silently
	 * @param pool
	 * @param sql
	 * @return returns a StringBuilder if the item was a clob
	 */
	public Object runOneSelect(String sql, String dbGroupName) throws SQLException {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		Object ret = null;
		try {
			conn = getConnPool(dbGroupName).getConn();
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);
//System.out.println("sql=" + sql);
			if (rs.next()) {
				ret = rs.getObject(1);
				
				if (ret instanceof CLOB) {
					StringBuilder sb = new StringBuilder();
					Reader rdr = ((CLOB)ret).getCharacterStream();
					char buf[] = new char[1024];
					int i;
					while ((i = rdr.read(buf)) > 0) {
						sb.append(buf,0,i);
					}
					rdr.close();
					ret = sb;
				}
			}
		} catch (ClassNotFoundException e) {
			 e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if (rs != null)
					rs.close();
			} catch (SQLException e1) {
				//e1.printStackTrace();
			}
			try {
				if (stmt != null)
					stmt.close();
			} catch (SQLException e1) {
				//e1.printStackTrace();
			}
			try {
				if (conn != null)
					getConnPool().releaseConn(conn);
			} catch (SQLException e1) {
				//e1.printStackTrace();
			}
		}
		return ret;
	}
	
	public Timestamp runOneSelectTimeStamp(String sql, String dbGroupName) throws SQLException {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		Timestamp ret = null;
		try {
			conn = getConnPool(dbGroupName).getConn();
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);
//System.out.println("sql=" + sql);
			if (rs.next()) {
				ret = rs.getTimestamp(1);
			}
		} catch (ClassNotFoundException e) {
			 e.printStackTrace();
		} finally {
			try {
				if (rs != null)
					rs.close();
			} catch (SQLException e1) {
				//e1.printStackTrace();
			}
			try {
				if (stmt != null)
					stmt.close();
			} catch (SQLException e1) {
				//e1.printStackTrace();
			}
			try {
				if (conn != null)
					getConnPool().releaseConn(conn);
			} catch (SQLException e1) {
				//e1.printStackTrace();
			}
		}
		return ret;
	}
	
	public int runOneUpdateAndCommit(String sql) throws SQLException {
		Connection conn = null;
		Statement stmt = null;
		int ret = 0;
		try {
			conn = getConnPool().getConn();
			stmt = conn.createStatement();
			ret = stmt.executeUpdate(sql);
			conn.commit();
//System.out.println("update sql=" + sql);
		} catch (ClassNotFoundException e) {
			 e.printStackTrace();
		} finally {
			try {
				if (stmt != null)
					stmt.close();
			} catch (SQLException e1) {
				//e1.printStackTrace();
			}
			try {
				if (conn != null)
					getConnPool().releaseConn(conn);
			} catch (SQLException e1) {
				//e1.printStackTrace();
			}
		}
		return ret;
	}
	
	@Override
	public void init(App appParam) {
		appsh = appParam;
		
		// workspace scope
		// groups
		DataManagerSingleton wssDM = new DataManagerSingleton(new WorkSpaceSettings(), appsh.getActiveWorkspace());
		appsh.registerDataManager(wssDM);
		setUpAccessebleScopes((WorkSpaceSettings)wssDM.get());
		wssDM.addDataChangeListener(new workSpaceChangeListener());
		List<AppDataScope> scopes = acessableScopes;
		
		appsh.registerDataManager(new DataManagerList(new DataBrowserDefinedTable(this), scopes));
		appsh.registerDataManager(new DataManagerList(new DataBrowserDefinedRelationship(this), scopes));

		// TODO use the workspace scope for this
		appsh.registerDataManager(new DataManagerList(new DBMetaSchema(), appsh.getActiveWorkspace()));
		appsh.registerDataManager(new DataManagerList(new DBMetaTable(), appsh.getActiveWorkspace()));
		
		appsh.registerDataManager(new DataManagerList(new SQLHistoryItem(), appsh.getActiveWorkspace()));
	}
	
	private void setUpAccessebleScopes(WorkSpaceSettings wss) {
		acessableScopes = new ArrayList<AppDataScope>();
		acessableScopes.add(appsh.getActiveWorkspace());
		if (wss != null) {
			for (String str : wss.groups) {
				acessableScopes.add((AppDataScope)appsh.getDataManagerList(DataGroup.class).getByPrimaryKey(str));
			}
		}
	}

	@Override
	public AppExtensions newInstance() {
		return new SqlClientApp();
	}
	
	public class workSpaceChangeListener implements ChangeListener {

		@Override
		public void stateChanged(ChangeEvent arg0) {
			//ChangeEventData ced = (ChangeEventData)arg0.getSource();
			setUpAccessebleScopes(getWSS());
			
			appsh.getDataManager(DataBrowserDefinedTable.class).setAccessableScopes(acessableScopes);
			appsh.getDataManager(DataBrowserDefinedRelationship.class).setAccessableScopes(acessableScopes);
			
			if (connPool != null) {
				WorkSpaceSettings s = getWSS();
				if (s.dbList.size() > 0) {
					WSDB db = s.dbList.get(0);
					connPool.checkParamsCurrent(db.databaseUrl, db.databaseUserName, db.databasePassword);
				}
			}
		}
	}
	
	public DataBrowserUIPanel getOrCreateDataBrowser() {

		ShowAppUIPanel sp = new ShowAppUIPanel(this.appsh, LapaeUIPanelMultipleType.dataBrowser, false);
		sp.actionPerformed(null);
		return (DataBrowserUIPanel)sp.getPanelThatWasShown();
		/*
		List<AppUIPanel> uiPanels = appsh.getTabManager().getUIPanels(LapaeUIPanelMultipleType.dataBrowser);
		if (uiPanels.size() == 0) {
			return (DataBrowserUIPanel)createAppUIPanel(LapaeUIPanelMultipleType.dataBrowser);
		}
		return (DataBrowserUIPanel)uiPanels.get(0);
		*/
	}

	public List<AppUIPanelType> getTypesForCreateMenu() {
		List<AppUIPanelType> types = new ArrayList<>();
		types.add(LapaeUIPanelMultipleType.sql);
		types.add(LapaeUIPanelMultipleType.dataBrowser);
		types.add(LapaeUIPanelMultipleType.sqlPerformance);
		types.add(LapaeUIPanelMultipleType.sqlCopy);
		types.add(LapaeUIPanelMultipleType.sets);
		types.add(LapaeUIPanelMultipleType.worker);
		types.add(LapaeUIPanelSingletonType.tableBrowser);
		types.add(LapaeUIPanelSingletonType.simpleSettings);
		return types;
	}

	public Class getUserNodeClass() {
		return SqlClientApp.class;
	}

}
