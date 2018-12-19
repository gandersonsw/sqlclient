/* Copyright (C) 2016 Graham Anderson gandersonsw@gmail.com - All Rights Reserved */
package com.graham.sqlclient.ui.sql.iterate;

import com.graham.tools.*;
import oracle.sql.*;

import java.sql.*;
import java.util.*;

/**
 * Created by grahamanderson on 9/18/15.
 */
public class SqlDataArray {

	private boolean loadLOBs;
	private boolean lazyDataTypeFound;
	private int colCount;
	List<List<Object>> sqlDataArray;

	public void setLoadLOBs(boolean b) {
		loadLOBs = b;
	}

	public void initFromMetaData(ResultSetMetaData md) throws SQLException {
		sqlDataArray = new ArrayList<List<Object>>();
		colCount = md.getColumnCount();
		for (int colIndex = 1; colIndex <= colCount; colIndex++) {
			List<Object> a = new ArrayList<Object>();
			a.add(md.getColumnName(colIndex));
			sqlDataArray.add(a);
		}
	}

	public void addRow(ResultSet rs) throws SQLException {
		for (int i = 1; i <= colCount; i++) {
			Object d = rs.getObject(i);
			if (d instanceof CLOB) {
				if (loadLOBs) {
					try {
						d = SqlTools.getCLOBString((CLOB) d);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else {
					lazyDataTypeFound = true;
				}
			}
			if (d instanceof java.sql.Date) {
				d = rs.getTimestamp(i);
			}
			sqlDataArray.get(i-1).add(d);
		}
	}

	public void addHeaderRow(String txt) {

		for (int i = 1; i <= colCount; i++) {
			if (i == 1) {
				sqlDataArray.get(i-1).add(txt);
			} else {
				sqlDataArray.get(i-1).add(null);
			}
		}
	}

	public boolean getLazyDataTypeFound() {
		return lazyDataTypeFound;
	}
}
