import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject

import com.kms.katalon.core.checkpoint.Checkpoint as Checkpoint
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.testcase.TestCase as TestCase
import com.kms.katalon.core.testdata.TestData as TestData
import com.kms.katalon.core.testobject.TestObject as TestObject

import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.mobile.keyword.MobileBuiltInKeywords as Mobile

import internal.GlobalVariable as GlobalVariable

import com.kms.katalon.core.annotation.BeforeTestCase
import com.kms.katalon.core.annotation.BeforeTestSuite
import com.kms.katalon.core.annotation.AfterTestCase
import com.kms.katalon.core.annotation.AfterTestSuite
import com.kms.katalon.core.context.TestCaseContext
import com.kms.katalon.core.context.TestSuiteContext

import com.kms.katalon.core.configuration.RunConfiguration


class AwsTestListener {

	/**
	 * Executes before every test suite starts.
	 * @param testSuiteContext: related information of the executed test suite.
	 */
	@BeforeTestSuite
	def beforeTestSuite(TestSuiteContext testSuiteContext) {

		GlobalVariable.Test_Suite = testSuiteContext.getTestSuiteId().replace("Test Suites/","")

		// create UNIQUE_ID and assign to globalVariable.UID
		GlobalVariable.UID = CustomKeywords.'com.androidnextdoor.katalon.TestUtils.generateUniqueId'()
		CustomKeywords.'com.androidnextdoor.katalon.ListenerKeywords.initializeWebUI'(testSuiteContext)
		//CustomKeywords.'com.androidnextdoor.katalon.ListenerKeywords.initializeAPI'(testSuiteContext, START_TIME)
	}

	/**
	 * Executes before every test case starts.
	 * @param testCaseContext related information of the executed test case.
	 */
	@BeforeTestCase
	def beforeTestCase(TestCaseContext testCaseContext) {
		String testCaseName = testCaseContext.getTestCaseId().replace("Test Cases","")

		if (GlobalVariable.Error == 1) {
//			This skips through tests cases after one failure
			println "Skipping: "+testCaseName
			testCaseContext.skipThisTestCase()
		}

	}

	/**
	 * Executes after every test case ends.
	 * @param testCaseContext related information of the executed test case.
	 */
	@AfterTestCase
	def afterTestCase(TestCaseContext testCaseContext) {
		String testCaseName = testCaseContext.getTestCaseId()
		
		if (testCaseContext.getTestCaseStatus() == "FAILED") {
			GlobalVariable.Failed_Test_Case = testCaseContext.getTestCaseId();
			GlobalVariable.Error = 1
		}
	}

	/**
	 * Executes after every test suite ends.
	 * @param testSuiteContext: related information of the executed test suite.
	 */
	@AfterTestSuite
	def afterTestSuite(TestSuiteContext testSuiteContext) {
		CustomKeywords.'com.androidnextdoor.katalon.ListenerKeywords.finishUpTesting'(testSuiteContext)
		//CustomKeywords.'com.androidnextdoor.katalon.ListenerKeywords.finishUpTestingAPI'(testSuiteContext, DURATION)
	}
}
