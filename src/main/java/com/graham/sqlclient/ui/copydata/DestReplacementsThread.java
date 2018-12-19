/* Copyright (C) 2016 Graham Anderson gandersonsw@gmail.com - All Rights Reserved */
package com.graham.sqlclient.ui.copydata;

import com.graham.tools.ConnPool;
import com.graham.tools.SqlTools;

import javax.swing.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;

public class DestReplacementsThread extends Thread {

	static class Ref {
		String t;
		String f;
		String id;
		String fromT;
		String fromF;
	}

	private ConnPool destCP;
	private JTextArea metaDataText;

	public DestReplacementsThread(ConnPool destCPParam, JTextArea metaDataTextParam) {
		destCP = destCPParam;
		metaDataText = metaDataTextParam;
	}

	public void run() {

		String text = metaDataText.getText();
		metaDataText.setText("working...");
		StringBuilder outText = new StringBuilder();
		Connection destConn = null;
		Statement destStmt = null;
		ResultSet destRs = null;

		try {
			destConn = destCP.getConn();
			destStmt = destConn.createStatement();

			for (String line : text.split("\n")) {
				Ref r = parseRef(line);
				if (r == null) {
					outText.append(line).append("\n");
				} else {
					String sql = "select " + r.f + " from " + r.t + " where " + r.f + " = '" + SqlTools.makeTextSqlSafe(r.id) + "'";
					destRs = destStmt.executeQuery(sql);
					if (destRs.next()) {
						// a row was found, no need to map to anything
						destRs.close();
					} else {
						destRs.close();
						sql = "select " + r.f + " from " + r.t + " where rownum = 1";
						destRs = destStmt.executeQuery(sql);
						if (destRs.next()) {
							outText.append(r.t + "." + r.f + ":" + r.id + ":" + destRs.getString(1) + "\n");
						} else {
							// nothing found - reference to null
							outText.append(r.t + "." + r.f + ":" + r.id + ":\n");
						}
						destRs.close();
					}
				}
			}

			metaDataText.setText(outText.toString());

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (destRs != null)
					destRs.close();
			} catch (SQLException e1) {
				//e1.printStackTrace();
			}
			try {
				if (destStmt != null)
					destStmt.close();
			} catch (SQLException e1) {
				//e1.printStackTrace();
			}
			try {
				if (destConn != null)
					destCP.releaseConn(destConn);
			} catch (SQLException e1) {
				//e1.printStackTrace();
			}
		}

	}

	/**
	 * Return null if not a ref line
	 */
	private Ref parseRef(String s) {
		int firstDot = s.indexOf(".");
		if (firstDot == -1) {
			return null;
		}
		int firstColon = s.indexOf(":",firstDot+1);
		if (firstColon == -1) {
			return null;
		}
		int firstGT = s.lastIndexOf(">");
		if (firstGT == -1) {
			return null;
		}
		int lastDot = s.indexOf(".", firstGT+1);
		if (lastDot == -1) {
			return null;
		}
		Ref r = new Ref();
		r.fromT = s.substring(0, firstDot).trim();
		r.fromF = s.substring(firstDot+1, firstColon).trim();
		r.id = s.substring(firstColon + 1, firstGT).trim();
		r.t = s.substring(firstGT + 1, lastDot).trim();
		r.f = s.substring(lastDot + 1).trim();
		return r;
	}
}
