package com.googlecode.qunitTestDriver.suite

import org.junit.internal.runners.model.EachTestNotifier;
import org.junit.runner.Description
import org.junit.runner.Runner
import org.junit.runner.notification.Failure
import org.junit.runner.notification.RunNotifier
import org.junit.runners.model.FrameworkMethod
import org.junit.runners.model.TestClass



class QUnitSuite extends Runner {
	private TestClass testClass
	private Description description
	private List<QUnitTestResult> results

	private static List<QUnitTestResult> getResultsFrom(TestClass testClass) {
		return testClass.onlyConstructor.newInstance().getQUnitTestResults()
	}
	
	public QUnitSuite(Class<?> klass) {
		this.testClass = new TestClass(klass)
		this.description = Description.createSuiteDescription(klass)
		this.results = getResultsFrom(testClass)
		
		for(QUnitTestResult result : results) {
			description.addChild(result.desc)
		}
	}

	@Override
	public Description getDescription() {
		description
	}

	@Override
	public void run(RunNotifier notifier) {
		notifier.fireTestStarted(description)

		for(QUnitTestResult result : results) {
			notifier.fireTestStarted(result.desc)

			for(QUnitTestCaseResult c : result.cases) {
				notifier.fireTestStarted(c.desc)
				
				if(c.error != null)
					notifier.fireTestFailure(new Failure(c.desc, new AssertionError(c.error)))
				
				notifier.fireTestFinished(c.desc)
			}

			notifier.fireTestFinished(result.desc)
		}

		notifier.fireTestFinished(description)
	}
}
