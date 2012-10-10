package com.cj.qunitTestDriver;

import groovy.lang.GroovyObjectSupport;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.runner.Description;
import org.junit.runner.RunWith;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;

import com.cj.qunitTestDriver.config.Configuration;
import com.cj.qunitTestDriver.suite.QUnitSuite;
import com.gargoylesoftware.htmlunit.html.DomNode;

@RunWith(QUnitSuite.class)
public abstract class QUnitTest extends GroovyObjectSupport {
	private TheTestSuite suite;
	
	public QUnitTest() {
		suite = new TheTestSuite();
	}
	
	void addToDescription(Description description) {
		suite.addToDescription(description);
	}
	
	void run(RunNotifier notifier) throws InterruptedException {
		suite.run(notifier);
	}
	
	class TheTestSuite {
		Map<String, Module> modules;
		QUnitTestDriver driver;
		QUnitTestPage page;
		Long startTime;
		
		TheTestSuite() {
			this.modules = new HashMap<String, Module>();
			this.driver = new QUnitTestDriver(getTestPageUrl(), getConfigurationsNullSafe());
			this.page = driver.getTestPageImmediate();
			this.startTime = System.currentTimeMillis();
			
			List<DomNode> testNodes = page.getDriver().findElementsByXPath("//ol[@id='qunit-tests']//li[contains(@id,'test-output')]");
			if(testNodes.isEmpty())
				throw new AssertionError("Could not find test output!");
			
			for(DomNode testNode : testNodes) {
				DomNode moduleDomNode = testNode.getFirstByXPath(".//*[@class='module-name']");
				String moduleName = moduleDomNode != null ? moduleDomNode.asText() : "<unnamed module>";
				
				if(!modules.containsKey(moduleName))
					modules.put(moduleName, new Module(moduleName, moduleDomNode));

				DomNode testNameDomNode = testNode.getFirstByXPath(".//*[@class='test-name']");
				String testName = testNameDomNode.asText();
				
				modules.get(moduleName).addTestCase(new TestCase(testName, testNode));
			}
		}
		
		void addToDescription(Description description) {
			for(Module m : modules.values())
				description.addChild(m.description);
		}
		
		void run(RunNotifier notifier) throws InterruptedException {
			for(Module module : modules.values()) {
				notifier.fireTestStarted(module.description);
				for(Description test : module.description.getChildren())
					notifier.fireTestStarted(test);
			}

			while(notifyTestsCompleted(notifier) && notTimedOutYet()) {
				Thread.sleep(200);
			}
		}

		private boolean notTimedOutYet() {
			return (System.currentTimeMillis() - startTime) < driver.getTimeout();
		}

		boolean notifyTestsCompleted(RunNotifier notifier) {
			for(Module module : modules.values())
				if(module.isRunning(notifier))
					return true;
			
			return false;
		}
	}
	
	class Module {
		String name;
		Description description;
		List<TestCase> testCases;
		DomNode domNode;
		
		Module(String moduleName, DomNode moduleDomNode) {
			this.name = moduleName;
			this.domNode = moduleDomNode;
			this.description = Description.createSuiteDescription(name);
			this.testCases = new ArrayList<TestCase>();
		}

		public boolean isRunning(RunNotifier notifier) {
			for(TestCase t : testCases)
				if(t.isRunning(notifier))
					return true;
			
			notifier.fireTestFinished(description);
			return false;
		}

		public void addTestCase(TestCase testCase) {
			testCases.add(testCase);
			description.addChild(testCase.description);
		}
	}
	
	class TestCase {
		String name;
		Description description;
		TestResult result;
		DomNode domNode;
		
		TestCase(String testName, DomNode testDomNode) {
			this.name = testName;
			this.domNode = testDomNode;
			this.description = Description.createTestDescription(getClass(), name);
			this.result = TestResult.RUNNING;
		}

		boolean isRunning(RunNotifier notifier) {
			Boolean testPassed = "pass".equals(domNode.getAttributes().getNamedItem("class").getNodeValue());
			Boolean testFailed = "fail".equals(domNode.getAttributes().getNamedItem("class").getNodeValue());
			Boolean testRunning = "running".equals(domNode.getAttributes().getNamedItem("class").getNodeValue());
			int statusesMatched = 0;
			
			if(testPassed) {
				statusesMatched++;
				if(result == TestResult.RUNNING) {
					notifier.fireTestFinished(description);
				}
				result = TestResult.PASSED;
			}
			
			if(testFailed) {
				statusesMatched++;
				if(result == TestResult.RUNNING) {
					Integer assertionsPassed = Integer.valueOf(((DomNode)domNode.getFirstByXPath(".//*[@class='counts']//*[@class='passed']")).asText());
					Integer assertionsFailed = Integer.valueOf(((DomNode)domNode.getFirstByXPath(".//*[@class='counts']//*[@class='failed']")).asText());
					Integer assertionsTotal = assertionsPassed + assertionsFailed;

					String expectedAssertions = "(" + assertionsFailed + ", " + assertionsPassed + ", " + assertionsTotal + ")";
					String actualAssertions = ((DomNode)domNode.getFirstByXPath(".//*[@class='counts']")).asText(); 
					if (!expectedAssertions.equals(actualAssertions))
						throw new AssertionError("Test Assertion counts not found!" + "(" + assertionsPassed + ", " + assertionsPassed + ", " + assertionsTotal + ")" + ((DomNode)domNode.getFirstByXPath(".//*[@class='counts']")).asText());
					if (testPassed == testFailed)
						throw new AssertionError("Could not determine if test passed or failed");

					String error = testPassed ? null : "Passed: " + assertionsPassed + ", Failed: " + assertionsFailed + ", Total: " + assertionsTotal;
					
					notifier.fireTestFailure(new Failure(description, new AssertionError(error)));
					notifier.fireTestFinished(description);
				}
				result = TestResult.FAILED;
			}
			
			if(testRunning) {
				statusesMatched++;
				result = TestResult.RUNNING;
			}
			
			if(statusesMatched != 1)
				throw new AssertionError("Could not determine if test passed or failed! passed:" + testPassed + "|failed:" + testFailed + "|running:" + testRunning);

			return testRunning;
		}
	}
	
	enum TestResult {
		PASSED,
		FAILED,
		RUNNING
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
