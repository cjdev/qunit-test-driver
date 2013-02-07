package com.cj.qunitTestDriver.junit

import java.util.List;

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
	def testInstance

	public QUnitSuite(Class<?> klass) {
		this.testClass = new TestClass(klass)
		this.description = Description.createSuiteDescription(klass)
		
		if(testClass.getJavaClass().getConstructors().length != 1 || testClass.getOnlyConstructor().getParameterTypes().length != 0)
			throw new Exception("Test class should have exactly one public zero-argument constructor")
	}

	@Override
	public Description getDescription() {
		if (testInstance == null) {
			this.testInstance = testClass.onlyConstructor.newInstance()
			this.testInstance.addToDescription(description)
		}
		
		description
	}

	@Override
	public void run(RunNotifier notifier) {
		notifier.fireTestStarted(getDescription())
		
		testInstance.run(notifier)
		
		notifier.fireTestFinished(getDescription())
	}
}
