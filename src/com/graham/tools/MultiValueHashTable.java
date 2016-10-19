/* Copyright (C) 2016 Graham Anderson gandersonsw@gmail.com - All Rights Reserved */
package com.graham.tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MultiValueHashTable<K,V> {

	private HashMap<K, List<V>> map = new HashMap<K, List<V>>();

	/*public void clear() {
		// TODO Auto-generated method stub
	}*/

	public boolean containsKey(K key) {
		return map.containsKey(key);
	}

	/*public boolean containsValue(V arg0) {
		// TODO Auto-generated method stub
		return false;
	}*/

	/*public Set entrySet() {
		// TODO Auto-generated method stub
		return null;
	}*/

	public List<V> get(K key) {
		return map.get(key);
	}

	/*public boolean isEmpty() {
		// TODO Auto-generated method stub
		return false;
	}*/

	/*public Set keySet() {
		// TODO Auto-generated method stub
		return null;
	}*/

	public Object put(K key, V value) {
		if (map.containsKey(key)) {
			List<V> l = map.get(key);
			l.add(value);
			return l;
		} else {
			List<V> l = new ArrayList<V>();
			l.add(value);
			map.put(key, l);
			return null;
		}
	}
	
	/**
	 * Add a given value for all the given keys.
	 */
	public void putAll(List<K> keys, V value) {
		for (K key : keys) {
			put(key, value);
		}
	}

	/*public void putAll(Map arg0) {
		// TODO Auto-generated method stub
		
	}*/

	public Object remove(K key, V value) {
		if (map.containsKey(key)) {
			List<V> l = map.get(key);
			l.remove(value);
			if (l.isEmpty()) {
				map.remove(key);
			}
			return l;
		}
		return null;
	}

	/*public int size() {
		// TODO Auto-generated method stub
		return 0;
	}*/

	/*public Collection values() {
		// TODO Auto-generated method stub
		return null;
	}*/

}
