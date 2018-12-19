/* Copyright (C) 2016 Graham Anderson gandersonsw@gmail.com - All Rights Reserved */
package com.graham.appshell.data;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.graham.tools.MultiValueHashTable;

public abstract class AppDataAbstract implements AppData {
	
	/**
	 * Data about the AppData structure.
	 */
	class AppDataMeta {
		List<String> fieldNames;
		List<String> primaryKeyFieldNames;
		Field primaryKey;
	}
	
	/**
	 * Lookup for fields for a certain AppData class, key is the classID
	 */
	private static Map<String, AppDataMeta> allFields = new HashMap<String, AppDataMeta>();
	
	private AppDataScope scope;
	private AppDataMeta metaData;
	private MultiValueHashTable<String, ChangeListener> changeListeners;
	private String primaryKeyLastPersisted; // the primary key value when this object was last persisted. May be different than the current primary key.
	
	public AppDataAbstract() {
		if (!allFields.containsKey(this.getClassId())) {
			allFields.put(this.getClassId(), createAppDataMeta());
		}
		metaData = allFields.get(this.getClassId());
	}
	
	public List<String> getFieldNames() {
		return metaData.fieldNames;
	}
	
	/**
	 * The default implementation for get primary key value. Can be over-ridden if other behavior is needed.
	 */
	public String getPrimaryKey() {
		try {
			Object obj = metaData.primaryKey.get(this);
			if (obj == null) {
				return null;
			}
			return obj.toString();
		} catch (IllegalArgumentException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}
	
	public List<String> getPrimaryKeyFieldNames() {
		return metaData.primaryKeyFieldNames;
	}
	
	public String getClassId() {
		AppDataClassAnn a = this.getClass().getAnnotation(AppDataClassAnn.class);
		return a.classID();
	}
	
	public AppData newInstance() {
		try {
			Constructor<? extends AppData> c = this.getClass().getConstructor();
			return c.newInstance();
		} catch (Exception e) { // SecurityException, NoSuchMethodException, IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException
			throw new RuntimeException(e);
		}
	}
	
	public Object getField(String fieldName) {
		Field f = null;
		try {
			f = getFieldForReflection(fieldName);
		} catch (Exception e) {
			throw new RuntimeException("field not found:" + fieldName);
		}
		try {
			return f.get(this);
		} catch (IllegalArgumentException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	public void setField(String fieldName, Object value) {
		Field f = null;
		try {
			f = getFieldForReflection(fieldName);
		} catch (Exception e) {
			throw new RuntimeException("field not found:" + fieldName);
		}
		try {
			f.set(this, value);
		} catch (IllegalArgumentException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
		
		if (changeListeners != null) {
			for (ChangeListener l : changeListeners.get(fieldName)) {
				ChangeEvent ce = new ChangeEvent(this);
				l.stateChanged(ce);
			}
		}
	}

	/*
	private Field findFieldByOldUILabel(final String uiLabel) {
		System.out.println("should not be here 4536 - delete this method soon");
		for (String fieldName : getFieldNames()) {
			final AppDataFieldAnn a = getFieldAnnoation(fieldName);
			if (a.uiLabel().equals(uiLabel)) {
				return getFieldForReflection(fieldName);
			}
		}
		return null;
	}
	*/
	
	private AppDataFieldAnn getFieldAnnoation(final String fieldName) {
		try {
			final Field f = this.getClass().getField(fieldName);
			final Annotation a = f.getAnnotation(AppDataFieldAnn.class);
			if (a == null) {
				throw new RuntimeException("not a data field");
			}
			return (AppDataFieldAnn)a;
		} catch (SecurityException e) {
			throw new RuntimeException(e);
		} catch (NoSuchFieldException e) {
			throw new RuntimeException(e);
		}
	}
	
	private Field getFieldForReflection(final String fieldName) {
		try {
			final Field f = this.getClass().getField(fieldName);
			final Annotation a = f.getAnnotation(AppDataFieldAnn.class);
			if (a == null) {
				throw new RuntimeException("not a data field");
			}
			return f;
		} catch (SecurityException e) {
			throw new RuntimeException(e);
		} catch (NoSuchFieldException e) {
			throw new RuntimeException(e);
		}
	}
	
	public String getUILabel(String fieldName) {
		try {
			return getFieldAnnoation(fieldName).uiLabel();
		} catch (Exception e) {
			System.out.println("getUILabel label not found:" + fieldName);
			return fieldName; // TODO remove this after all types converted
		}
	}

	private AppDataMeta createAppDataMeta() {
		AppDataMeta m = new AppDataMeta();
		m.fieldNames = new ArrayList<String>();
		m.primaryKeyFieldNames = new ArrayList<String>();
		Field fArr[] = this.getClass().getFields();
		for (Field f : fArr) {
			if (f.isAnnotationPresent(AppDataFieldAnn.class)) {
				m.fieldNames.add(f.getName());
				AppDataFieldAnn a = (AppDataFieldAnn)f.getAnnotation(AppDataFieldAnn.class);
				if (a.primaryKeyFlag()) {
					m.primaryKey = f;
					m.primaryKeyFieldNames.add(f.getName());
				}
			}
		}
		return m;
	}
	
	public AppDataScope getScope() {
		return scope;
	}
	
	public void setScope(AppDataScope scopeParam) {
		if (scope != null) {
			throw new IllegalArgumentException("scope cannot be changed one it is set");
		}
		scope = scopeParam;
	}

	void clearScopeBecauseOfFailedPersist() {
		scope = null;
	}
	
	@Override
	public List<String> getUIAllowedValues(String fieldName) {
		return null;
	}

	@Override
	public boolean isUIEditable(String fieldName) {
		return true;
	}

	@Override
	public String getUIToolTipText(String fieldName) {
		return null;
	}

	@Override
	public boolean getUITextTrimFlag(String fieldName) {
		return true;
	}

	@Override
	public String verifyAfterUIOK() {
		return null;
	}

	public String toString() {
		return getPrimaryKey();
	}

	public void addChangeListener(ChangeListener l, List<String> fieldNames) {
		if (changeListeners == null) {
			changeListeners = new MultiValueHashTable<String, ChangeListener>();
		}
		changeListeners.putAll(fieldNames, l);
	}

	public String getPrimaryKeyLastPersisted() {
		return primaryKeyLastPersisted;
	}

	public String updatePrimaryKeyLastPersisted() {
		primaryKeyLastPersisted = getPrimaryKey();
		return primaryKeyLastPersisted;
	}
}
