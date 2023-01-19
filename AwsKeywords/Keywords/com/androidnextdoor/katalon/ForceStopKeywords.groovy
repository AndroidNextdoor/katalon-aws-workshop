package com.androidnextdoor.katalon

import com.androidnextdoor.db.DataSourceUtils
import com.kms.katalon.core.annotation.Keyword
import com.kms.katalon.core.configuration.RunConfiguration
import com.kms.katalon.core.exception.StepErrorException as StepErrorException
import com.kms.katalon.core.util.KeywordUtil
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable

import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.text.SimpleDateFormat

class ForceStopKeywords {

	private static String STRING_FILESTAMP = new SimpleDateFormat("MMddyyyyHHmmss").format(System.currentTimeMillis());
	private static String REPORT_DIR = ""

	static void FORCESTOP() {
		throw new StepErrorException("FORCED-STOP")
	}

	static KeywordUtil log = new KeywordUtil()
	DataSourceKeywords datasource = new DataSourceKeywords()
	Connection conn = null

	/**
	 * Force Stop and Close Browser
	 */
	@Keyword
	def forceStop() {
		REPORT_DIR = RunConfiguration.getReportFolder()
		GlobalVariable.Error = 1

		log.markErrorAndStop('FORCE STOPPING')

		try{
			WebUI.takeScreenshot(REPORT_DIR+"\\Error_"+STRING_FILESTAMP+'.png')
		}catch(Exception e){
			log.logInfo(e.toString())
		}
	}
}
