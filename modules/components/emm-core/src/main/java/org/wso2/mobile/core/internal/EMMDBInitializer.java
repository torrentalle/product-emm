
/*
 * *
 *  *  Copyright (c) 2005-2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *  *
 *  *  Licensed under the Apache License, Version 2.0 (the "License");
 *  *  you may not use this file except in compliance with the License.
 *  *  You may obtain a copy of the License at
 *  *
 *  *        http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  *  Unless required by applicable law or agreed to in writing, software
 *  *  distributed under the License is distributed on an "AS IS" BASIS,
 *  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  *  See the License for the specific language governing permissions and
 *  *  limitations under the License.
 *
 */

package org.wso2.mobile.core.internal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.mobile.core.EMMDBInitializerException;

import javax.sql.DataSource;
import java.io.*;
import java.sql.*;
import java.util.StringTokenizer;

/**
 * This class executes the sql script in dbscripts/emm folder to initialize the database
 * tables during startup.
 */
class EMMDBInitializer {

	private static final String DBSCRIPTS_LOCATION = "/dbscripts/emm/";

	//DB types
	private static final String DB_TYPE_ORACLE = "oracle";
	private static final String DB_TYPE_DB2 = "db2";
	private static final String DB_TYPE_OPENEDGE = "openedge";
	private static final String DELIMITER_CHAR = "/";
	private static final String DB_TYPE_HSQL = "hsql";
	private static final String DB_TYPE_DERBY = "derby";
	private static final String DB_TYPE_MYSQL = "mysql";
	private static final String DB_TYPE_MSSQL = "mssql";
	private static final String DB_TYPE_H2 = "h2";
	private static final String DB_TYPE_POSTGRESQL = "postgresql";
	private static final String DB_TYPE_INFORMIX = "informix";

	private static final String REM_TOKEN = "REM";

	private static Log log = LogFactory.getLog(EMMDBInitializer.class);
	private static final String DB_CHECK_SQL = "select * from platforms";
	private DataSource dataSource;
	private String delimiter = ";";
	private Statement statement;

	EMMDBInitializer(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	void createEMMDatabase() throws EMMDBInitializerException {
		if (!isDatabaseStructureCreated()) {
			Connection conn = null;
			try {
				conn = dataSource.getConnection();
				conn.setAutoCommit(false);
				statement = conn.createStatement();
				executeSQLScript();
				conn.commit();
				log.info("EMM DB tables created successfully.");
			} catch (SQLException e) {
				String msg = "Failed to create database tables for EMM.";
				log.error(msg, e);
				throw new EMMDBInitializerException(msg, e);
			} finally {
				try {
					if (conn != null) {
						conn.close();
					}
				} catch (SQLException e) {
					log.error("Failed to close database connection.", e);
				}
			}
		} else {
			log.info("EMM database already exists. Not creating a new database.");
		}
	}

	private void executeSQLScript() throws EMMDBInitializerException {

		StringBuffer sql = new StringBuffer();
		BufferedReader reader = null;

		try {
			String databaseType = getDatabaseType(dataSource.getConnection());
			boolean keepFormat = false;
			if (DB_TYPE_ORACLE.equals(databaseType)) {
				delimiter = DELIMITER_CHAR;
			} else if (DB_TYPE_DB2.equals(databaseType)) {
				delimiter = DELIMITER_CHAR;
			} else if (DB_TYPE_OPENEDGE.equals(databaseType)) {
				delimiter = DELIMITER_CHAR;
				keepFormat = true;
			}

			String dbScriptLocation = getDbScriptLocation(databaseType);
			InputStream is = new FileInputStream(dbScriptLocation);
			reader = new BufferedReader(new InputStreamReader(is));
			String line;
			while ((line = reader.readLine()) != null) {
				line = line.trim();
				if (!keepFormat) {
					if (line.startsWith("//")) {
						continue;
					}
					if (line.startsWith("--")) {
						continue;
					}
					StringTokenizer st = new StringTokenizer(line);
					if (st.hasMoreTokens()) {
						String token = st.nextToken();
						if (REM_TOKEN.equalsIgnoreCase(token)) {
							continue;
						}
					}
				}
				sql.append(keepFormat ? "\n" : " ").append(line);

				// SQL defines "--" as a comment to EOL
				// and in Oracle it may contain a hint
				// so we cannot just remove it, instead we must end it
				if (!keepFormat && line.contains("--")) {
					sql.append("\n");
				}
				if ((checkStringBufferEndsWith(sql, delimiter))) {
					executeSQL(sql.substring(0, sql.length() - delimiter.length()));
					sql.replace(0, sql.length(), "");
				}
			}
			// Catch any statements not followed by ;
			if (sql.length() > 0) {
				executeSQL(sql.toString());
			}
		} catch (IOException e) {
			String msg = "Error occurred while opening the SQL script file for creating emm database";
			log.error(msg, e);
			throw new EMMDBInitializerException(msg, e);

		} catch (SQLException e) {
			String msg = "Error occurred while executing SQL script for creating emm database";
			log.error(msg, e);
			throw new EMMDBInitializerException(msg, e);
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					log.error("Error occurred while closing the file connection", e);
				}
			}
		}
	}

	private String getDatabaseType(Connection conn) throws EMMDBInitializerException {
		String type = null;
		try {
			if (conn != null && (!conn.isClosed())) {
				DatabaseMetaData metaData = conn.getMetaData();
				String databaseProductName = metaData.getDatabaseProductName();
				if (databaseProductName.matches("(?i).*hsql.*")) {
					type = DB_TYPE_HSQL;
				} else if (databaseProductName.matches("(?i).*derby.*")) {
					type = DB_TYPE_DERBY;
				} else if (databaseProductName.matches("(?i).*mysql.*")) {
					type = DB_TYPE_MYSQL;
				} else if (databaseProductName.matches("(?i).*oracle.*")) {
					type = DB_TYPE_ORACLE;
				} else if (databaseProductName.matches("(?i).*microsoft.*")) {
					type = DB_TYPE_MSSQL;
				} else if (databaseProductName.matches("(?i).*h2.*")) {
					type = DB_TYPE_H2;
				} else if (databaseProductName.matches("(?i).*db2.*")) {
					type = DB_TYPE_DB2;
				} else if (databaseProductName.matches("(?i).*postgresql.*")) {
					type = DB_TYPE_POSTGRESQL;
				} else if (databaseProductName.matches("(?i).*openedge.*")) {
					type = DB_TYPE_OPENEDGE;
				} else if (databaseProductName.matches("(?i).*informix.*")) {
					type = DB_TYPE_INFORMIX;
				} else {
					String msg = "Unsupported database: " + databaseProductName +
					             ". Database will not be created automatically by the WSO2 EMM Server. " +
					             "Please create the database using appropriate database scripts for " +
					             "the database.";
					throw new EMMDBInitializerException(msg);
				}
			}
		} catch (SQLException e) {
			String msg = "Failed to get metadata from sql connection to create emm database.";
			log.error(msg, e);
			throw new EMMDBInitializerException(msg, e);
		}
		return type;
	}

	private String getDbScriptLocation(String databaseType) {
		String scriptName = databaseType + ".sql";
		log.debug("Loading database script from :" + scriptName);
		String carbonHome = System.getProperty("carbon.home");
		return carbonHome +
		       DBSCRIPTS_LOCATION + scriptName;
	}

	/**
	 * Checks that a string buffer ends up with a given string.
	 *
	 * @param buffer the buffer to perform the check on
	 * @param suffix the suffix
	 * @return <code>true</code> if the character sequence represented by the
	 * argument is a suffix of the character sequence represented by
	 * the StringBuffer object; <code>false</code> otherwise. Note that the
	 * result will be <code>true</code> if the argument is the
	 * empty string.
	 */
	private boolean checkStringBufferEndsWith(StringBuffer buffer, String suffix) {
		if (suffix.length() > buffer.length()) {
			return false;
		}
		int endIndex = suffix.length() - 1;
		int bufferIndex = buffer.length() - 1;
		while (endIndex >= 0) {
			if (buffer.charAt(bufferIndex) != suffix.charAt(endIndex)) {
				return false;
			}
			bufferIndex--;
			endIndex--;
		}
		return true;
	}

	/**
	 * Executes given sql statement
	 *
	 * @param sql
	 * @throws Exception
	 */
	private void executeSQL(String sql) throws EMMDBInitializerException {
		// Check and ignore empty statements
		if ("".equals(sql.trim())) {
			return;
		}

		ResultSet resultSet = null;
		try {
			log.debug("SQL : " + sql);
			boolean ret;
			int updateCount, updateCountTotal = 0;
			ret = statement.execute(sql);
			updateCount = statement.getUpdateCount();
			resultSet = statement.getResultSet();
			do {
				if (!ret) {
					if (updateCount != -1) {
						updateCountTotal += updateCount;
					}
				}
				ret = statement.getMoreResults();
				if (ret) {
					updateCount = statement.getUpdateCount();
					resultSet = statement.getResultSet();
				}
			} while (ret);

			log.debug(sql + " : " + updateCountTotal + " rows affected");
			Connection conn = dataSource.getConnection();
			SQLWarning warning = conn.getWarnings();
			while (warning != null) {
				log.debug(warning + " sql warning");
				warning = warning.getNextWarning();
			}
			conn.clearWarnings();
		} catch (SQLException e) {
			if (e.getSQLState().equals("X0Y32") || e.getSQLState().equals("42710")) {
				log.info("Table Already Exists. Hence, skipping table creation");

			} else {
				throw new EMMDBInitializerException("Error occurred while executing sql statement: " + sql, e);
			}
		} finally {
			if (resultSet != null) {
				try {
					resultSet.close();
				} catch (SQLException e) {
					log.error("Error occurred while closing result set.", e);
				}
			}
		}
	}

	/**
	 * Checks whether database tables are created.
	 *
	 * @return <code>true</core> if checkSQL is success, else <code>false</code>.
	 */
	private boolean isDatabaseStructureCreated() {
		try {
			Connection conn = dataSource.getConnection();
			Statement statement = null;
			try {
				statement = conn.createStatement();
				ResultSet rs = statement.executeQuery(DB_CHECK_SQL);
				if (rs != null) {
					rs.close();
				}
			} finally {
				try {
					if (statement != null) {
						statement.close();
					}
				} finally {
					if (conn != null) {
						conn.close();
					}
				}
			}
		} catch (SQLException e) {
			return false;
		}
		return true;
	}

}
