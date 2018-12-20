/* Copyright (C) 2016 Graham Anderson gandersonsw@gmail.com - All Rights Reserved */
package com.graham.tools;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class DbClientOracleThin extends DbClient {

    @Override
    public DbParsedUrl parseUrl(String urlText) {
      DbParsedUrl url = new DbParsedUrl();
  		int atLoc = urlText.indexOf("@");
  		if (atLoc != -1) {
  			url.driverClass = urlText.substring(0, atLoc + 1);
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

  		return url;
    }

    @Override
    public String createUrl(DbParsedUrl u) {
      StringBuilder urlStr = new StringBuilder();

      urlStr.append(u.driverClass);
      urlStr.append(":@");
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

    @Override
    public String getTestSql() {
      return "select * from dual";
    }

    @Override
    public ArrayList<String[]> getTableIndexes(Connection conn, String tableName) throws SQLException {
      Statement stmt = conn.createStatement();
      ResultSet rs2 = stmt.executeQuery("SELECT index_name, column_name FROM ALL_IND_COLUMNS WHERE table_name = '" + tableName + "' ORDER BY index_name");


      ArrayList arr = new ArrayList<String[]>();
      while (rs2.next()) {
        arr.add(new String[]{rs2.getString(1), rs2.getString(2)});
      }
      rs2.close();
      stmt.close();
      return arr;
    }
}
