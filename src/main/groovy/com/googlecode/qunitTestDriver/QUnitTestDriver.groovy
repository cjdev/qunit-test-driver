package com.googlecode.qunitTestDriver

import com.gargoylesoftware.htmlunit.BrowserVersion
import com.googlecode.qunitTestDriver.config.Configuration


class QUnitTestDriver {
    final JettyServer server
    final String testPath
    Integer timeout = QUnitTestPage.DEFAULT_TIMEOUT
	Map<String,List<String>> pathMappings = ["/":["./"]]
    Boolean joinToServer=false
	List<Integer> portSet = [8098, 8198, 8298, 8398, 8498, 8598, 8695, 8796] //Psuedorandom Assortment Of Ports
	BrowserVersion browserVersion = BrowserVersion.FIREFOX_3_6

    public QUnitTestDriver(String testRelativePath, Configuration... configs) {
		this.testPath = testRelativePath

		for(Configuration config: configs)
			config.configure(this)

        server = new JettyServer(pathMappings, portSet).start()

		if (joinToServer)
			server.join()
    }
	
	public QUnitTestDriver(String testRelativePath, List<Configuration> configs) {
		this(testRelativePath, configs.toArray())
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
        return new QUnitTestPage(server.getPort(), testPath, timeout, browserVersion)
    }

    public void stopServer() {
        server.stop()
    }
}
