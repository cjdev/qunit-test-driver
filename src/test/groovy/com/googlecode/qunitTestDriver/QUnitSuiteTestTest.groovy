package com.googlecode.qunitTestDriver

import com.googlecode.qunitTestDriver.config.Configuration

class QUnitSuiteTestTest extends QUnitSuiteTest {

	@Override
	public String getTestPageUrl() {
		return "src/test/resources/QUnitTestPageTest.html"
	}

	@Override
	public Configuration[] getConfigurations() {
		return null
	}
}
