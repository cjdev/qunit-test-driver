package com.googlecode.qunitTestDriver.config

import com.googlecode.qunitTestDriver.QUnitTestDriver;


class ServerRoot implements Configuration{
	String root;
	public ServerRoot(String root){
		this.root = root;
	}
	void configure(QUnitTestDriver runner){
		runner.serverRoot(root)
	}

}
