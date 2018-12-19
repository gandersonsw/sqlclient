/* Copyright (C) 2016 Graham Anderson gandersonsw@gmail.com - All Rights Reserved */
package com.graham.appshell;

import java.awt.Point;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.graham.appshell.data.*;
import com.graham.appshell.find.OpenFindTextDialog;
import com.graham.appshell.handlers.*;
import com.graham.appshell.tabui.*;
import com.graham.appshell.workspace.MenuSelectWorkSpace;
import com.graham.appshell.workspace.MenuSelectWorkSpaceOther;
import com.graham.appshell.workspace.WorkSpace;
import com.graham.tools.MiscTools;
import com.graham.tools.UITools;

/**
 * AppShell package provides basic data persisting and a basic UI manager.
 * package "data" is for persiting data, types extend from AppDataAbstract
 * package "tabui" is for managing the ui
 * package "workspace" is for managment of the workspce. Data is stored in the
 * file system in the workspace. A user can define multiple workspaces and switch
 * between them to keep data from different projects/contracts/environments/etc seperated.
 *
 * The concept of the scope is used to share data. Data in the aplication scope is available
 * anytime. Data in a specific group can be accessed also. Otherwise the data is in a
 * workspace scope. (Classes are AppScopeImpl, DataGroup, WorkSpace)
 */
public class App {

	// TODO get rid of this
	private static App currentApp;
	
	private AppExtensions appExt;
	private AppQuit appQuitHandler;
	private JFrame mainFrame;
	private TabUIManager tabUIManager;

	private HashMap<String,DataManager> dataManagers;
	
	private WorkSpace activeWorkspace;
	private AppScopeImpl asi;

	private List<AppQuitListener> quitListeners = new ArrayList<AppQuitListener>();

	/**
	 * @param newWorkSpace pass value if a new workspace was just created, and we want to set up some 
	 * initial setting first before we allow users to work
	 * pass null if already created
	 */
	public void init(AppExtensions appExtParam, String newWorkSpacePath) {
		AppScopeImpl.setUserNodeClass(appExtParam.getUserNodeClass());
		currentApp = this;
		appExt = appExtParam;
		// workspaces and dataGroup are only stored in the application scope
		asi = new AppScopeImpl();
		DataManagerList dm = new DataManagerList(new WorkSpace(), asi);
		
		if (newWorkSpacePath == null) {
			for (AppData d : dm.getList()) {
				if (((WorkSpace)d).activeFlag) {
					activeWorkspace = (WorkSpace)d;
				}
			}
			if (activeWorkspace == null) {
				throw new IllegalArgumentException("no active workspace");
			}
		} else {
			activeWorkspace = new WorkSpace();
			activeWorkspace.activeFlag = true;
			activeWorkspace.path = newWorkSpacePath;
			activeWorkspace.setScope(asi);

			WorkSpace existingWorkspace = (WorkSpace)dm.getByPrimaryKey(activeWorkspace.path);
			if (existingWorkspace == null) {
				dm.add(activeWorkspace);
			} else {
				existingWorkspace.activeFlag = true;
				dm.changed(existingWorkspace);
			}
		}
		
		AppRuntimeSavedData appData = readAppData();
		
		registerDataManager(dm);
		dm = new DataManagerList(new DataGroup(), asi);
		registerDataManager(dm);
		
		appExt.init(this);
		mainFrame = new JFrame();
		mainFrame.setTitle(getMainTitle());

		tabUIManager = new TabUIManager();

		JMenuBar mbar = addMenus(mainFrame);

		mainFrame.setContentPane(tabUIManager.buildUI());
		if (newWorkSpacePath != null) {
			tabUIManager.addTab(createAppUIPanel(appExt.getStartupUIPanelType()));
		}

		if (appData == null) {
			UITools.setFrameSizeAndCenter(mainFrame, 800, 600);
		} else {
			mainFrame.setSize(appData.windowWidth, appData.windowHeight);
			mainFrame.setLocation(appData.windowx, appData.windowy);

			for (AppUIPanelSavedData sd : appData.uiPanelSavedData) {
				HashMap<String,Object> data1 = null;
				try {
					data1 = (HashMap<String,Object>)sd.data;
					System.out.println("DataSize:" + sd.uiPanelKey + ":" + (MiscTools.estimateObjectSize(data1) / 1024) / 1024.0);
				} catch (Exception e) {
					e.printStackTrace();
					// TODO log warning
					data1 = new HashMap<String,Object>();
				}
				try {
					AppUIPanel uiPanel = createAppUIPanel(sd.uiPanelKey.getUIPanelType());
					if (uiPanel instanceof AppUIPanelMultiple) {
						((AppUIPanelMultiple)uiPanel).setKey(sd.uiPanelKey);
					}
					uiPanel.initStartingUp(data1);
					tabUIManager.addTab(uiPanel);
				} catch (Exception e) {
					// TODO log warning
					e.printStackTrace();
				}
			}

			try { // this could fail if there is an exception thrown from AppUIPanel#initStartingUp
				if (appData.selectedUIPane != null) {
					tabUIManager.setSelectedTab(appData.selectedUIPane);
				}
			} catch (Exception e) { }
		}

		MainTabChanged mtc = new MainTabChanged(tabUIManager, mbar);
		mtc.stateChanged(null);
		tabUIManager.addSelectedTabChangeListener(mtc);
		mainFrame.setVisible(true);
	}

	public AppUIPanel createAppUIPanel(AppUIPanelType panelType) {
		if (panelType.equals(AppUIPanelSingletonTypeImpl.AppSettings)) {
			return new AppSettingsUIPanel(App.getCurrentApp());
		}
		return appExt.createAppUIPanel(panelType);
	}

	public String getMainTitle() {
		String wsname = null;
		int last = activeWorkspace.path.lastIndexOf(File.separator);
		if (last == -1) {
			wsname = activeWorkspace.path;
		} else {
			wsname = activeWorkspace.path.substring(last + 1);
		}

		return "SQLClient : " + wsname;
	}
	
	public AppDataScope getScope(String scopeKey) {
		char c = scopeKey.charAt(0);
		if (c == 'A') { // application
			return asi;
		} else if (c == 'G') { // group
			return (AppDataScope)getDataManagerList(DataGroup.class).getByPrimaryKey(scopeKey.substring(1));
		} else if (c == 'W') { // workspace
			return (AppDataScope)getDataManagerList(WorkSpace.class).getByPrimaryKey(scopeKey.substring(1));
		} else {
			throw new IllegalArgumentException("invalide scope type");
		}
	}
	
	public AppDataScope getAppScope() {
		return asi;
	}
	/*
	public AppDataScope getWorkspaceScope() {
		this.getActiveWorkspace().getScope()
		(AppDataScope)getDataManagerList(WorkSpace.CLASS_ID).getByPrimaryKey(scopeKey.substring(1))
	}
	*/

	
	public WorkSpace getActiveWorkspace() {
		return activeWorkspace;
	}
	
	public void registerDataManager(DataManager dm) {
		dm.setApp(this);
		
		if (dataManagers == null) {
			dataManagers = new HashMap<String,DataManager>();
		}
		dataManagers.put(dm.getDataClassId(), dm);
	}
	
	public DataManager getDataManager(String classID) {
		return dataManagers.get(classID);
	}
	
	public DataManager getDataManager(Class<? extends AppData> c) {
		AppDataClassAnn a = (AppDataClassAnn)c.getAnnotation(AppDataClassAnn.class);
		return getDataManager(a.classID());
	}
	
	public DataManagerList getDataManagerList(String classID) {
		return (DataManagerList)dataManagers.get(classID);
	}
	
	public DataManagerList getDataManagerList(Class<? extends AppData> c) {
		AppDataClassAnn a = c.getAnnotation(AppDataClassAnn.class);
		return getDataManagerList(a.classID());
	}
	
	public DataManagerSingleton getDataManagerSingleton(String classID) {
		return (DataManagerSingleton)dataManagers.get(classID);
	}
	
	public DataManagerSingleton getDataManagerSingleton(Class<? extends AppData> c) {
		AppDataClassAnn a = c.getAnnotation(AppDataClassAnn.class);
		return getDataManagerSingleton(a.classID());
	}

	public JMenuBar addMenus(JFrame mainFrame) {
		
		JMenuBar mainMenuBar = new JMenuBar();

		JMenu fileMenu = new JMenu("File");
		fileMenu.add(new JMenuItem(new CloseTabAction(this)));

		JMenu workspaceMenu = new JMenu("Workspace");
		
		Collection<AppData> wsList = getDataManagerList(WorkSpace.class).getList();

		for (AppData appData : wsList) {
			WorkSpace ws = (WorkSpace)appData;
			JCheckBoxMenuItem wsMenuItem = new JCheckBoxMenuItem(new MenuSelectWorkSpace(this, ws, appExt));
			workspaceMenu.add(wsMenuItem);
			if (ws.equals(getActiveWorkspace()))
				wsMenuItem.setState(true);
		}
		
		workspaceMenu.addSeparator();
		workspaceMenu.add(new JMenuItem(new MenuSelectWorkSpaceOther(this, appExt)));
		fileMenu.add(workspaceMenu);

		fileMenu.add(new ShowAppUIPanel(this, AppUIPanelSingletonTypeImpl.AppSettings));

		appQuitHandler = new AppQuit(this);
		fileMenu.add(new JMenuItem(appQuitHandler));
		mainMenuBar.add(fileMenu);

		JMenu editMenu = new JMenu("Edit");
		//editMenu.add(new JMenuItem("Undo"));
		//editMenu.addSeparator();
		//editMenu.add(new JMenuItem("Cut"));
		//editMenu.add(new JMenuItem("Copy"));
		//editMenu.add(new JMenuItem("Paste"));
		//editMenu.addSeparator();
		editMenu.add(new JMenuItem(new OpenFindTextDialog(this)));
		/*
		editMenu.add(new JMenuItem(undoAction));
		editMenu.addSeparator();
		editMenu.add(new JMenuItem(focusL.getCutAction()));
		editMenu.add(new JMenuItem(focusL.getCopyAction()));
		editMenu.add(new JMenuItem(focusL.getPasteAction()));
		editMenu.add(new JMenuItem(focusL.getClearAction()));
		editMenu.addSeparator();
		editMenu.add(new JMenuItem(globalPrefsAction));*/
		mainMenuBar.add(editMenu);



		JMenu settingsMenu = new JMenu("Project");
		List<AppUIPanelType> uiPanelTypes = appExt.getTypesForCreateMenu();
		for (AppUIPanelType uiPanelType : uiPanelTypes) {
			if (uiPanelType instanceof AppUIPanelSingletonType) {
				settingsMenu.add(new JMenuItem(new ShowAppUIPanel(this, (AppUIPanelSingletonType)uiPanelType)));
			} else {
				settingsMenu.add(new JMenuItem(new ShowAppUIPanel(this,  (AppUIPanelMultipleType)uiPanelType, true)));
			}
		}

		mainMenuBar.add(settingsMenu);
		/*
		JMenu workflowMenu = new JMenu("WorkFlow");
		workflowMenu.add(new JMenuItem(new ShowAppUIPanel(this, LapaeUIPanelMultipleType.worker, true)));
		mainMenuBar.add(workflowMenu);
		*/
		JMenu helpMenu = new JMenu("Help");
		helpMenu.add(new JMenuItem(new ShowAppUIPanel(this, AppUIPanelSingletonTypeImpl.AppAbout)));
	//	helpMenu.add(new JMenuItem(new ShowAppUIPanel(this, AppUIPanelSingletonTypeImpl.AppHelp)));
		mainMenuBar.add(helpMenu);

		mainFrame.setJMenuBar (mainMenuBar);
		/*
		treeContextMenu = new JPopupMenu();
		treeContextMenu.add(closeAllContextAction);
		treeContextMenu.add(saveAllContextAction);
		treeContextMenu.add(uploadAllContextAction);
		treeContextMenu.addSeparator();
		treeContextMenu.add(projectTreeManager.refreshProjectContextAction);
		treeContextMenu.addSeparator();
		treeContextMenu.add(projectTreeManager.removeProjectContextAction);
		treeContextMenu.add(projectSettingsContextAction);
		
		treeLeafContextMenu = new JPopupMenu();
		treeLeafContextMenu.add(closeContextAction);
		treeLeafContextMenu.add(saveContextAction);
		treeLeafContextMenu.add(uploadContextAction);
		treeLeafContextMenu.add(viewInBrowserContextAction);
		treeLeafContextMenu.addSeparator();
		treeLeafContextMenu.add(projectTreeManager.removeProjectContextAction);
		treeLeafContextMenu.add(projectSettingsContextAction);*/

		return mainMenuBar;
	}

	public void openTextEditor(String title, String text) {
		
		JFrame te = new JFrame();
		te.setTitle(title);
		JTextArea area = new JTextArea();
		area.setText(text);
		JScrollPane sp = new JScrollPane(area);
		te.setContentPane(sp);
		
		UITools.setFrameSizeAndCenter(te, 400, 300);
		te.setVisible(true);
		
		addAppQuitListener(new FloatingFrameQuitListener(te));
	}

	/**
	 * this should only be called during startup
	 * @return
	 */
	public static WorkSpace getActiveWorkspaceStatic(AppExtensions appExtParam) {
		AppScopeImpl.setUserNodeClass(appExtParam.getUserNodeClass());
		AppScopeImpl asi = new AppScopeImpl();
		DataManagerList dm = new DataManagerList(new WorkSpace(), asi);
		
		for (AppData d : dm.getList()) {
			if (((WorkSpace)d).activeFlag) {
				return (WorkSpace)d;
			}
		}
		return null;
	}
	
	public void addAppQuitListener(AppQuitListener aql) {
		quitListeners.add(aql);
	}
	
	public void removeAppQuitListener(AppQuitListener aql) {
		quitListeners.remove(aql);
	}
	
	public boolean saveForExit() {
		
		Object obj[] = quitListeners.toArray();
		
		for (int i = 0; i < obj.length; i++) {
			AppQuitListener aql = (AppQuitListener)obj[i];
			if (!aql.canQuit()) {
				return false;
			}
		}
		for (int i = 0; i < obj.length; i++) {
			AppQuitListener aql = (AppQuitListener)obj[i];
			aql.quiting();
		}
		
		AppRuntimeSavedData appData = new AppRuntimeSavedData();
		appData.windowWidth = mainFrame.getWidth();
		appData.windowHeight = mainFrame.getHeight();
		Point pt = mainFrame.getLocation();
		appData.windowx = pt.x;
		appData.windowy = pt.y;

		AppUIPanel panel = tabUIManager.getSelectedTabUIPanel();
		if (panel != null) {
			appData.selectedUIPane = panel.getKey();
		}

		appData.uiPanelSavedData = new ArrayList<>();
		for (AppUIPanelKey k : tabUIManager.uiPanels.keySet()) {
			AppUIPanel uiPanel = tabUIManager.uiPanels.get(k);
			if (!uiPanel.throwAwayWhenQuiting()) {
				AppUIPanelSavedData sd = new AppUIPanelSavedData();
				sd.uiPanelKey = k;
				try {
					sd.data = uiPanel.getQuitingObjectSave();
					if (sd.data == null) {
						return false;
					}
					appData.uiPanelSavedData.add(sd);
				} catch (Exception e) {
					// do nothing in this case
				}
			}
		}

		writeAppData(appData);
		
		return true;
	}

	/**
	 * This may return null. All kinds of errors can happen, and if an error happens, null will be returned.
	 */
	private AppRuntimeSavedData readAppData() {
		File scopeDir = activeWorkspace.getScopeDirectory();
		File f = null;
		if (scopeDir != null) {
			f = new File(scopeDir, "appsave.obj");
		}
		Object retObj = null;
		if (f == null || !f.isFile()) {
			return null;
		} else {
			ObjectInputStream ois = null;
			try {
				ois = new ObjectInputStream(new FileInputStream(f));
				retObj = ois.readObject();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				try {
					if (ois != null)
						ois.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return (AppRuntimeSavedData)retObj;
	}
	
	private void writeAppData(AppRuntimeSavedData sd) {
		File scopeDir = activeWorkspace.getScopeDirectory();
		if (!scopeDir.isDirectory()) {
			scopeDir.mkdir();
		}
		File f = new File(scopeDir, "appsave.obj");
		ObjectOutputStream oos = null;
		try {
			oos = new ObjectOutputStream(
					new FileOutputStream(f));
			oos.writeObject(sd);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if (oos != null)
					oos.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public static App getCurrentApp() {
		return currentApp;
	}

	public JFrame getMainFrame() {
		return mainFrame;
	}

	public TabUIManager getTabManager() {
		// TODO find all references to this and remove the ones that can
		return tabUIManager;
	}

	public AppQuit getAppQuitHandler() {
		return appQuitHandler;
	}
	
}
