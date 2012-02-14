package com.googlecode.qunitTestDriver

import com.googlecode.qunitTestDriver.config.Configuration

class QUnitTestDriver {
    final JettyServer server
    final String testPath
    Integer timeout = QUnitTestPage.DEFAULT_TIMEOUT
	String serverRoot = "./"
    Boolean joinToServer=false
	Integer[] portSet = [8098, 8198, 8298, 8398, 8498, 8598, 8695, 8796] //Psuedorandom Assortment Of Ports

    public QUnitTestDriver(String testRelativePath, Configuration... configs) {
		testPath = testRelativePath

		for(Configuration config: configs){
			config.configure(this)
		}

        server = new JettyServer(serverRoot, portSet).start()

		if (joinToServer) server.join()
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
        return new QUnitTestPage(server.getPort(), testPath, timeout)
    }

    public void stopServer() {
        server.stop()
    }

}
