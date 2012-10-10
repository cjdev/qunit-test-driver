package com.cj.qunitTestDriver.config

import com.cj.qunitTestDriver.QUnitTestDriver;

/**
 * This class allows you to set which ports are used in the internal Jetty server.
 * The ports are tried one-by-one until one of them works.
 * This is nice for an environment where tests may be running in parallel on single machine.
 */
class PortSet implements Configuration{
	List<Integer> ports;
	
	public PortSet(Integer... ports){
		this.ports = ports;
	}
	
	void configure(QUnitTestDriver runner){
		runner.setPortSet(ports)
	}

}
