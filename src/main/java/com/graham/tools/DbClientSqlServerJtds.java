/* Copyright (C) 2016 Graham Anderson gandersonsw@gmail.com - All Rights Reserved */
package com.graham.tools;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class DbClientSqlServerJtds extends DbClient {

    @Override
    public DbParsedUrl parseUrl(String urlText) {
      DbParsedUrl url = new DbParsedUrl();
  		int atLoc = urlText.indexOf("://");
  		if (atLoc != -1) {
  			url.driverClass = urlText.substring(0, atLoc);
  			urlText = urlText.substring(atLoc + 3);
  		}

  		int colonLoc = urlText.indexOf(":");
  		if (colonLoc != -1) {
  			url.hostName = urlText.substring(0, colonLoc);
  			urlText = urlText.substring(colonLoc + 1);
  		}

  		int semiLoc = urlText.indexOf(";");
      if (semiLoc == -1) {
        url.port = urlText;
  		} else {
  			url.port = urlText.substring(0, semiLoc);
  			url.extraParams = urlText.substring(semiLoc + 1);
  		}

  		return url;
    }

    @Override
    public String createUrl(DbParsedUrl u) {
      StringBuilder urlStr = new StringBuilder();

      urlStr.append(u.driverClass);
      urlStr.append("://");
      urlStr.append(u.hostName);
      if (u.port != null && u.port.length() > 0) {
        urlStr.append(":");
        urlStr.append(u.port);
      }

      if (u.extraParams != null && u.extraParams.length() > 0) {
        urlStr.append(";");
        urlStr.append(u.extraParams);
      }

      return urlStr.toString();
    }

    @Override
    public String getTestSql() {
        return "select GetDate()";
    }

    @Override
    public ArrayList<String[]> getTableIndexes(Connection conn, String tableName) throws SQLException {
      Statement stmt = conn.createStatement();
      ResultSet rs2 = stmt.executeQuery("exec sp_helpindex '" + tableName + "'");

      ArrayList arr = new ArrayList<String[]>();
      while (rs2.next()) {
        String col = rs2.getString(3);
        for (String coli : col.split(",")) {
          arr.add(new String[]{rs2.getString(1), coli.trim()});
        }
      }
      rs2.close();
      stmt.close();
      return arr;
    }
}
