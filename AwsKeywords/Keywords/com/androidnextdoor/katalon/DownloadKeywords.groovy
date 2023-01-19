package com.androidnextdoor.katalon

import java.awt.Robot;
import java.awt.event.KeyEvent;
import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject

import org.apache.commons.codec.binary.Base64;
import com.kms.katalon.core.annotation.Keyword
import com.kms.katalon.core.checkpoint.Checkpoint
import com.kms.katalon.core.checkpoint.CheckpointFactory
import com.kms.katalon.core.mobile.keyword.MobileBuiltInKeywords
import com.kms.katalon.core.model.FailureHandling
import com.kms.katalon.core.testcase.TestCase
import com.kms.katalon.core.testcase.TestCaseFactory
import com.kms.katalon.core.testdata.TestData
import com.kms.katalon.core.testdata.TestDataFactory
import com.kms.katalon.core.testobject.ObjectRepository
import com.kms.katalon.core.testobject.TestObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords

import internal.GlobalVariable

import org.openqa.selenium.WebElement
import org.openqa.selenium.WebDriver
import org.openqa.selenium.By

import com.kms.katalon.core.mobile.keyword.internal.MobileDriverFactory
import com.kms.katalon.core.webui.driver.DriverFactory

import com.kms.katalon.core.testobject.RequestObject
import com.kms.katalon.core.testobject.ResponseObject
import com.kms.katalon.core.testobject.ConditionType
import com.kms.katalon.core.testobject.TestObjectProperty

import com.kms.katalon.core.mobile.helper.MobileElementCommonHelper
import com.kms.katalon.core.util.KeywordUtil

import com.kms.katalon.core.webui.exception.WebElementNotFoundException

import java.nio.file.Files
import java.nio.file.Paths

import com.kms.katalon.core.configuration.RunConfiguration as RC
import com.kms.katalon.core.exception.StepFailedException
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import javax.xml.bind.DatatypeConverter
import org.openqa.selenium.Keys
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.support.events.EventFiringWebDriver;
import com.kms.katalon.core.windows.keyword.WindowsBuiltinKeywords as Windows


class DownloadKeywords {

	/***
	 * Wait for the file to exist in the specified directory within the given timeout
	 * 
	 * @param dir
	 * 			name of the directory (or relative path - which is appended to the user's HOME directory) where file is expected to be located after download
	 * @param fileName
	 * 			name of the file to be downloaded (including file extenstion)
	 * @param timeout
	 * 			how many seconds to wait
	 * @return
	 *			true if file is located in the specified directory
	 */
	@Keyword
	def boolean isFileDownloaded(String dir, String fileName, int timeout) {
		KeywordUtil.logInfo("Verifying download of file " + fileName + " at path: " + dir)

		Map<String, String> env = System.getenv();

		// For Mac + Linux
		String homeEnv = System.getenv('HOME')

		KeywordUtil.logInfo("File path: " + Paths.get(homeEnv, dir, fileName).toString())

		long timeoutMilliseconds = timeout * 1000
		long waitPeriodMilliseconds = 100
		long totalMillisecondsWaited = 0

		boolean fileDownloaded = false

		while (!fileDownloaded && totalMillisecondsWaited < timeoutMilliseconds) {
			fileDownloaded = Files.exists(Paths.get(homeEnv, dir, fileName))

			if (!fileDownloaded) {
				Thread.sleep(waitPeriodMilliseconds)
				totalMillisecondsWaited += waitPeriodMilliseconds
			}
		}

		if (fileDownloaded) {
			KeywordUtil.markPassed(fileName + " exists at path: " + dir)
		} else {
			KeywordUtil.markFailed("Unable to locate " + fileName + " at path: " + dir)
		}

		return fileDownloaded
	}

	/***
	 * Wait for the file to exist in the Reports folder within the given timeout
	 *
	 * @param fileName
	 * 			name of the file to be downloaded (including file extenstion)
	 * @param timeout
	 * 			how many seconds to wait
	 * @return
	 *			true if file is located in the Reports folder
	 */
	@Keyword
	def boolean isFileDownloadedToReportsFolder(String fileName, int timeout) {
		KeywordUtil.logInfo("Verifying download of file " + fileName)

		Map<String, String> env = System.getenv();

		// For Mac + Linux
		String reportFolder = RC.getReportFolder()
		File reportDir = new File(reportFolder)


		String deviceFarmHome = "C:\\Users\\testnode\\chrome_user_data\\"
		KeywordUtil.logInfo("Device Farm dir: " + deviceFarmHome)
		KeywordUtil.logInfo("Report dir: " + reportFolder)
		KeywordUtil.logInfo("Report dir absolute path: " + reportDir.absolutePath)

		KeywordUtil.logInfo("File path: " + Paths.get(deviceFarmHome, fileName).toString())

		long timeoutMilliseconds = timeout * 1000
		long waitPeriodMilliseconds = 100
		long totalMillisecondsWaited = 0

		boolean fileDownloaded = false

		while (!fileDownloaded && totalMillisecondsWaited < timeoutMilliseconds) {
			fileDownloaded = Files.exists(Paths.get(reportFolder, fileName))

			if (!fileDownloaded) {
				Thread.sleep(waitPeriodMilliseconds)
				totalMillisecondsWaited += waitPeriodMilliseconds
			}
		}

		if (fileDownloaded) {
			KeywordUtil.markPassed(fileName + " exists at path: " + reportFolder)
		} else {
			KeywordUtil.markFailed("Unable to locate " + fileName + " at path: " + reportFolder)
		}

		return fileDownloaded
	}

	public String getDownloadedFileNameBySubStringChrome(String Matcher) {

		String file = "";
		String fileNameList = "";
		KeywordUtil log = new KeywordUtil();

		def REMOTE_DRIVER_TYPE = DriverFactory.getRemoteWebDriverServerType()
		if(REMOTE_DRIVER_TYPE) {

			Windows.switchToWindowTitle("Save As")

			// Cast Katalon's WebDriver into EventFiringWebDriver
			EventFiringWebDriver eventFiring = (EventFiringWebDriver) DriverFactory.getWebDriver()
			// Get the driver wrapped inside
			WebDriver wrappedWebDriver = eventFiring.getWrappedDriver()
			// Cast the wrapped driver into RemoteWebDriver
			RemoteWebDriver remoteWebDriver = (RemoteWebDriver) wrappedWebDriver

			//The script below returns the list of files as a list of the form '[$FileName1, $FileName2...]'
			// with the most recently downloaded file listed first.
			if(!remoteWebDriver.getCurrentUrl().startsWith("chrome://downloads/")) {
				remoteWebDriver.get("chrome://downloads/");
			}

			fileNameList = WebUI.executeJavaScript(
					"return  document.querySelector('downloads-manager')  "+
					" .shadowRoot.querySelector('#downloadsList')         "+
					" .items.filter(e => e.state === 'COMPLETE')          "+
					" .map(e => e.filePath || e.file_path || e.fileUrl || e.file_url); ", null);

			log.logInfo(fileNameList);
			//Removing square brackets
			fileNameList = fileNameList.substring(1, fileNameList.length() -1);
			log.logInfo(fileNameList);
			String [] fileNames = fileNameList.split(",");
			for(int i=0; i<fileNames.length; i++) {
				if(fileNames[i].trim().contains(Matcher)) {
					log.logInfo("Found ${fileNames[i].trim()} By ${Matcher}")
					file = fileNames[i].trim();

					String content = get_file_content(remoteWebDriver, file)
					log.logInfo(content)
					try {
						String reportFolder = RC.getReportFolder()
						File download_url = new File((String) "file://${file}");

						FileOutputStream fos = new FileOutputStream(reportFolder + download_url .getName());

						byte[] decoder = Base64.decodeBase64(content.substring(content.indexOf("base64,")+7));
						fos.write(decoder);
						log.logInfo("File saved to local.");
					} catch (Exception e) {
						e.printStackTrace();
					}

					remoteWebDriver.get("file://${file}");

					Thread.sleep(10000);
				}
			}

		} else {
			WebDriver driver = DriverFactory.getWebDriver();

			//The script below returns the list of files as a list of the form '[$FileName1, $FileName2...]'
			// with the most recently downloaded file listed first.
			if(!driver.getCurrentUrl().startsWith("chrome://downloads/")) {
				driver.get("chrome://downloads/");
			}

			fileNameList = WebUI.executeJavaScript(
					"return  document.querySelector('downloads-manager')  "+
					" .shadowRoot.querySelector('#downloadsList')         "+
					" .items.filter(e => e.state === 'COMPLETE')          "+
					" .map(e => e.filePath || e.file_path || e.fileUrl || e.file_url); ", null);

			log.logInfo(fileNameList);
			//Removing square brackets
			fileNameList = fileNameList.substring(1, fileNameList.length() -1);
			log.logInfo(fileNameList);
			String [] fileNames = fileNameList.split(",");
			for(int i=0; i<fileNames.length; i++) {
				if(fileNames[i].trim().contains(Matcher)) {
					log.logInfo("Found ${fileNames[i].trim()} By ${Matcher}")
					file = fileNames[i].trim();

					String content = get_file_content_local(driver, file)
					log.logInfo(content)
					try {
						String home = System.getProperty("user.home");
						File download_url = new File((String) "file://${file}");

						FileOutputStream fos = new FileOutputStream(home +"/downloads/" + download_url .getName());

						byte[] decoder = Base64.decodeBase64(content.substring(content.indexOf("base64,")+7));
						fos.write(decoder);
						log.logInfo("File saved to local.");
					} catch (Exception e) {
						e.printStackTrace();
					}

					driver.get("file://${file}");

					Thread.sleep(10000);
				}
			}
		}

		return fileNameList;

	}

	public void test_download_file_remote(String downloadPath) throws Exception {
		File download_url = new File((String) "file://${downloadPath}");
		WebDriver driver = DriverFactory.getWebDriver();
		driver.get(download_url.toString());

		int count = 1;
		while (count < 11) {
			if (get_downloaded_files((RemoteWebDriver) driver).toString().contains(  (download_url.getName().substring(0,download_url.getName().indexOf(".")-1)) )){ //Note: multiple file downloads on the same grid node of the same file name will increment the file name like 50MB(2).zip
				System.out.println("FILE DOWNLOADED");
				break;
			} else {
				System.out.println("DOWNLOAD PROGRESS: " + get_download_progress_all((RemoteWebDriver) driver));
			}
			count++;
			Thread.sleep(10000);
		}

		ArrayList downloaded_files_arraylist = get_downloaded_files((RemoteWebDriver) driver);
		String content = get_file_content((RemoteWebDriver) driver,(String) downloaded_files_arraylist.get(0));// large files might need and increase in implicit wait.
		try {
			String reportFolder = RC.getReportFolder()

			FileOutputStream fos = new FileOutputStream(reportFolder + download_url .getName());

			byte[] decoder = Base64.decodeBase64(content.substring(content.indexOf("base64,")+7));
			fos.write(decoder);
			System.out.println("File saved to local.");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void test_download_file_local(String downloadPath) throws Exception {
		KeywordUtil log = new KeywordUtil();
		File download_url = new File((String) "file://${downloadPath}");
		WebDriver driver = DriverFactory.getWebDriver();
		driver.get(download_url.toString());

		int count = 1;
		while (count < 11) {
			if (get_downloaded_files_local(driver).toString().contains(  (download_url.getName().substring(0,download_url.getName().indexOf(".")-1)) )){ //Note: multiple file downloads on the same grid node of the same file name will increment the file name like 50MB(2).zip
				log.logInfo("FILE DOWNLOADED");
				break;
			} else {
				log.logInfo("DOWNLOAD PROGRESS: " + get_download_progress_all_local(driver));
			}
			count++;
			Thread.sleep(10000);
		}

		ArrayList downloaded_files_arraylist = get_downloaded_files_local(driver);
		String content = get_file_content(driver,downloaded_files_arraylist.get(0));// large files might need and increase in implicit wait.
		try {
			String home = System.getProperty("user.home");

			FileOutputStream fos = new FileOutputStream(home + "/downloads/" + download_url .getName());

			byte[] decoder = Base64.decodeBase64(content.substring(content.indexOf("base64,")+7));
			fos.write(decoder);
			System.out.println("File saved to local.");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	String get_file_content(RemoteWebDriver remoteDriver,String path) {
		String file_content = null;
		try {
			if(!remoteDriver.getCurrentUrl().startsWith("chrome://downloads")) {
				remoteDriver.get("chrome://downloads/");
			}


			WebElement elem = (WebElement) remoteDriver.executeScript(
					"var input = window.document.createElement('INPUT'); "+
					"input.setAttribute('type', 'file'); "+
					"input.hidden = true; "+
					"input.onchange = function (e) { e.stopPropagation() }; "+
					"return window.document.documentElement.appendChild(input); "
					, "");

			elem.sendKeys(path);

			file_content = (String) remoteDriver.executeAsyncScript(
					"var input = arguments[0], callback = arguments[1]; "+
					"var reader = new FileReader(); "+
					"reader.onload = function (ev) { callback(reader.result) }; "+
					"reader.onerror = function (ex) { callback(ex.message) }; "+
					"reader.readAsDataURL(input.files[0]); "+
					"input.remove(); "
					, elem);

			if (!file_content.startsWith("data:")){
				System.out.println("Failed to get file content");
			}

		} catch (Exception e) {
			System.err.println(e);
		}
		return file_content;

	}

	String get_file_content_local(WebDriver driver, String path) {
		String file_content = null;
		KeywordUtil log = new KeywordUtil();
		try {
			if(!driver.getCurrentUrl().startsWith("chrome://downloads")) {
				driver.get("chrome://downloads/");
			}

			JavascriptExecutor js = (JavascriptExecutor) driver;

			WebElement elem = (WebElement) js.executeScript(
					"var input = window.document.createElement('INPUT'); "+
					"input.setAttribute('type', 'file'); "+
					"input.hidden = true; "+
					"input.onchange = function (e) { e.stopPropagation() }; "+
					"return window.document.documentElement.appendChild(input); "
					, "");

			elem.sendKeys(path);

			file_content = js.executeAsyncScript(
					"var input = arguments[0], callback = arguments[1]; "+
					"var reader = new FileReader(); "+
					"reader.onload = function (ev) { callback(reader.result) }; "+
					"reader.onerror = function (ex) { callback(ex.message) }; "+
					"reader.readAsDataURL(input.files[0]); "+
					"input.remove(); "
					, elem);

			if (!file_content.startsWith("data:")){
				log.logInfo("Failed to get file content");
			}

		} catch (Exception e) {
			log.logInfo(e.toString());
		}
		return file_content;

	}

	ArrayList get_downloaded_files(RemoteWebDriver remoteDriver) {
		ArrayList filesFound = null;
		try {
			if(!remoteDriver.getCurrentUrl().startsWith("chrome://downloads")) {
				remoteDriver.get("chrome://downloads/");
			}
			filesFound =  (ArrayList)  remoteDriver.executeScript(
					"return  document.querySelector('downloads-manager')  "+
					" .shadowRoot.querySelector('#downloadsList')         "+
					" .items.filter(e => e.state === 'COMPLETE')          "+
					" .map(e => e.filePath || e.file_path || e.fileUrl || e.file_url); ","");
		} catch (Exception e) {
			System.err.println(e);
		}
		return filesFound;
	}

	ArrayList get_downloaded_files_local(WebDriver driver) {
		ArrayList filesFound = null;
		try {
			if(!driver.getCurrentUrl().startsWith("chrome://downloads")) {
				driver.get("chrome://downloads/");
			}
			filesFound =  (ArrayList)  WebUI.executeJavaScript(
					"return  document.querySelector('downloads-manager')  "+
					" .shadowRoot.querySelector('#downloadsList')         "+
					" .items.filter(e => e.state === 'COMPLETE')          "+
					" .map(e => e.filePath || e.file_path || e.fileUrl || e.file_url); ","");
		} catch (Exception e) {
			System.err.println(e);
		}
		return filesFound;
	}

	String get_download_progress(RemoteWebDriver remoteDriver) {
		String progress = null;
		try {
			if(!remoteDriver.getCurrentUrl().startsWith("chrome://downloads")) {
				remoteDriver.get("chrome://downloads/");
			}
			progress=  (String) remoteDriver.executeScript(
					"var tag = document.querySelector('downloads-manager').shadowRoot;"+
					"var intag = tag.querySelector('downloads-item').shadowRoot;"+
					"var progress_tag = intag.getElementById('progress');"+
					"var progress = null;"+
					" if(progress_tag) { "+
					"    progress = progress_tag.value; "+
					"  }" +
					"return progress;"
					,"");


		} catch (Exception e) {
			System.err.println(e);
		}
		return progress;
	}

	ArrayList get_download_progress_all(RemoteWebDriver remoteDriver) {
		ArrayList progress = null;
		try {
			if(!remoteDriver.getCurrentUrl().startsWith("chrome://downloads")) {
				remoteDriver.get("chrome://downloads/");
			}
			progress=  (ArrayList) remoteDriver.executeScript(
					" var tag = document.querySelector('downloads-manager').shadowRoot;" +
					"			    var item_tags = tag.querySelectorAll('downloads-item');" +
					"			    var item_tags_length = item_tags.length;" +
					"			    var progress_lst = [];" +
					"			    for(var i=0; i<item_tags_length; i++) {" +
					"			        var intag = item_tags[i].shadowRoot;" +
					"			        var progress_tag = intag.getElementById('progress');" +
					"			        var progress = null;" +
					"			        if(progress_tag) {" +
					"			            var progress = progress_tag.value;" +
					"			        }" +
					"			        progress_lst.push(progress);" +
					"			    }" +
					"			    return progress_lst",
					"");


		} catch (Exception e) {
			System.err.println(e);
		}
		return progress;
	}

	ArrayList get_download_progress_all_local(WebDriver driver) {
		ArrayList progress = null;
		JavascriptExecutor js = (JavascriptExecutor) driver;
		try {
			if(!driver.getCurrentUrl().startsWith("chrome://downloads")) {
				driver.get("chrome://downloads/");
			}
			progress=  (ArrayList) js.executeScript(
					" var tag = document.querySelector('downloads-manager').shadowRoot;" +
					"			    var item_tags = tag.querySelectorAll('downloads-item');" +
					"			    var item_tags_length = item_tags.length;" +
					"			    var progress_lst = [];" +
					"			    for(var i=0; i<item_tags_length; i++) {" +
					"			        var intag = item_tags[i].shadowRoot;" +
					"			        var progress_tag = intag.getElementById('progress');" +
					"			        var progress = null;" +
					"			        if(progress_tag) {" +
					"			            var progress = progress_tag.value;" +
					"			        }" +
					"			        progress_lst.push(progress);" +
					"			    }" +
					"			    return progress_lst",
					"");


		} catch (Exception e) {
			System.err.println(e);
		}
		return progress;
	}

}