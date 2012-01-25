package com.googlecode.qunitTestDriver.config

import com.googlecode.qunitTestDriver.QUnitTestDriver;

/**
 * If passed in, will cause the test runner to STOP executing just after starting the jetty server.
 * This is useful for debugging 404 errors in your tests.
 */
class JoinToServer implements Configuration{
	void configure(QUnitTestDriver runner){
		runner.joinToServer(true)
	}

}
