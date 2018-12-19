/* Copyright (C) 2016 Graham Anderson gandersonsw@gmail.com - All Rights Reserved */
package com.graham.sqlclient.ui.copydata;

import com.graham.tools.ConnPool;
import com.graham.tools.SearchContext;
import com.graham.tools.SqlTools;
import com.graham.tools.StringTools;

import javax.swing.*;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class SqlCopyThread extends Thread {

	static class ImportKey {
		String ktable;
		String kfield;
	}

	static class InsertRow {
		String sql;
		long sort;
	}

	static class TreeNode {
		List<TreeNode> cnodes = new ArrayList<>();
		InsertRow r;
		List<RefToBeExpanded> refs;
	}

	static class RefToBeExpanded {
		final String t; // table reffered to
		final String f; // field reffered to
		final String id; // id reffered to
		final String fromT; // where this was refferenced from
		final String fromF; // where this was refferenced from

		public RefToBeExpanded(String tp, String fp, String idp) {
			t = tp;
			f = fp;
			id = idp;
			fromT = null;
			fromF = null;
		}
		public RefToBeExpanded(String tp, String fp, String idp, String fromtp, String fromfp) {
			t = tp;
			f = fp;
			id = idp;
			fromT = fromtp;
			fromF = fromfp;
		}
	}


	private String schemaName;
	private ConnPool sourceCP;
	private ConnPool destCP;
	private String table;
	private String tableFieldName;
	private List<String> ids;

	private Set<String> onlyInsertIntoTables;
	private Set<String> columnNamesToRandomize;
	private Map<String,String> refsToReplace;

	private JTextArea output;
	private JTextArea metaDataText;
	private ThreadManagerWithButtons testThreadManager;

	private DatabaseMetaData md;

	/**
	 * This keeps track of references to other objects. So get(refereenced to table).get(referrenced to field)
	 * Then list of ids that reference to that.
	 * referencedToRows[table reffered to][columnName refferred to][id referred to]
	 */
	private Map<String, Map<String, Map<String, List<InsertRow>>>> referencedToRows = new HashMap<>();

	/**
	 * This keeps track of the table meta data.
	 * tableImportedKeys[from table name][from column name] -> tableName and columnName referenced to
	 */
	private Map<String, Map<String, ImportKey>> tableImportedKeys = new HashMap<>();

	private TreeNode root;

	private Connection sourceConn = null;

	private long counter = 1;

	private int totalInserts;

	private StringBuilder skippedCopyRows = new StringBuilder();
	private StringBuilder doneCopyRows = new StringBuilder();
	private Set<String> skippedRefFromRows = new HashSet<>();


	public SqlCopyThread(
		String schemaNameParam,
		ConnPool sourceCPParam,
		ConnPool destCPParam,
		String tableParam,
		String tableFieldNameParam,
		List<String> idsParam,
		JTextArea outputParam,
		ThreadManagerWithButtons testThreadManagerParam,
		Set<String> onlyInsertIntoTablesParam,
		Set<String> columnNamesToRandomizeParam,
		Map<String,String> refsToReplaceParam,
		JTextArea metaDataTextParam) {
		schemaName = schemaNameParam;
		sourceCP = sourceCPParam;
		destCP = destCPParam;
		table = tableParam;
		tableFieldName = tableFieldNameParam;
		ids = idsParam;
		output = outputParam;
		testThreadManager = testThreadManagerParam;

		onlyInsertIntoTables = onlyInsertIntoTablesParam;
		columnNamesToRandomize = columnNamesToRandomizeParam;
		refsToReplace = refsToReplaceParam;

		metaDataText = metaDataTextParam;
	}

	public void run() {
		output.setText("");

		try {
			root = new TreeNode();
			sourceConn = sourceCP.getConn();
			md = sourceConn.getMetaData();
			for (String id : ids) {
				RefToBeExpanded ref = new RefToBeExpanded(table, tableFieldName, id);
				copyRow(ref, root);
				if (testThreadManager.isKillThread()) {
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (sourceConn != null)
					sourceCP.releaseConn(sourceConn);
			} catch (SQLException e1) {
				//e1.printStackTrace();
			}
		}

		root.r = new InsertRow(); // this is just here for the sorting
		updateTreeSort(root);

		InsertRow[] rows = getSortedInserts();

		StringBuilder sb = new StringBuilder();
		for (int i = rows.length - 1; i >= 0; i--) {
			sb.append(rows[i].sql);
			sb.append("\n\n");
		}
		output.setText(sb.toString());
/*
		for (String tn : referencedToRows.keySet()) {
			Map<String, Map<String, List<InsertRow>>> t = referencedToRows.get(tn);
			for (String fn : t.keySet()) {
				Map<String, List<InsertRow>> r = t.get(fn);
				for (String id : r.keySet()) {
					List<InsertRow> ir = r.get(id);
					for (InsertRow ir66 : ir) {
						System.out.println(tn + "." + fn + ":" + id);
					}
				}
			}
		}
		*/

		StringBuilder md = new StringBuilder();
		md.append("***** Refs Skipped *****\n");
		for (String ref : skippedRefFromRows) {
			md.append(ref).append("\n");
		}
		md.append("***** Rows copied *****\n");
		md.append(doneCopyRows);
		md.append("***** Skipped Rows *****\n");
		md.append(skippedCopyRows);
		metaDataText.setText(md.toString());

		testThreadManager.completed();
	}

	/**
	 * create a copy but with no IDs, or create a new one
	 */
	private Map<String,ImportKey> getImportKeys(String t) throws SQLException {

		if (!tableImportedKeys.containsKey(t)) {
			Map<String, ImportKey> keys = new HashMap<>();
			ResultSet iKeys = md.getImportedKeys(null, schemaName, t);

			while (iKeys.next()) {
				ImportKey a = new ImportKey();
				a.ktable = iKeys.getString(3);
				a.kfield = iKeys.getString(4);
				keys.put(iKeys.getString(8), a);
			}
			iKeys.close();

			tableImportedKeys.put(t, keys);
		}

		return tableImportedKeys.get(t);
	}

	private void copyRow(RefToBeExpanded ref, TreeNode parentNode) throws SQLException {
		ResultSet sourceRs = null;
		Statement sourceStmt = null;

		if (onlyInsertIntoTables.size() != 0 && !onlyInsertIntoTables.contains(ref.t)) {
			skippedCopyRows.append(ref.t + "." + ref.f + ":" + ref.id + "\n");
			skippedRefFromRows.add(ref.fromT + "." + ref.fromF + ":" + ref.id + " > " + ref.t + "." + ref.f);
			return;
		} else {
			doneCopyRows.append(ref.t + "." + ref.f + ":" + ref.id + "\n");
		}

		try {
			String sql = "select * from " + ref.t + " where " + ref.f + "='" + ref.id + "'";
			sourceStmt = sourceConn.createStatement();
			sourceRs = sourceStmt.executeQuery(sql);

			ResultSetMetaData rsmd = sourceRs.getMetaData();

			int columnsNumber = rsmd.getColumnCount();

			Map<String,ImportKey> importKeys = getImportKeys(ref.t);

			List<TreeNode> allNewNodes = new ArrayList<>();

			while (sourceRs.next()) {

				testThreadManager.updateCompletePercentage(totalInserts++);

				TreeNode newNode = new TreeNode();
				newNode.refs = new ArrayList<>();
				allNewNodes.add(newNode);

				if (testThreadManager.isKillThread()) {
					return;
				}

				StringBuilder insertSql = new StringBuilder("insert into " + ref.t + " (");
				for (int i = 1; i <= columnsNumber; i++) {
					if (i > 1) {
						insertSql.append(",");
					}
					insertSql.append(rsmd.getColumnName(i));
				}
				insertSql.append(") values (");
				for (int i = 1; i <= columnsNumber; i++) {
					if (i > 1) {
						insertSql.append(",");
					}
					if (sourceRs.getString(i) == null) {
						insertSql.append("NULL");
					} else {
						String value = sourceRs.getString(i);
						final String k33 = ref.t + "." + rsmd.getColumnName(i);
						final String k34 = k33 + ":" + sourceRs.getString(i).toUpperCase();
						if (columnNamesToRandomize.contains(k33)) {
//System.out.println("randomize:" + ref.t + "." + rsmd.getColumnName(i));
							value = "random" + (int) (Math.random() * 10000);
						} else if (refsToReplace.containsKey(k34) || refsToReplace.containsKey(k33 + ":*")) {
//System.out.println("replaced:" + k34);
							value = refsToReplace.get(k33);
							if (value == null) {
								value = refsToReplace.get(k33 + ":*");
							}
							if (value.equals("<UUID>")) {
								value = StringTools.replaceAll(UUID.randomUUID().toString(), SearchContext.createReplaceAllContext("-", ""));
//System.out.println("uuid made:" + value);
							}
						} else {
							ImportKey iKey = importKeys.get(rsmd.getColumnName(i));
							if (iKey != null) {

								InsertRow ir = getReferenceRow(iKey.ktable, iKey.kfield, sourceRs.getString(i));
								if (ir == null) {
									newNode.refs.add(new RefToBeExpanded(iKey.ktable, iKey.kfield, sourceRs.getString(i), ref.t, rsmd.getColumnName(i)));
								} else {
									TreeNode c = new TreeNode();
									c.r = ir;
									ir.sort = counter++;// this object was referenced later than wherever it was referenced before, so we want it to go before this object, instead of whatever it was supposed to go before
									counter += 10000000;
									newNode.cnodes.add(c);
								}
							}
						}

						insertSql.append("'");
						insertSql.append(SqlTools.makeTextSqlSafe(value));
						insertSql.append("'");

					}
				}
				insertSql.append(");");

				InsertRow newRow = new InsertRow();
				newRow.sql = insertSql.toString();
				newRow.sort = counter++;
				addReferenceRow(ref, newRow);

				newNode.r = newRow;
				parentNode.cnodes.add(newNode);
			}

			sourceRs.close();
			sourceRs = null;
			sourceStmt.close();
			sourceStmt = null;

			for (TreeNode n : allNewNodes) {
				for (RefToBeExpanded r : n.refs) {
					InsertRow i = getReferenceRow(r);
					if (i == null) {
						copyRow(r, n);
					} else {
						TreeNode c = new TreeNode();
						c.r = i;
						n.cnodes.add(c);
					}
				}
				n.refs = null;
			}

		} finally {
			try {
				if (sourceRs != null)
					sourceRs.close();
			} catch (SQLException e1) {
				//e1.printStackTrace();
			}
			try {
				if (sourceStmt != null)
					sourceStmt.close();
			} catch (SQLException e1) {
				//e1.printStackTrace();
			}
		}
	}

	private InsertRow getReferenceRow(RefToBeExpanded ref) {
		return getReferenceRow(ref.t, ref.f, ref.id);
	}

	private InsertRow getReferenceRow(String t, String f, String id) {
		if (referencedToRows.containsKey(t)) {
			Map<String, Map<String, List<InsertRow>>> refToFields = referencedToRows.get(t);
			if (refToFields.containsKey(f)) {
				Map<String, List<InsertRow>> refToIds = refToFields.get(f);
				if (refToIds.containsKey(id)) {
					return refToIds.get(id).get(0); // TODO get 0, is this correct?
				} else {
					return null;
				}
			}
		}
		return null;
	}

	private void addReferenceRow(RefToBeExpanded ref, InsertRow ir) {
		if (!referencedToRows.containsKey(ref.t)) {
			Map<String, Map<String, List<InsertRow>>> tmp = new HashMap<>();
			referencedToRows.put(ref.t, tmp);
		}
		Map<String, Map<String, List<InsertRow>>> refToFields = referencedToRows.get(ref.t);
		if (!refToFields.containsKey(ref.f)) {
			Map<String, List<InsertRow>> tmp = new HashMap<>();
			refToFields.put(ref.f, tmp);
		}
		Map<String, List<InsertRow>> refToIds = refToFields.get(ref.f);
		if (refToIds.containsKey(ref.id)) {
			refToIds.get(ref.id).add(ir);
		} else {
			List<InsertRow> ir66 = new ArrayList<>();
			ir66.add(ir);
			refToIds.put(ref.id, ir66);
		}

	}

	/**
	 * make sure all children have a sort value larger than their parent
	 */
	private void updateTreeSort(TreeNode n) {
		for (TreeNode c : n.cnodes) {
			if (c.r.sort < n.r.sort) {
				c.r.sort = n.r.sort + 1;
			}
			updateTreeSort(c);
		}
	}

	private InsertRow[] getSortedInserts() {
		List<InsertRow> rows = new ArrayList<>();

		for (String t : referencedToRows.keySet()) {
			Map<String, Map<String, List<InsertRow>>> fMap = referencedToRows.get(t);
			for (String f : fMap.keySet()) {
				Map<String, List<InsertRow>> idMap = fMap.get(f);
				for (List<InsertRow> rows66 : idMap.values()) {
					rows.addAll(rows66);
				}
			}
		}

		InsertRow[] rowArr =  rows.toArray(new InsertRow[0]);
		Arrays.sort(rowArr, new RowComparator());
		return rowArr;
	}

	class RowComparator implements Comparator<InsertRow> {

		@Override
		public int compare(InsertRow o1, InsertRow o2) {
			return Long.compare(o1.sort, o2.sort);
		}
	}

}
