package com.googlecode.qunitTestDriver;

import java.util.List;

import com.googlecode.qunitTestDriver.config.Configuration;

public class QUnitTestTest extends QUnitTest {

	@Override
	public String getTestPageUrl() {
		return "src/test/resources/QUnitTestPageTest.html";
	}

	@Override
	public List<Configuration> getConfigurations() {
		return null;
	}
}
