package com.androidnextdoor.katalon

import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import static com.kms.katalon.core.testobject.ObjectRepository.findWindowsObject

import com.kms.katalon.core.annotation.Keyword
import com.kms.katalon.core.checkpoint.Checkpoint
import com.kms.katalon.core.cucumber.keyword.CucumberBuiltinKeywords as CucumberKW
import com.kms.katalon.core.mobile.keyword.MobileBuiltInKeywords as Mobile
import com.kms.katalon.core.model.FailureHandling
import com.kms.katalon.core.testcase.TestCase
import com.kms.katalon.core.testdata.TestData
import com.kms.katalon.core.testobject.TestObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.windows.keyword.WindowsBuiltinKeywords as Windows
import com.kms.katalon.core.util.KeywordUtil
import internal.GlobalVariable

import java.text.SimpleDateFormat
import com.kms.katalon.core.context.TestCaseContext
import com.kms.katalon.core.context.TestSuiteContext
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.kms.katalon.core.configuration.RunConfiguration
import org.openqa.selenium.WebDriver
import org.openqa.selenium.remote.RemoteWebDriver
import org.openqa.selenium.remote.SessionId
import org.openqa.selenium.support.events.EventFiringWebDriver
import com.kms.katalon.core.webui.driver.DriverFactory

import com.androidnextdoor.aws.AwsUtils
import org.openqa.selenium.chrome.ChromeOptions
import org.openqa.selenium.firefox.FirefoxOptions
import org.openqa.selenium.edge.EdgeOptions
import java.sql.Connection

public class ListenerKeywords {
	private KeywordUtil log = new KeywordUtil();

	private static String USERNAME = ""
	private static String BROWSER = ""
	private static String BROWSER_VERSION = ""
	private static String RESOLUTION = ""
	private static String PLATFORM = ""
	private static String STARTING_URL = ""
	private static String URL = ""
	private static String GRID_URL = ""
	private static String AWS_DEVICE_FARM_NAME = ""
	private static String AWS_DEVICE_FARM_ARN = ""
	private static String AWS_DEVICE_FARM_SECRET_ARN = ""
	private static String AWS_DEVICE_FARM_PROJECT_ID = ""
	private static String AWS_PROFILE = ""
	private static String AWS_REGION = ""
	private static String FAILED_TEST_CASE = ""
	private static String FAILED_TEST_SCREENSHOT = ""
	private static String SUCCESSFUL_TEST_SCREENSHOT = ""
	private static String TEST_SUITE = ""
	private static String TEST_SUITE_COLLECTION = ""
	private static String TEST_SUITE_STATUS = "PASSED"
	private static String UNIQUE_ID = ""
	private static String SESSION_ID = ""
	private static String DURATION_STRING = ""
	private static String VIDEO_URL = ""
	private static String MAX_TIMEOUT = "1200"
	private static Integer SYSTEM_ERROR = 0
	private static String REPORT_DIR = ""
	private static String STRING_TIMESTAMP = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(System.currentTimeMillis())
	private static String STRING_FILESTAMP = new SimpleDateFormat("MMddyyyyHHmmss").format(System.currentTimeMillis())
	private static String SOURCE = ""
	private static Integer ORIGINAL_TEST_RESULT = 0
	private static String REMOTE_DRIVER_TYPE = ""
	private static String PROFILE = ""
	private static String CUSTOMER = ""
	private static String DEVICE_FARM_ARN = ""
	private static String[] NEXT_STAGE_ARRAY = []
	private static boolean PASSED = 0
	Connection conn = null
	DataSourceKeywords datasource = new DataSourceKeywords()
	AwsUtils awsUtils = new AwsUtils()

	@Keyword
	def initializeWebUI(TestSuiteContext testSuiteContext) {

		STARTING_URL = GlobalVariable.Starting_Url
		URL = GlobalVariable.Url
		USERNAME = GlobalVariable.Username
		FAILED_TEST_CASE = GlobalVariable.Failed_Test_Case
		SOURCE = GlobalVariable.Source
		PLATFORM = GlobalVariable.Platform
		BROWSER = GlobalVariable.Browser
		BROWSER_VERSION = GlobalVariable.Browser_Version

		AWS_PROFILE = GlobalVariable.Aws_Profile
		AWS_REGION = GlobalVariable.Aws_Region
		AWS_DEVICE_FARM_NAME = GlobalVariable.Device_Farm_Name
		DEVICE_FARM_ARN = GlobalVariable.Device_Farm_ARN

		AWS_DEVICE_FARM_SECRET_ARN = awsUtils.retrieveSecret(AWS_PROFILE, AWS_REGION, AWS_DEVICE_FARM_NAME, DEVICE_FARM_ARN)
		//1200 is 20 minutes
		MAX_TIMEOUT = GlobalVariable.Max_Timeout_Seconds

		TEST_SUITE = testSuiteContext.getTestSuiteId().replace("Test Suites/","")
		GlobalVariable.Test_Suite = TEST_SUITE

		UNIQUE_ID = GlobalVariable.UID

		REPORT_DIR = RunConfiguration.getReportFolder()

		CUSTOMER = RunConfiguration.getProjectDir().split('/').last()

		PROFILE = RunConfiguration.getExecutionProfile()

		conn = datasource.getConnection()

		REMOTE_DRIVER_TYPE = DriverFactory.getRemoteWebDriverServerType()
		//Checks if a Remote Driver is running

		log.logInfo("Remote Driver Type ::: ${REMOTE_DRIVER_TYPE}")

		if(REMOTE_DRIVER_TYPE) {
			AwsUtils aws = new AwsUtils()
			GRID_URL = aws.getGridUrl(AWS_PROFILE, AWS_DEVICE_FARM_SECRET_ARN, MAX_TIMEOUT)

			if(GRID_URL != '') {
				log.logInfo("CONNECTING TO GRID URL: " + GRID_URL)

				// https://docs.aws.amazon.com/devicefarm/latest/testgrid/techref-support.html#techref-support-capabilities
				RunConfiguration.setDriverPreferencesProperty("Remote", "remoteWebDriverUrl", GRID_URL)
				RunConfiguration.setDriverPreferencesProperty("Remote", "platform", PLATFORM)
				RunConfiguration.setDriverPreferencesProperty("Remote", "browserName", BROWSER)
				RunConfiguration.setDriverPreferencesProperty("Remote", "browserVersion", BROWSER_VERSION)
				// 20 Minute Timeout
				RunConfiguration.setDriverPreferencesProperty("Remote","aws:maxDurationSecs", MAX_TIMEOUT)

				if(BROWSER == "chrome") {
					// put specific chrome options below
					ChromeOptions options = new ChromeOptions();
					RemoteWebDriver driver = new RemoteWebDriver(new URL(GRID_URL), options);

					//Hi-jacks Remote Driver Here
					DriverFactory.changeWebDriver(driver);
					driver.get(STARTING_URL);
				} else if (BROWSER == "firefox") {
					FirefoxOptions options = new FirefoxOptions()
					RemoteWebDriver driver = new RemoteWebDriver(new URL(GRID_URL), options);

					//Hi-jacks Remote Driver Here
					DriverFactory.changeWebDriver(driver);
					driver.get(STARTING_URL);
				} else if (BROWSER == "MicrosoftEdge") {
					EdgeOptions options = new EdgeOptions()
					RemoteWebDriver driver = new RemoteWebDriver(new URL(GRID_URL), options);

					//Hi-jacks Remote Driver Here
					DriverFactory.changeWebDriver(driver);
					driver.get(STARTING_URL);
				}
			}
		}
	}

	@Keyword
	def updateResults(TestCaseContext testCaseContext){
		WebDriver driver = DriverFactory.getWebDriver();
		String browserVersion = DriverFactory.getBrowserVersion(driver)
		log.logInfo("Browser Version ::: ${browserVersion}")

		def browserParts = browserVersion.split(" ", 2);
		BROWSER = browserParts[0]
		BROWSER_VERSION = browserParts[1]
		REPORT_DIR = RunConfiguration.getReportFolder()
		REMOTE_DRIVER_TYPE = DriverFactory.getRemoteWebDriverServerType()

		if(REMOTE_DRIVER_TYPE){
			log.logInfo("Remote Driver Type ::: ${REMOTE_DRIVER_TYPE}")
			ObjectMapper objectMapper = new ObjectMapper();

			// Cast Katalon's WebDriver into EventFiringWebDriver
			EventFiringWebDriver eventFiring = (EventFiringWebDriver) DriverFactory.getWebDriver()
			// Get the driver wrapped inside
			WebDriver wrappedWebDriver = eventFiring.getWrappedDriver()
			// Cast the wrapped driver into RemoteWebDriver
			RemoteWebDriver remoteWebDriver = (RemoteWebDriver) wrappedWebDriver
			SessionId session = remoteWebDriver.getSessionId();
			SESSION_ID = session.toString();
			RESOLUTION = remoteWebDriver.manage().window().getSize()
			AWS_DEVICE_FARM_PROJECT_ID = GlobalVariable.Device_Farm_Project_ID
			VIDEO_URL = 'https://us-west-2.console.aws.amazon.com/devicefarm/home?region=us-west-2#/browser/projects/'+AWS_DEVICE_FARM_PROJECT_ID+'/runsselenium/logs/'+SESSION_ID
			GlobalVariable.Video_Url

			WebUI.comment("VIDEO URL: "+VIDEO_URL)
		}
	}

	@Keyword
	def finishUpTesting(TestSuiteContext testSuiteContext){

		TEST_SUITE = testSuiteContext.getTestSuiteId().replace("Test Suites/","")
		UNIQUE_ID = GlobalVariable.UID
		WebDriver driver = DriverFactory.getWebDriver();

		REMOTE_DRIVER_TYPE = DriverFactory.getRemoteWebDriverServerType()
		log.logInfo("Remote Driver Type ::: ${REMOTE_DRIVER_TYPE}")

		SYSTEM_ERROR = GlobalVariable.System_Error

		REPORT_DIR = RunConfiguration.getReportFolder()

		if(GlobalVariable.Failed_Test_Case != '' || GlobalVariable.Error == 1){
			WebUI.delay(5, FailureHandling.STOP_ON_FAILURE)
			FAILED_TEST_SCREENSHOT = REPORT_DIR + '\\' + UNIQUE_ID + "_Error_" + STRING_FILESTAMP + ".png"
			WebUI.takeScreenshot(FAILED_TEST_SCREENSHOT)
		} else {
			// IMPORTANT! - THIS IS WHERE IT PASSES THE TEST
			PASSED = 1
			WebUI.delay(5, FailureHandling.STOP_ON_FAILURE)
			SUCCESSFUL_TEST_SCREENSHOT = REPORT_DIR + '\\' + UNIQUE_ID + "_" + STRING_FILESTAMP + ".png"
			// Takes a screenshot
			WebUI.takeScreenshot(SUCCESSFUL_TEST_SCREENSHOT)
		}

		//For Selenium Grid
		if(REMOTE_DRIVER_TYPE){
			log.logInfo("Remote Drive Shutting down.")
			ObjectMapper objectMapper = new ObjectMapper();

			// Cast Katalon's WebDriver into EventFiringWebDriver
			EventFiringWebDriver eventFiring = (EventFiringWebDriver) DriverFactory.getWebDriver()
			// Get the driver wrapped inside
			WebDriver wrappedWebDriver = eventFiring.getWrappedDriver()
			// Cast the wrapped driver into RemoteWebDriver
			RemoteWebDriver remoteWebDriver = (RemoteWebDriver) wrappedWebDriver
			SessionId session = remoteWebDriver.getSessionId();
			SESSION_ID = session.toString();

			String result = "passed"
			if( GlobalVariable.Failed_Test_Case != "" || GlobalVariable.Error == 1 ){
				result="failed"
				PASSED = 0
			}

			try{
				// Get all console errors
				// Do whatever you need by executing your java script here


				// WebUI.executeJavaScript("CALL")
			}catch (Exception e){
				log.logInfo e.toString()
			}finally{
				WebUI.closeBrowser()
				remoteWebDriver.quit()
			}

		} else {
			WebUI.closeBrowser()
		}

	}

}
