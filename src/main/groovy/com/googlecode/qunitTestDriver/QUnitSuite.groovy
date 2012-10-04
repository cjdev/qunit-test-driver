package com.googlecode.qunitTestDriver

import org.junit.internal.runners.model.EachTestNotifier;
import org.junit.runner.Description
import org.junit.runner.Runner
import org.junit.runner.notification.Failure
import org.junit.runner.notification.RunNotifier
import org.junit.runners.model.FrameworkMethod
import org.junit.runners.model.TestClass

import com.googlecode.qunitTestDriver.annotations.QUnitResults
import com.googlecode.qunitTestDriver.annotations.QUnitTestCaseResult
import com.googlecode.qunitTestDriver.annotations.QUnitTestResult


class QUnitSuite extends Runner {
	private TestClass testClass
	private Description description
	private List<QUnitTestResult> results

	private static List<QUnitTestResult> getResultsFrom(TestClass testClass, FrameworkMethod method) {
		return method.invokeExplosively(testClass.onlyConstructor.newInstance(), null)
	}
	
	private static FrameworkMethod getResultsMethod(TestClass testClass) {
		List<FrameworkMethod> methods = testClass.getAnnotatedMethods(QUnitResults.class)
		
		if (methods.size != 1)
			throw new Exception("There must be exactly 1 results method on class " + testClass.getName());
			
		return methods.get(0)
	}
	
	public QUnitSuite(Class<?> klass) {
		this.testClass = new TestClass(klass)
		FrameworkMethod method = getResultsMethod(testClass)
		this.description = Description.createSuiteDescription(klass)
		this.results = getResultsFrom(testClass, method)
		
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
//		EachTestNotifier testNotifier= new EachTestNotifier(notifier, getDescription());
		
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
