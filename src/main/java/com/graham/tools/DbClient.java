/* Copyright (C) 2016 Graham Anderson gandersonsw@gmail.com - All Rights Reserved */
package com.graham.tools;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class DbClient {

  public static DbClient getClient(String url) {
    if (url.startsWith("jdbc:oracle:thin")) {
      return new DbClientOracleThin();
    } else if (url.startsWith("jdbc:jtds:sqlserver")) {
      return new DbClientSqlServerJtds();
    } else {
      return new DbClient();
    }
  }

  public DbParsedUrl parseUrl(String urlText) {
    DbParsedUrl url = new DbParsedUrl();
		int atLoc = urlText.indexOf(":");
		if (atLoc != -1) {
			atLoc = urlText.indexOf(":", atLoc+1);
		}
		if (atLoc != -1) {
			atLoc = urlText.indexOf(":", atLoc+1);
		}
		if (atLoc != -1) {
			url.driverClass = urlText.substring(0, atLoc);
			urlText = urlText.substring(atLoc + 1);
		}

		int colonLoc = urlText.indexOf(":");
		if (colonLoc != -1) {
			url.hostName = urlText.substring(0, colonLoc);
			urlText = urlText.substring(colonLoc + 1);
		}

		colonLoc = urlText.indexOf(":");
		if (colonLoc != -1) {
			url.port = urlText.substring(0, colonLoc);
			url.sid = urlText.substring(colonLoc + 1);
		}

		int slashLoc = urlText.indexOf("/");
		if (slashLoc != -1) {
			url.port = urlText.substring(0, slashLoc);
			url.serviceName = urlText.substring(slashLoc + 1);
		}

		int semiLoc = urlText.indexOf(";");
		if (semiLoc != -1) {
			url.port = urlText.substring(0, semiLoc);
			url.extraParams = urlText.substring(semiLoc + 1);
		}

		return url;
  }


  public String createUrl(DbParsedUrl u) {
    StringBuilder urlStr = new StringBuilder();

    urlStr.append(u.driverClass);
    urlStr.append(":");
    urlStr.append(u.hostName);
    if (u.port != null && u.port.length() > 0) {
      urlStr.append(":");
      urlStr.append(u.port);
    }

    if (u.sid != null && u.sid.length() > 0) {
      urlStr.append(":");
      urlStr.append(u.sid);
    }

    if (u.serviceName != null && u.serviceName.length() > 0) {
      urlStr.append("/");
      urlStr.append(u.serviceName);
    }

    return urlStr.toString();
  }

  public String getTestSql() {
    return "select * from dual";
  }

  public List<String[]> getTableIndexes(Connection conn, String tableName) throws SQLException {
    throw new SQLException("getTableIndexes not supported by default");
  }
}
