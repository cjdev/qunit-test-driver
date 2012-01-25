package com.googlecode.qunitTestDriver

import com.googlecode.qunitTestDriver.config.Configuration

class QUnitTestDriver {
    final JettyServer server
    final String testRelativePath
	String serverRoot = "/"
    Boolean joinToServer
	Integer[] portRange = [8098, 8198, 8298, 8398, 8498, 8598, 8695, 8796] //Psuedorandom Assortment Of Ports

    public QUnitTestDriver(String testRelativePath, Configuration... configs) {
		for(Configuration config: configs){
			config.configure(this)
		}
        server = new JettyServer("./", portRange)
		if (joinToServer) server.join()
        testRelativePath = testRelativePath
    }

    public static void run(String testRelativePath, Configuration... configs) {
        QUnitTestDriver runner = new QUnitTestDriver(testRelativePath, configs)
        try {
            runner.getTestPage().assertTestsPass()
        } finally {
            //when tests fail, still stop the server.
            runner.stopServer()
        }
    }
    
    protected QUnitTestPage getTestPage() {
        return new QUnitTestPage(server.getPort(), testRelativePath)
    }

    public void stopServer() {
        server.stop()
    }

}
