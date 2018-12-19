/* Copyright (C) 2016 Graham Anderson gandersonsw@gmail.com - All Rights Reserved */
package com.graham.appshell.data;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.ref.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.graham.appshell.App;

/**
 * Manages saving AppData objects to a file, loading them, and some other infrastructure.
 */
public abstract class DataManager {
	
	private App app;
	//private ArrayList<WeakReference<ChangeListener>> dbtcl = new ArrayList<>();
	private ArrayList<ChangeListener> dbtcl = new ArrayList<>();
	AppData templateItem;
	private List<AppDataScope> accessableScopes;

	public DataManager(AppData templateItemParam, List<AppDataScope> accessableScopesParam) {
		templateItem = templateItemParam;
		accessableScopes = accessableScopesParam;
	}
	
	public void setAccessableScopes(List<AppDataScope> accessableScopesParam) {
		accessableScopes = accessableScopesParam;
		clearCachedData();
	}
	
	public abstract void clearCachedData();

	public void setApp(App appParam) {
		app = appParam;
	}
	
	public String getDataClassId() {
		return templateItem.getClassId();
	}
	
	private Object readObject(String name, AppDataScope scope) {
		File wsDir = scope.getScopeDirectory();
		File f = null;
		if (wsDir != null) {
			f = new File(wsDir, name);
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
		return retObj;
	}
	
	/**
	 * read a named object from the current workspace
	 * @param name file name to use
	 * @return
	 */
	public List<AppData> readNamedObjectList(String name) {
		List<AppData> retList = new ArrayList<AppData>();
		for (AppDataScope scope : accessableScopes) {
			Object readObj = readObject(name, scope);
			if (readObj != null) {
				List<HashMap<String,Object>> lst = (List<HashMap<String,Object>>)readObj;
				for (HashMap<String,Object> dataItem : lst) {
					retList.add(newInstanceFromMap(dataItem, scope));
				}
			}
		}
		return retList;
	}
	
	/**
	 * read a named object from the current workspace
	 * @param name file name to use
	 * @return
	 */
	public AppData readNamedObjectSingleton(String name) {
		if (accessableScopes.size() != 1) {
			throw new IllegalArgumentException("must have 1 scope for a singleton");
		}
		Object readObj = readObject(name, accessableScopes.get(0));
		if (readObj == null)
			return null;
		return newInstanceFromMap((HashMap<String,Object>)readObj, accessableScopes.get(0));
	}
	
	private AppData newInstanceFromMap(HashMap<String,Object> m, AppDataScope scope) {
		AppData data = templateItem.newInstance();
		for (String fname : m.keySet()) {
			data.setField(fname, m.get(fname));
		}
		data.setScope(scope);
		return data;
	}
	
	public HashMap<String,Object> mapFromInstance(AppData d) {
		HashMap<String,Object> mp = new HashMap<String,Object>();
		for (String fName : d.getFieldNames()) {
			Object obj = d.getField(fName);
			if (obj != null) {
				mp.put(fName, obj);
			}
		}
		return mp;
	}
	
	public void writeObject(Object obj, String name, AppDataScope scope) {
		if (!accessableScopes.contains(scope)) {
			throw new IllegalArgumentException("no permission for that scope");
		}
		
		File scopeDir = scope.getScopeDirectory();
	//	System.out.println("writeObject:" + scopeDir);
		if (!scopeDir.isDirectory()) {
			scopeDir.mkdir();
		}
		File f = new File(scopeDir, name);
		ObjectOutputStream oos = null;
		try {
			oos = new ObjectOutputStream(
					new FileOutputStream(f));
			oos.writeObject(obj);
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

	public void writeNamedObjectSingleton(AppData d, String name) {
		writeObject(mapFromInstance(d), name, accessableScopes.get(0));
	}

	public void addDataChangeListener(ChangeListener l) {
		if (l == null) {
			throw new IllegalArgumentException("null not allowed");
		}
		//dbtcl.add(new WeakReference<ChangeListener>(l));
		dbtcl.add(l);
	}
	
	public void removeDataChangeListener(ChangeListener clParam) {
		if (clParam == null) {
			System.out.println("WARNING: removeDataChangeListener parameter is null");
			return;
		}

		dbtcl.remove(clParam);
		/*
		WeakReference<ChangeListener> foundItemInList = null;
		for (WeakReference<ChangeListener> itemInList : dbtcl) {
			if (clParam.equals(itemInList.get())) {
				foundItemInList = itemInList;
				break;
			}
		}
		if (foundItemInList == null) {
			System.out.println("WARNING: removeDataChangeListener item not found:" + clParam);
		} else {
			dbtcl.remove(foundItemInList);
		}
		*/
	}
	
	public void fireChanged(ChangeEventData ced) {
		ChangeEvent e = new ChangeEvent(ced);
	//	for (WeakReference<ChangeListener> wr : dbtcl) {
	//		ChangeListener cl = wr.get();
	//		if (cl != null) {
	//			cl.stateChanged(e);
	//		}
	//	}
		for (ChangeListener cl : dbtcl) {
			cl.stateChanged(e);
		}
	}
	
}
