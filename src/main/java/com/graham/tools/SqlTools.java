/* Copyright (C) 2016 Graham Anderson gandersonsw@gmail.com - All Rights Reserved */
package com.graham.tools;

import java.io.IOException;
import java.io.Reader;
import java.sql.Clob;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SqlTools {

	private static final String[] KEYWORDS = {"A","ABORT","ABS","ABSOLUTE","ACCESS","ACOS","ACQUIRE","ACTION","ACTIVATE","ADA","ADD","ADDFORM","ADMIN","AFTER","AGGREGATE","ALIAS","ALL","ALLOCATE","ALTER","AN","ANALYZE","AND","ANY","APPEND","ARCHIVE","ARCHIVELOG","ARE","ARRAY","ARRAYLEN","AS","ASC","ASCII","ASIN","ASSERTION","AT","ATAN","AUDIT","AUTHORIZATION","AVG","AVGU","BACKUP","BECOME","BEFORE","BEGIN","BETWEEN","BIGINT","BINARY","BIND","BINDING","BIT","BLOB","BLOCK","BODY","BOOLEAN","BOTH","BREADTH","BREAK","BREAKDISPLAY","BROWSE","BUFFERPOOL","BULK","BY","BYREF","CACHE","CALL","CALLPROC","CANCEL","CAPTURE","CASCADE","CASCADED","CASE","CAST","CATALOG","CCSID","CEILING","CHANGE","CHAR","CHARACTER","CHARTOROWID","CHECK","CLASS","CLOB","CHECKPOINT","CHR","CLEANUP","CLEAR","CLEARROW","CLOSE","CLUSTER","CLUSTERED","COALESCE","COBOL","COLGROUP","COLLATE","COLLATION","COLLECTION","COLUMN","COMMAND","COMMENT","COMMIT","COMPLETION","COMMITTED","COMPILE","COMPLEX","COMPRESS","COMPUTE","CONCAT","CONFIRM","CONNECT","CONNECTION","CONSTRAINT","CONSTRAINTS","CONSTRUCTOR","CONTAINS","CONTAINSTABLE","CONTENTS","CONTINUE","CONTROLFILE","CONTROLROW","CONVERT","COPY","CORRESPONDING","COS","COUNT","COUNTU","CREATE","CROSS","CUBE","CURRENT","CURRENT_DATE","CURRENT_PATH","CURRENT_ROLE","CURRENT_TIME","CURRENT_TIMESTAMP","CURRENT_USER","CURSOR","CVAR","CYCLE","DATA","DATABASE","DATAFILE","DATAHANDLER","DATAPAGES","DATE","DAY","DAYOFMONTH","DAYOFWEEK","DAYOFYEAR","DAYS","DBA","DBCC","DBSPACE","DEALLOCATE","DEC","DECIMAL","DECLARATION","DECLARE","DECODE","DEFAULT","DEFERRABLE","DEFERRED","DEFINE","DEFINITION","DEGREES","DELETE","DEPTH","DEREF","DELETEROW","DENY","DESC","DESCRIBE","DESCRIPTOR","DESTROY","DHTYPE","DESTRUCTOR","DETERMINISTIC","DICTIONARY","DIAGNOSTICS","DIRECT","DISABLE","DISCONNECT","DISK","DISMOUNT","DISPLAY","DISTINCT","DISTRIBUTE","DISTRIBUTED","DO","DOMAIN","DOUBLE","DOWN","DROP","DUMMY","DUMP","DYNAMIC","EACH","EDITPROC","ELSE","ELSEIF","ENABLE","END","ENDDATA","ENDDISPLAY","ENDEXEC","END-EXEC","ENDFORMS","ENDIF","ENDLOOP","EQUALS","ENDSELECT","ENDWHILE","ERASE","ERRLVL","ERROREXIT","ESCAPE","EVENTS","EVERY","EXCEPT","EXCEPTION","EXCEPTIONS","EXCLUDE","EXCLUDING","EXCLUSIVE","EXEC","EXECUTE","EXISTS","EXIT","EXP","EXPLAIN","EXPLICIT","EXTENT","EXTERNAL","EXTERNALLY","EXTRACT","FALSE","FETCH","FIELD","FIELDPROC","FILE","FILLFACTOR","FINALIZE","FINALIZE","FIRST","FLOAT","FLOOR","FLOPPY","FLUSH","FOR","FORCE","FORMDATA","FORMINIT","FORMS","FORTRAN","FOREIGN","FOUND","FREELIST","FREELISTS","FREETEXT","FREETEXTTABLE","FROM","FREE","FULL","FUNCTION","GENERAL","GET","GETCURRENTCONNECTION","GETFORM","GETOPER","GETROW","GLOBAL","GO","GOTO","GRANT","GRANTED","GRAPHIC","GREATEST","GROUP","GROUPING","GROUPS","HASH","HAVING","HOST","HELP","HELPFILE","HOLDLOCK","HOUR","HOURS","IDENTIFIED","IDENTITY","IGNORE","IDENTITYCOL","IF","IFNULL","IIMESSAGE","IIPRINTF","IMMEDIATE","IMPORT","IN","INCLUDE","INCLUDING","INCREMENT","INDEX","INDEXPAGES","INDICATOR","INITCAP","INITIAL","INITIALIZE","INITIALLY","INITRANS","INITTABLE","INNER","INOUT","INPUT","INSENSITIVE","INSERT","INSERTROW","INSTANCE","INSTR","INT","INTEGER","INTEGRITY","INTERFACE","INTERSECT","INTERVAL","INTO","IS","ISOLATION","ITERATE","JOIN","KEY","KILL","LABEL","LANGUAGE","LARGE","LAST","LATERAL","LAYER","LEADING","LEAST","LEFT","LESS","LENGTH","LEVEL","LIKE","LIMIT","LINENO","LINK","LIST","LISTS","LOAD","LOADTABLE","LOCAL","LOCALTIME","LOCALTIMESTAMP","LOCATOR","LOCATE","LOCK","LOCKSIZE","LOG","LOGFILE","LONG","LONGINT","LOWER","LPAD","LTRIM","LVARBINARY","LVARCHAR","MAIN","MANAGE","MANUAL","MAP","MATCH","MAX","MAXDATAFILES","MAXEXTENTS","MAXINSTANCES","MAXLOGFILES","MAXLOGHISTORY","MAXLOGMEMBERS","MAXTRANS","MAXVALUE","MENUITEM","MESSAGE","MICROSECOND","MICROSECONDS","MIN","MINEXTENTS","MINUS","MINUTE","MODIFIES","MINUTES","MINVALUE","MIRROREXIT","MOD","MODE","MODIFY","MODULE","MONEY","MONTH","MONTHS","MOUNT","MOVE","NAMED","NAMES","NATIONAL","NATURAL","NCHAR","NCLOB","NEW","NEXT","NHEADER","NO","NOARCHIVELOG","NOAUDIT","NOCACHE","NOCHECK","NOCOMPRESS","NOCYCLE","NOECHO","NOMAXVALUE","NOMINVALUE","NONCLUSTERED","NONE","NOORDER","NORESETLOGS","NORMAL","NOSORT","NOT","NOTFOUND","NOTRIM","NOWAIT","NULL","NULLIF","NULLVALUE","NUMBER","NUMERIC","OBJECT","NUMPARTS","NVL","OBID","ODBCINFO","OF","OFF","OFFLINE","OFFSETS","OLD","ON","ONCE","ONLINE","ONLY","OPEN","OPERATION","OPENDATASOURCE","OPENQUERY","OPENROWSET","OPTIMAL","OPTIMIZE","OPTION","OR","ORDER","ORDINALITY","OUT","OUTER","OUTPUT","OVER","OVERLAPS","OWN","PACKAGE","PAD","PARAMETER","PARAMETERS","PAGE","PAGES","PARALLEL","PART","PARTIAL","PATH","POSTFIX","PASCAL","PCTFREE","PCTINCREASE","PCTINDEX","PCTUSED","PERCENT","PERM","PERMANENT","PERMIT","PI","PIPE","PLAN","PLI","POSITION","POWER","PRECISION","PREFIX","PREORDER","PREPARE","PRESERVE","PRIMARY","PRINT","PRINTSCREEN","PRIOR","PRIQTY","PRIVATE","PRIVILEGES","PROC","PROCEDURE","PROCESSEXIT","PROFILE","PROGRAM","PROMPT","PUBLIC","PUTFORM","PUTOPER","PUTROW","QUALIFICATION","QUARTER","QUOTA","RADIANS","RAISE","RAISERROR","RAND","RANGE","RAW","READ","READS","READTEXT","REAL","RECURSIVE","REF","RECONFIGURE","RECORD","RECOVER","REDISPLAY","REFERENCES","REFERENCING","RELATIVE","REGISTER","RELEASE","RELOCATE","REMOVE","RENAME","REPEAT","REPEATABLE","REPEATED","REPLACE","REPLICATE","REPLICATION","RESET","RESETLOGS","RESOURCE","RESTORE","RESTRICT","RESULT","RESTRICTED","RESUME","RETRIEVE","RETURN","RETURNS","REUSE","REVOKE","RIGHT","ROLE","ROLES","ROLLBACK","ROLLUP","ROUTINE","ROW","ROWS","ROWCOUNT","ROWGUIDCOL","ROWID","ROWIDTOCHAR","ROWLABEL","ROWNUM","ROWS","RPAD","RRN","RTRIM","RULE","RUN","RUNTIMESTATISTICS","SAVE","SAVEPOINT","SCHEDULE","SCHEMA","SCN","SCREEN","SCROLL","SCOPE","SEARCH","SCROLLDOWN","SCROLLUP","SECOND","SECONDS","SECQTY","SECTION","SEGMENT","SELECT","SEQUENCE","SERIALIZABLE","SERVICE","SESSION","SESSION_USER","SET","SETS","SETUSER","SIN","SIMPLE","SIGN","SHUTDOWN","SHORT","SHARE","SHARED","SETUSER","SIZE","SLEEP","SMALLINT","SNAPSHOT","SOME","SORT","SOUNDEX","SPACE","SPECIFIC","SPECIFICTYPE","SQL","SQLEXCEPTION","SQLBUF","SQLCA","SQLCODE","SQLERROR","SQLSTATE","SQLWARNING","SQRT","START","STATE","STATEMENT","STATIC","STRUCTURE","STATISTICS","STOGROUP","STOP","STORAGE","STORPOOL","SUBMENU","SUBPAGES","SUBSTR","SUBSTRING","SUCCESSFUL","SUFFIX","SUM","SYSTEM_USER","SUMU","SWITCH","SYNONYM","SYSCAT","SYSDATE","SYSFUN","SYSIBM","SYSSTAT","SYSTEM","SYSTIME","SYSTIMESTAMP","TABLE","TABLEDATA","TABLES","TABLESPACE","TAN","TAPE","TEMP","TEMPORARY","TERMINATE","THAN","TEXTSIZE","THEN","THREAD","TIME","TIMEOUT","TIMESTAMP","TIMEZONE_HOUR","TIMEZONE_MINUTE","TINYINT","TO","TOP","TPE","TRACING","TRAILING","TRAN","TRANSACTION","TRANSLATE","TRANSLATION","TREAT","TRIGGER","TRIGGERS","TRIM","TRUE","TRUNCATE","TSEQUAL","TYPE","UID","UNCOMMITTED","UNDER","UNION","UNIQUE","UNKNOWN","UNNEST","UNLIMITED","UNLOADTABLE","UNSIGNED","UNTIL","UP","UPDATE","UPDATETEXT","UPPER","USAGE","USE","USER","USING","UUID","VALIDATE","VALIDPROC","VALIDROW","VALUE","VALUES","VARBINARY","VARCHAR","VARIABLE","VARIABLES","VARYING","VCAT","VERSION","VIEW","VOLUMES","WAITFOR","WEEK","WHEN","WHENEVER","WHERE","WHILE","WITH","WITHOUT","WORK","WRITE","WRITETEXT","YEAR","YEARS","ZONE"};

	public static String makeTextSqlSafe(String txt) {
		return StringTools.replaceAll(txt, SearchContext.createReplaceAllContext("'", "''"));
	}

	public static String formatTimeStamp(Timestamp t) {
		DateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return f.format(new java.util.Date(t.getTime()));
	}

	public static String getSQLDateFormatString() {
		return "YYYY-MM-DD HH24:MI:SS";
	}

	public static SqlInfo parseSQL(String sql) {
		SqlInfo info = new SqlInfo();
		info.sql = sql;
		List<String> tokens = getSqlTokens(sql);
		boolean inFrom = false;
		String tableName = null;
		for (String t : tokens) {
			if (t.equals("FROM") || t.equals("JOIN")) {
				inFrom = true;
			} else if (Arrays.binarySearch(KEYWORDS, t) >= 0) {
				inFrom = false;
				if (tableName != null) {
					info.tables.add(tableName);
					tableName = null;
				}
				if (t.equals("WHERE")) {
			//		if (info.hasWhere) {
			//			info.hasMoreThanOneWhere = true;
			//		}
					info.hasWhere = true;
				}
			} else if (inFrom) {
				if (t.equals(",")) {
					if (tableName != null) {
						info.tables.add(tableName);
						tableName = null;
					}
				} else if (tableName == null) {
					tableName = t;
				} else {
					info.tableShortcuts.put(t, tableName);
					info.tables.add(tableName);
					tableName = null;
				}
			}
		}

		if (tableName != null) {
			info.tables.add(tableName);
			tableName = null;
		}

		return info;
	}


    /**
     *
     * @param txt
     * @return
     */
    private static List<String> getSqlTokens(String txt) {
    	ArrayList<String> ret = new ArrayList<String>();
    	int max = txt.length();
    	boolean inToken = false;
    	int i;
    	int tokenStart = 0;
    	char c;
    	for (i = 0; i < max; i++) {
    		c = txt.charAt(i);
    		if (inToken) {
    			if (Character.isLetterOrDigit(c) || c == '_') {
    			} else {
    				inToken = false;
    				String token = txt.substring(tokenStart, i);
    				ret.add(token.toUpperCase());
    			}
    		} else {
    			if (Character.isLetter(c) || c == '_') {
    				inToken = true;
    				tokenStart = i;
    			}
    		}
    		if (c == ',') {
    			ret.add(String.valueOf(c));
    		}
    	}

		if (inToken) {
			String token = txt.substring(tokenStart, i);
			ret.add(token.toUpperCase());
		}

		return ret;
    }

    public static String getCLOBString(Clob obj) throws SQLException, IOException {
		StringBuilder sb = new StringBuilder();
		Reader rdr = obj.getCharacterStream();
		char buf[] = new char[1024];
		int i;
		while ((i = rdr.read(buf)) > 0) {
			sb.append(buf,0,i);
		}
		rdr.close();
		return sb.toString();
    }

	public static int computeFetchSize(int rowLimit) {
		if (rowLimit > 1000) {
			return 500;
		}
		if (rowLimit > 19) {
			return rowLimit / 2 + 1;
		}
		return 10;
	}

}
