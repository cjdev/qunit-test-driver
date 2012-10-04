package com.googlecode.qunitTestDriver;

import groovy.lang.GroovyObjectSupport;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.runner.RunWith;

import com.gargoylesoftware.htmlunit.html.DomNode;
import com.googlecode.qunitTestDriver.config.Configuration;
import com.googlecode.qunitTestDriver.suite.*;

@RunWith(QUnitSuite.class)
public abstract class QUnitTest extends GroovyObjectSupport {

	public List<QUnitTestResult> getQUnitTestResults() throws Exception {
		QUnitTestDriver runner = null;
		
		try {
			runner = new QUnitTestDriver(getTestPageUrl(), getConfigurationsNullSafe());
			QUnitTestPage page = runner.getTestPage();
			page.waitForQunitTests();
			
			Map<String,List<QUnitTestCaseResult>> caseResultMap = new HashMap<String,List<QUnitTestCaseResult>>();

			List<DomNode> testNodes = page.getDriver().findElementsByXPath("//ol[@id='qunit-tests']//li[contains(@id,'test-output')]");
			if(testNodes.isEmpty())
				throw new AssertionError("Could not find test output!");

			for(DomNode testNode : testNodes) {
				DomNode module = testNode.getFirstByXPath(".//*[@class='module-name']");
				String moduleName = module != null ? module.asText() : "<unnamed module>";

				DomNode test = testNode.getFirstByXPath(".//*[@class='test-name']");
				String testName = test.asText();

				Boolean testPassed = "pass".equals(testNode.getAttributes().getNamedItem("class").getNodeValue());
				Boolean testFailed = "fail".equals(testNode.getAttributes().getNamedItem("class").getNodeValue());
				Integer assertionsPassed = Integer.valueOf(((DomNode)testNode.getFirstByXPath(".//*[@class='counts']//*[@class='passed']")).asText());
				Integer assertionsFailed = Integer.valueOf(((DomNode)testNode.getFirstByXPath(".//*[@class='counts']//*[@class='failed']")).asText());
				Integer assertionsTotal = assertionsPassed + assertionsFailed;

				String expectedAssertions = "(" + assertionsFailed + ", " + assertionsPassed + ", " + assertionsTotal + ")";
				String actualAssertions = ((DomNode)testNode.getFirstByXPath(".//*[@class='counts']")).asText(); 
				if (!expectedAssertions.equals(actualAssertions))
					throw new AssertionError("Test Assertion counts not found!" + "(" + assertionsPassed + ", " + assertionsPassed + ", " + assertionsTotal + ")" + ((DomNode)testNode.getFirstByXPath(".//*[@class='counts']")).asText());
				if (testPassed == testFailed)
					throw new AssertionError("Could not determine if test passed or failed");

				String error = testPassed ? null : "Passed: " + assertionsPassed + ", Failed: " + assertionsFailed + ", Total: " + assertionsTotal;

				if (!caseResultMap.containsKey(moduleName))
					caseResultMap.put(moduleName, new ArrayList<QUnitTestCaseResult>());

				caseResultMap.get(moduleName).add(new QUnitTestCaseResult(getClass(), testName, error));
			}

			List<QUnitTestResult> results = new ArrayList<QUnitTestResult>();
			for(Map.Entry<String,List<QUnitTestCaseResult>> entry : caseResultMap.entrySet()) {
				results.add(new QUnitTestResult(entry.getKey(), entry.getValue()));
			}

			return results;
		} finally {
			if (runner != null && runner.getServer() != null)
				runner.getServer().stop();
		}
	}
	
	private List<Configuration> getConfigurationsNullSafe() {
		return getConfigurations() != null ? getConfigurations() : Arrays.<Configuration>asList();
	}

	/**
	 * Override this method to return where your QUnit html test page is (e.g. "src/test/resources/QUnitTestPageTest.html")	
	 * @return a string that is the relative location of your QUnit html test
	 */
	public abstract String getTestPageUrl();

	/**
	 * Optionally, override this method to return any configurations that should be passed through to QUnitTestDriver.  If you want
	 * to use the defaults, simply returning null will suffice. 
	 * @return an array of Configuration objects that will be passed through to the QUnitTestDriver
	 */
	public abstract List<Configuration> getConfigurations();
}
