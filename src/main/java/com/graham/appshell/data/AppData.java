/* Copyright (C) 2016 Graham Anderson gandersonsw@gmail.com - All Rights Reserved */
package com.graham.appshell.data;

import java.util.List;

import javax.swing.event.ChangeListener;

/**
 * An implementation of this is both an instance of the data, and works to define the structure of the data.
 */
public interface AppData {
	
	void setField(String fieldName, Object value);
	
	Object getField(String fieldName);
	
	/**
	 * Get the names for all the data elements contained in this object.  
	 * This list will be used by the UI if needed, so if the UI will be used, 
	 * the names should be formated user friendly, example : "Favorite Color"
	 */
	List<String> getFieldNames();

	/**
     * the field value (not the name) for the primary key.  These will be enforced to be unique.
	 * Return null if there is no key.
	 */
	String getPrimaryKey();
	
	/**
	 * Get the list of field that are used in the process to generate the primary key.
	 */
	List<String> getPrimaryKeyFieldNames();
	
	/**
	 * A short unique string that identifies this type.
	 */
	String getClassId();

	/**
	 * How is the first instance created?
	 * The object that knows about the types of "AppData"s that are support calls for each:
	 * 
	 * App.registerDataManager(new DataManagerList(new AppDataType(), App.getActiveWorkspace()));
	 * or
	 * App.registerDataManager(new DataManagerSingleton(new AppDataType(), App.getActiveWorkspace()));
	 */
	AppData newInstance();
	
	AppDataScope getScope();
	
	void setScope(AppDataScope scope);
	
	/**
	 * @param name The field name.
	 * @return The short name that should be displayed in a UI to identify this field.
	 */
	String getUILabel(String fieldName);
	
	/**
	 * if you want a drop-down menu for this item to be used in the UI, return a list of items, that will appear in that menu.
	 */
	List<String> getUIAllowedValues(String fieldName);

	String getUIToolTipText(String fieldName);

	boolean isUIEditable(String fieldName);
	
	/**
	 * return true if the value entered by the user in a text field should be trimmed of lead/ending whitespace
	 */
	boolean getUITextTrimFlag(String fieldName);
	
	/**
	 * Called after user clicks "Save" in UI.
	 * @return null if everything is verified ok, otherwise return message String
	 */
	String verifyAfterUIOK();
	
	/**
	 * 
	 */
	void addChangeListener(ChangeListener l, List<String> fieldNames);

	/**
	 * The primary key that the object it saved in. If an objects primary key is changed, this is used to find the object.
	 * @return
	 */
	String getPrimaryKeyLastPersisted();

	/**
	 * Set the PrimaryKeyLastPersisted to the current primary key, and return the updated value.
	 * @return
	 */
	String updatePrimaryKeyLastPersisted();
	
}
