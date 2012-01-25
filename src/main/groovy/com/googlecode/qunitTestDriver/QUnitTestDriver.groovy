package com.googlecode.qunitTestDriver

import com.googlecode.qunitTestDriver.config.Configuration

class QUnitTestDriver {
    final JettyServer server
    String serverRoot = "/"
    final String testRelativePath
	Integer[] portRange = [8098, 8198, 8298, 8398, 8498, 8598, 8698, 8798]

    public QUnitTestDriver(String testRelativePath, Configuration... configs) {
		for(Configuration config: configs){
			config.configure(this)
		}
        this.server = new JettyServer("./", portRange)
        this.testRelativePath = testRelativePath
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
