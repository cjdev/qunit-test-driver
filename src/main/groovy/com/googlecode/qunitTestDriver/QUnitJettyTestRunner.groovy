package com.googlecode.qunitTestDriver

import com.googlecode.qunitTestDriver.config.Configuration

class QUnitJettyTestRunner {
    private final JettyServer server
    private static String DEFAULT_SERVER_ROOT = "/"
    private final String testRelativePath
	private Integer[] portRange = [8098, 8198, 8298, 8398, 8498, 8598, 8698, 8798]

    public QUnitJettyTestRunner(String testRelativePath, Configuration... configs) {
		for(Configuration config: configs){
			config.configure(this)
		}
        this.server = new JettyServer("./", portRange)
        this.testRelativePath = testRelativePath
    }


    public static void run(String testRelativePath) {
        QUnitJettyTestRunner runner = new QUnitJettyTestRunner(testRelativePath)
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

    public JettyServer getServer() {
        return server
    }
	
	protected setPortRange(Integer[] range){
		portRange = range
	}

}
