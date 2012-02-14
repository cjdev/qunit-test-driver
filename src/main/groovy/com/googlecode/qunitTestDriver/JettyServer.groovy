package com.googlecode.qunitTestDriver

import org.eclipse.jetty.server.Server
import org.eclipse.jetty.server.handler.ResourceHandler
import org.eclipse.jetty.server.handler.HandlerList
import org.eclipse.jetty.server.handler.DefaultHandler

public class JettyServer {

    public static final Integer DEFAULT_PORT = 9098
    public static final String DEFAULT_SERVER_ROOT = "./"

    private Server server;
    private Integer finalPort
    private final Integer[] ports
    private final ResourceHandler resourceHandler
    private final String serverRoot

    public JettyServer(String serverRoot, Integer... ports) throws Exception {
        this.serverRoot = serverRoot
        resourceHandler = new ResourceHandler()
        resourceHandler.directoriesListed = true
        resourceHandler.resourceBase = serverRoot
        this.ports = ports
    }

    public JettyServer start() {
        for (Integer port: ports) {
            try {
                println("Starting Jetty server for QUnit tests on port " + port + " with root " + serverRoot)

                HandlerList handlers = new HandlerList()
                handlers.addHandler(resourceHandler)
                handlers.addHandler(new DefaultHandler())

                finalPort = port
                server = new Server(port)

                server.setHandler(handlers)

                server.start();
                break;
            } catch (Exception e) {
                println("That port didn't work...")
            }
        }

        if (server == null || !server.isStarted()) {
            throw new RuntimeException("No Available Ports To Start Jetty");
        }
        return this
    }

    public void stop() throws Exception {
        server.stop()
    }

    public void join() throws InterruptedException {
        println("Joining to server.  You have to kill me to stop me now!");
        server.join()
    }

    public Integer getPort() {
        return finalPort
    }

    String getRoot() {
        return serverRoot
    }

    public static void main(String[] args) throws Exception {

        try {
            createForMain(args).start().join()

        } catch (Exception e) {
            println("\n\n\nUsage: " + args[0] + " port,relativePath\n\n\n")
        }
    }

    static JettyServer createForMain(String[] commandLineArgs) {
        if (!commandLineArgs) {
            return new JettyServer(DEFAULT_SERVER_ROOT, DEFAULT_PORT)
        }

        def port = Integer.parseInt(commandLineArgs[0])
        def serverRoot = DEFAULT_SERVER_ROOT
        if (commandLineArgs.size() == 2) {
            serverRoot = commandLineArgs[1]
        }

        return new JettyServer(serverRoot, port)
    }


}
