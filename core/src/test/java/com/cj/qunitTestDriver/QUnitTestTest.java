package com.cj.qunitTestDriver;

import java.util.List;

import com.cj.qunitTestDriver.QUnitTest;
import com.cj.qunitTestDriver.config.Configuration;

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
