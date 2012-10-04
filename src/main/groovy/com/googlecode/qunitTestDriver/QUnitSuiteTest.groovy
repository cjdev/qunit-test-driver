package com.googlecode.qunitTestDriver

import static org.junit.Assert.assertEquals

import org.junit.After;
import org.junit.runner.RunWith

import com.gargoylesoftware.htmlunit.html.DomNode
import com.googlecode.qunitTestDriver.annotations.QUnitResults
import com.googlecode.qunitTestDriver.annotations.QUnitTestCaseResult
import com.googlecode.qunitTestDriver.annotations.QUnitTestResult
import com.googlecode.qunitTestDriver.config.Configuration


@RunWith(QUnitSuite.class)
public abstract class QUnitSuiteTest {

	@QUnitResults
	public List<QUnitTestResult> runQUnitTests() {
		QUnitTestDriver runner
		
		try {
			runner = new QUnitTestDriver(getTestPageUrl(), getConfigurations())
			QUnitTestPage page = runner.getTestPage()
			Map<String,List<QUnitTestCaseResult>> caseResultMap = [:]

			List<DomNode> testNodes = page.driver.findElementsByXPath("//ol[@id='qunit-tests']/li[contains(@id,'qunit-test-output')]")

			for(DomNode testNode : testNodes) {
				DomNode module = testNode.getFirstByXPath(".//*[@class='module-name']")
				String moduleName = module != null ? module.asText() : "<unnamed module>"

				DomNode test = testNode.getFirstByXPath(".//*[@class='test-name']")
				String testName = test.asText()

				Boolean testPassed = testNode.getAttributes().getNamedItem("class").getNodeValue() == "pass"
				Boolean testFailed = testNode.getAttributes().getNamedItem("class").getNodeValue() == "fail"
				Integer assertionsPassed = Integer.valueOf(testNode.getFirstByXPath(".//*[@class='counts']//*[@class='passed']").asText())
				Integer assertionsFailed = Integer.valueOf(testNode.getFirstByXPath(".//*[@class='counts']//*[@class='failed']").asText())
				Integer assertionsTotal = assertionsPassed + assertionsFailed

				assert "(${assertionsFailed}, ${assertionsPassed}, ${assertionsTotal})" == testNode.getFirstByXPath(".//*[@class='counts']").asText()
				assert testPassed != testFailed

				String error = testPassed ? null : "Passed: ${assertionsPassed}, Failed: ${assertionsFailed}, Total: ${assertionsTotal}"

				if (!caseResultMap.containsKey(moduleName))
					caseResultMap.put(moduleName, [])

				caseResultMap.get(moduleName).add(new QUnitTestCaseResult(getClass(), testName, error))
			}

			List<QUnitTestResult> results = []
			for(Map.Entry<String,List<QUnitTestCaseResult>> entry : caseResultMap.entrySet()) {
				results.add(new QUnitTestResult(entry.key, entry.value))
			}

			return results
		} finally {
			runner.server.stop()
		}
	}

	/**
	 * Override this method to return where your QUnit html test page is (e.g. "src/test/resources/QUnitTestPageTest.html")	
	 * @return a string that is the relative location of your QUnit html test
	 */
	public abstract String getTestPageUrl()

	/**
	 * Optionally, override this method to return any configurations that should be passed through to QUnitTestDriver.  If you want
	 * to use the defaults, simply returning null will suffice. 
	 * @return an array of Configuration objects that will be passed through to the QUnitTestDriver
	 */
	public abstract Configuration[] getConfigurations()
}