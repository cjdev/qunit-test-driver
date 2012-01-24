package com.davidron.qunitTestDriver

import com.davidron.qunitTestDriver.JettyServer

class QUnitJettyTestRunner {
    private final JettyServer server
    private static String DEFAULT_SERVER_ROOT = "/"
    private final String testRelativePath
	static final Integer[] GOOD_PORT_RANGE = [9086];

    public QUnitJettyTestRunner(String testRelativePath) {
        this.server = new JettyServer("./", GOOD_PORT_RANGE)
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

}
