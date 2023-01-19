package com.androidnextdoor.katalon


import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.annotation.Keyword
import com.kms.katalon.core.util.KeywordUtil
import com.kms.katalon.core.testobject.TestObject as TestObject
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable
import com.kms.katalon.core.webui.driver.DriverFactory as DriverFactory
import org.openqa.selenium.Rectangle as Rectangle
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject

class BrowserKeywords {

	static KeywordUtil log = new KeywordUtil()
	private ForceStopKeywords stop = new ForceStopKeywords()

	/**
	 * Open Browser
	 */
	@Keyword
	def openBrowser() {
		String siteLink = GlobalVariable.Url

		if (GlobalVariable.Failed_Test_Case.equals('')) {
			try {

				String REMOTE_DRIVER_TYPE = DriverFactory.getRemoteWebDriverServerType()

				if(REMOTE_DRIVER_TYPE == null) {
					WebUI.openBrowser(siteLink)
				}

				WebUI.waitForPageLoad(45, FailureHandling.STOP_ON_FAILURE)

				WebUI.maximizeWindow()
			}
			catch (com.kms.katalon.core.exception.StepFailedException e) {
				GlobalVariable.Failed_Test_Case = 'Could Not Open Browser'
				ForceStopKeywords stop = new ForceStopKeywords()
				stop.forceStop(9)

				log.logInfo(e.toString())
			}
		}
	}

	/**
	 * Try and loop through a couple times to catch a button click
	 */
	@Keyword
	def tryClickButton(String testObject) {

		def count = 0

		def finished = false

		while(count < 5 && !finished) {
			log.logInfo("Trying to Click Button")

			try {
				WebUI.waitForElementClickable(findTestObject(testObject), 2)

				WebUI.click(findTestObject(testObject))

				log.logInfo("Success Clicking ${testObject}")

				finished = true
			}
			catch (com.kms.katalon.core.exception.StepFailedException e) {
				WebUI.takeFullPageScreenshot()
				count++
				log.logInfo(count.toString() + " Unsuccessful Attempt to Click ${testObject}: " + e.toString())
			}
		}

		if(!finished) {
			stop.forceStop(1)
		}
	}
}
