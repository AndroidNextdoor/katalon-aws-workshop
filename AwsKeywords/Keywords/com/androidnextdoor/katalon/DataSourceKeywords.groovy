package com.androidnextdoor.katalon

import java.sql.Connection
import java.sql.ResultSet
import java.sql.DriverManager
import java.sql.Statement

import com.kms.katalon.core.annotation.Keyword
import com.kms.katalon.core.util.KeywordUtil
import com.androidnextdoor.db.DataSourceUtils
import com.androidnextdoor.aws.AwsUtils
import internal.GlobalVariable

public class DataSourceKeywords {

	private static Connection connection = null;
	private KeywordUtil log = new KeywordUtil();
	AwsUtils awsUtils = new AwsUtils()

	/**
	 * Open and return a connection to database
	 * @param dataFile absolute file path
	 * @return an instance of java.sql.Connection
	 */
	@Keyword
	def getConnection(){
		def profile = GlobalVariable.Aws_Profile
		def region =  GlobalVariable.Aws_Region
		def dbType = GlobalVariable.Db_Type
		def dbConnectionStr = awsUtils.retrieveSecret(profile, region, GlobalVariable.Db_Type, GlobalVariable.Db_Connection_String)
		def dbUser = awsUtils.retrieveSecret(profile, region, GlobalVariable.Db_Type, GlobalVariable.Db_User)
		def dbPass =  awsUtils.retrieveSecret(profile, region, GlobalVariable.Db_Type, GlobalVariable.Db_Password)

		println(dbConnectionStr)

		DataSourceUtils dataSource = new DataSourceUtils()
		connection = dataSource.connectDB(dbType,dbConnectionStr,dbUser,dbPass)
		return connection
	}
}
