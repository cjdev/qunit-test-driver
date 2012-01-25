package com.googlecode.qunitTestDriver.config

import com.googlecode.qunitTestDriver.QUnitTestDriver;


class PortRange implements Configuration{
	Integer[] ports;
	public PortRange(Integer... ports){
		this.ports = ports;
	}
	void configure(QUnitTestDriver runner){
		runner.setPortRange(ports)
	}

}
