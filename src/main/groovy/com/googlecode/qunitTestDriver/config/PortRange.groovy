package com.googlecode.qunitTestDriver.config

import com.googlecode.qunitTestDriver.QUnitJettyTestRunner;


class PortRange implements Configuration{
	Integer[] ports;
	public PortRange(Integer... ports){
		this.ports = ports;
	}
	void configure(QUnitJettyTestRunner runner){
		runner.setPortRange(ports)
	}

}
