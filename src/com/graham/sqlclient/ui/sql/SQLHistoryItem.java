/* Copyright (C) 2016 Graham Anderson gandersonsw@gmail.com - All Rights Reserved */
package com.graham.sqlclient.ui.sql;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.graham.appshell.data.AppDataAbstract;
import com.graham.appshell.data.AppDataClassAnn;
import com.graham.appshell.data.AppDataFieldAnn;

@AppDataClassAnn(
	classID = "SQLHistoryItem"
)
public class SQLHistoryItem extends AppDataAbstract {
	
	@AppDataFieldAnn(
		uiLabel = "Sql"
	)
	public String sql;
	
	@AppDataFieldAnn(
		uiLabel = "Created Time",
		primaryKeyFlag = true
	)
	public Date createdTime; // when the stmt was executed
	
	@AppDataFieldAnn(
		uiLabel = "ExecutionTime"
	)
	public long executionTime; // how long the execution took
	
	@AppDataFieldAnn(
		uiLabel = "Result Count"
	)
	public int resultCount;
	
	@AppDataFieldAnn(
		uiLabel = "More Results Flag"
	)
	public boolean hasMoreResults;
	
	@AppDataFieldAnn(
		uiLabel = "Update Count"
	)
	public int updateCount;
	
	@AppDataFieldAnn(
		uiLabel = "Results"
	)
	public List<List<Object>> results; // we will keep the last 10 results, discard the rest
	
	@AppDataFieldAnn(
		uiLabel = "Update Flag"
	)
	public boolean wasUpdate; // true if it was an update insert / false if it was a select

	@Override
	public String getPrimaryKey() {
		return String.valueOf(createdTime.getTime());
	}
	
	@Override
	public String toString() {
		return sql;
	}
	
	public List<List<Object>> getResults() {
		return results;
	}
	
	public void setResults(List<List<Object>> resultsParam) {
		results = new ArrayList<List<Object>>();
		int max = resultsParam.get(0).size();
		if (max > 11) {
			max = 11;
		}

		for (int i = 0; i < resultsParam.size(); i++) {
			List<Object> col = new ArrayList<Object>();
			for (int j = 0 ; j < max; j++) {
				Object val = resultsParam.get(i).get(j);
				col.add(val == null ? null : val.toString());
			}
			results.add(col);
		}
	}
	
	public boolean hasResults() {
		return !wasUpdate && getResults() != null && getResults().get(0).size() > 1;
	}

}
