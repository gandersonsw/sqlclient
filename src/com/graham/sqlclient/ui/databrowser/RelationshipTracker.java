/* Copyright (C) 2016 Graham Anderson gandersonsw@gmail.com - All Rights Reserved */
package com.graham.sqlclient.ui.databrowser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RelationshipTracker {

	private Map<String, List<DataBrowserDefinedRelationship>> items = new HashMap<String, List<DataBrowserDefinedRelationship>>();

	public void addRelationship(DataBrowserDefinedRelationship rel) {

		// if there is a comma, it is a mutli-colum mapping, so don't include it
		if (rel.fromColumn.indexOf(',') == -1) {
			List<DataBrowserDefinedRelationship> l;
			final String columnName = rel.fromColumn.toUpperCase();
			if (items.containsKey(columnName)) {
				l = items.get(columnName);
			} else {
				l = new ArrayList<DataBrowserDefinedRelationship>();
				items.put(columnName, l);
			}
			
			l.add(rel);
		}
	}

	public void removeRelationship(DataBrowserDefinedRelationship rel) {
		items.remove(rel.fromColumn.toUpperCase());
	}
	
	public boolean shouldHiliteValue(final String columnName) {
		return items.containsKey(columnName.toUpperCase());
	}
	
	public List<DataBrowserDefinedRelationship> getRelationships(final String columnName) {
		if (items.containsKey(columnName)) {
			return items.get(columnName);
		} else {
			return Collections.<DataBrowserDefinedRelationship>emptyList();
		}
	}
}
