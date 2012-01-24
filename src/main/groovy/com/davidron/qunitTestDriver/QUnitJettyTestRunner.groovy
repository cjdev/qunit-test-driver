package com.davidron.qunitTestDriver

class QUnitJettyTestRunner {
    private final JettyServer server
    private static String DEFAULT_SERVER_ROOT = "/"
    private final String testRelativePath

    public QUnitJettyTestRunner(Class testClass, String testRelativePath) {
        this.server = new JettyServer(getPort(), parseServerRoot(testClass))
        this.testRelativePath = testRelativePath
    }

    private String parseServerRoot(Class testClass) {
        String classPath = testClass.protectionDomain.codeSource.location.path
        int indexOfTarget = classPath.indexOf("/target/")
        String serverRoot = classPath.substring(0, indexOfTarget)

        return serverRoot + "/"
    }

    public static void run(Class testClass, String testRelativePath) {
        QUnitJettyTestRunner runner = new QUnitJettyTestRunner(testClass, testRelativePath)
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
