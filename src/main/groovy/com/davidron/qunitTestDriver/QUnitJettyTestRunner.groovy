package com.davidron.qunitTestDriver

class QUnitJettyTestRunner {
    private final JettyServer server
    private static String DEFAULT_SERVER_ROOT = "/"
    private final String testRelativePath

    public QUnitJettyTestRunner(String testRelativePath) {
        this.server = new JettyServer("./", getPort())
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
        return new QUnitTestPage(getPort(), testRelativePath)
    }

    public void stopServer() {
        server.stop()
    }

    private static int getPort() {
		PortUtil.getPort(System.getProperties(), JettyServer.DEFAULT_PORT);
    }

    public JettyServer getServer() {
        return server
    }

}
