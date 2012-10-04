package com.googlecode.qunitTestDriver.annotations

import org.junit.runner.Description

public class QUnitTestResult {
	String name
	List<QUnitTestCaseResult> cases
	Description desc

	QUnitTestResult(String name, QUnitTestCaseResult... cases) {
		this(name, Arrays.asList(cases))
	}
	
	QUnitTestResult(String name, List<QUnitTestCaseResult> cases) {
		this.name = name
		this.cases = cases
		this.desc = Description.createSuiteDescription(name)
		
		for(QUnitTestCaseResult c : cases)
			this.desc.addChild(c.desc)
	}
}