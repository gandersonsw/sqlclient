/* Copyright (C) 2016 Graham Anderson gandersonsw@gmail.com - All Rights Reserved */
package com.graham.appshell.data;

import java.util.*;

import com.graham.tools.MiscTools;

/**
 * A data manager for handleing a list of data. They are identified by the PrimaryKey.
 */
public class DataManagerList extends DataManager {

	private HashMap<String, AppData> datas;
	
	public DataManagerList(AppData templateItemParam, List<AppDataScope> accessableScopesParam) {
		super(templateItemParam, accessableScopesParam);
	}

	public DataManagerList(AppData templateItemParam, AppDataScope accessableScopesParam) {
		super(templateItemParam, MiscTools.listOf1(accessableScopesParam));
	}

	public void clearCachedData() {
		datas = null;
		fireChanged(new ChangeEventData(null, ChangeEventData.EditType.DRELOAD));
	}
	
	private void checkLoaded() {
		if (datas == null) {
			datas = new HashMap<String, AppData>();
			List<AppData> datas2 = readNamedObjectList(getDataClassId() + "List.obj");
			for (AppData d : datas2) {
				datas.put(d.updatePrimaryKeyLastPersisted(), d);
			}
		}
	}
	
	public Collection<AppData> getList() {
		checkLoaded();
		return datas.values();
	}
	
	/**
	 * sorted by primary keys
	 */
	public AppData[] getSortedArray() {
		checkLoaded();
		AppData tList2[] = datas.values().toArray(new AppData[datas.values().size()]);
		Arrays.sort(tList2, 0, tList2.length, new AppDataComparator());
		return tList2;
	}
	
	public class AppDataComparator implements Comparator<AppData> {
		@Override
		public int compare(AppData arg0, AppData arg1) {
			return arg0.getPrimaryKey().compareTo(arg1.getPrimaryKey());
		}
	}
	
	public AppData getByPrimaryKey(String key) {
		checkLoaded();
		return datas.get(key);
	}
	
	private void addOne(AppData d) {
		String pk = d.updatePrimaryKeyLastPersisted();
		if (pk == null) {
			throw new IllegalArgumentException("primary key must be defined before adding: " + d.getClassId());
		}
		if (d.getScope() == null) {
			throw new IllegalArgumentException("scope must be defined before adding: " + d.getClassId() + ":" + pk);
		}

		AppData d2 = getByPrimaryKey(pk);
		if (d2 != null) {
			throw new IllegalArgumentException("item with same key already exists:" + d.getClassId() + ":" + pk);
		}
		datas.put(pk, d);
	}
	
	/**
	 * we write immediately to the file here - in the future - may want to be smarter about this and only write at certain intervals or something
	 * @param d
	 */
	public void add(AppData d) {
		checkLoaded();
		addOne(d);
		Set<AppDataScope> scopesToWrite = new HashSet<>();
		scopesToWrite.add(d.getScope());
		writeNamedObjectList(scopesToWrite);
		fireChanged(new ChangeEventData(d, ChangeEventData.EditType.DNEW));
	}
	
	public void addAll(Collection<? extends AppData> items, boolean deleteAllFirst) {
		Set<AppDataScope> scopesToWrite = new HashSet<>();
		checkLoaded();
		if (deleteAllFirst) {
			for (AppData currentItem : datas.values()) {
				scopesToWrite.add(currentItem.getScope());
			}
			datas = new HashMap<String, AppData>();
		}

		for (AppData item :items) {
			addOne(item);
			scopesToWrite.add(item.getScope());
		}
		writeNamedObjectList(scopesToWrite);
		fireChanged(new ChangeEventData(null, ChangeEventData.EditType.DRELOAD));
	}
	
	public void changed(AppData d) {
		if (d.getPrimaryKey() == null) {
			throw new IllegalArgumentException("cant modify primary key to null" + d.getClassId());
		}
		if (d.getPrimaryKeyLastPersisted() == null) {
			throw new IllegalArgumentException("should have been added before and have a lastPersistedPrimaryKey" + d.getClassId() + ":" + d.getPrimaryKey());
		}
		if (!datas.containsKey(d.getPrimaryKeyLastPersisted())) {
			throw new IllegalArgumentException("not contained" + d.getClassId() + ":" + d.getPrimaryKeyLastPersisted());
		}

		final ChangeEventData ced = new ChangeEventData(d, ChangeEventData.EditType.DEDIT, d.getPrimaryKeyLastPersisted());
		if (!d.getPrimaryKeyLastPersisted().equals(d.getPrimaryKey())) {
			datas.remove(d.getPrimaryKeyLastPersisted());
			addOne(d);
		}

		Set<AppDataScope> scopesToWrite = new HashSet<>();
		scopesToWrite.add(d.getScope());
		writeNamedObjectList(scopesToWrite);
		fireChanged(ced);
	}
	
	public void delete(List<String> kys) {
		checkLoaded();

		Set<AppDataScope> scopesToWrite = new HashSet<>();

		for (String key : kys) {
			if (datas.containsKey(key)) {
				scopesToWrite.add(datas.get(key).getScope());
				datas.remove(key);
			} else {
				System.out.println("WARNING: delete object not found:" + templateItem.getClassId() + ":" + key);
			}
		}

		writeNamedObjectList(scopesToWrite);
		fireChanged(new ChangeEventData(null, ChangeEventData.EditType.DRELOAD));
	}
/*
	public void deleteAll() {
		checkLoaded();

		Set<AppDataScope> scopesToWrite = new HashSet<>();
		for (AppData d : datas.values()) {
			scopesToWrite.add(d.getScope());
		}

		datas = new HashMap<>();

		writeNamedObjectList(scopesToWrite);
		fireChanged(new ChangeEventData(null, ChangeEventData.EditType.DRELOAD));
	}
*/
	/**
	 * The items in the list may be in different scopes.  We want to make sure we keep data in the scope it was created in.
	 * @param writeTheseScopes  only those scope files will be updated. Must not be null.
	 */
	private void writeNamedObjectList(Set<AppDataScope> writeTheseScopes) {
		HashMap<AppDataScope,List<HashMap<String,Object>>> scopesWithData = new HashMap<>();
		for (AppDataScope scope : writeTheseScopes) {
			scopesWithData.put(scope, new ArrayList());
		}

		for (AppData d : datas.values()) {
			if (writeTheseScopes.contains(d.getScope())) {
				List<HashMap<String,Object>> scopeData = scopesWithData.get(d.getScope());
				scopeData.add(mapFromInstance(d));
			}
		}

		for (AppDataScope ads : scopesWithData.keySet()) {
			writeObject(scopesWithData.get(ads), getDataClassId() + "List.obj", ads);
		}
	}

}
