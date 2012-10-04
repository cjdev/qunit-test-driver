package com.googlecode.qunitTestDriver.suite

import org.junit.runner.Description;

public class QUnitTestCaseResult {
	String name
	String error
	Description desc

	QUnitTestCaseResult(Class<?> testClass, String name, String error) {
		this.name = name
		this.error = error
		this.desc = Description.createTestDescription(testClass, name)
	}
}