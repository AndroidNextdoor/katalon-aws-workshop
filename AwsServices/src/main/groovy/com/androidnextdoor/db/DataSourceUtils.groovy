package com.androidnextdoor.db

import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet
import java.sql.Statement
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.util.regex.Matcher
import java.util.regex.Pattern

public class DataSourceUtils {

	private static Connection connection = null;
	private static Logger logger = LoggerFactory.getLogger(DataSourceUtils.class);

	/**
	 * Open and return a connection to database
	 * @param db_type type of database [mysql,sqlserver,oracle,postgres]
	 * @param encrypted_connection_str Encrypted Connection String
	 * @param encrypted_db_user Encrypted User
	 * @param encrypted_db_password Encrypted Password
	 * @return an instance of java.sql.Connection
	 */
	Connection connectDB(String dbType, String connectionString, String username, String password){

		String dbDriver = "";

		if(dbType== "mysql"){
			dbDriver = "com.mysql.cj.jdbc.Driver";
		} else if (dbType == "sqlserver"){
			dbDriver = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
		} else if (dbType == "oracle"){
			dbDriver = "oracle.jdbc.driver.OracleDriver";
		} else if (dbType == "postgres"){
			dbDriver = "org.postgresql.Driver";
		} else {
			dbDriver = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
		}

		Class.forName(dbDriver)

		if(connection != null && !connection.isClosed()){
			connection.close()
		}

		connection = DriverManager.getConnection(connectionString, username, password)
		return connection
	}

	/**
	 * execute a SQL query on database
	 * @param queryString SQL query string
	 * @return a reference to returned data collection, an instance of java.sql.ResultSet
	 */
	ResultSet executeQuery(String queryString, String dbType, String connectionString, String username, String password) {

		ResultSet rs = null
		if(validateSQL(queryString)){
			connection = connectDB(dbType, connectionString, username, password)
			Statement stm = connection.createStatement()
			rs = stm.executeQuery(queryString)
			closeDatabaseConnection(connection)
		} else {
			logger.error("SQL Not Allowed\n\n ${queryString}")
		}
		return rs
	}

	public static boolean validateSQL(String sql) {
		// Regular expression to match any malicious scripts
		String pattern = "(;|'|--|\\|)|(DROP|ALTER|DELETE|CREATE)";

		// Compile the regular expression
		Pattern p = Pattern.compile(pattern);

		// Match the input SQL statement against the pattern
		Matcher m = p.matcher(sql);

		// Return false if any malicious scripts are found
		if (m.find()) {
			return false;
		}

		// Otherwise, return true
		return true;
	}

	static Connection closeDatabaseConnection(Connection connection) {
		if(connection != null && !connection.isClosed()){
			connection.close()
		}
		connection = null

		return connection
	}

	/**
	 * Execute non-query (usually INSERT/UPDATE/DELETE/COUNT/SUM...) on database
	 * @param queryString a SQL statement
	 * @return single value result of SQL statement
	 */
	def execute(String queryString) {
		Statement stm = connection.createStatement()
		boolean result = stm.execute(queryString)
		return result
	}
}
