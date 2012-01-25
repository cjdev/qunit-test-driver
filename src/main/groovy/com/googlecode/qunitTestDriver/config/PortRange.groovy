package com.googlecode.qunitTestDriver.config

import com.googlecode.qunitTestDriver.QUnitTestDriver;

/**
 * This class allows you to set which ports are used in the internal Jetty server.
 * The ports are tried one-by-one until one of them works.
 * This is nice for an environment where tests may be running in parallel on single machine.
 */
class PortRange implements Configuration{
	Integer[] ports;
	public PortRange(Integer... ports){
		this.ports = ports;
	}
	void configure(QUnitTestDriver runner){
		runner.setPortRange(ports)
	}

}
