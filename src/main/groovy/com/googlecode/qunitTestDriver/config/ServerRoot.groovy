package com.googlecode.qunitTestDriver.config

import com.googlecode.qunitTestDriver.QUnitTestDriver;

/**
 * Specifies the root of the server that Jetty will be given.
 */
class ServerRoot implements Configuration{
	String root;
	public ServerRoot(String root){
		this.root = root;
	}
	void configure(QUnitTestDriver runner){
		runner.serverRoot(root)
	}

}
