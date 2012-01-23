package cj.qunit

import org.eclipse.jetty.server.Server
import org.eclipse.jetty.server.handler.ResourceHandler
import org.eclipse.jetty.server.handler.HandlerList
import org.eclipse.jetty.server.handler.DefaultHandler

public class JettyServer {
    private final Server server
    public static final Integer DEFAULT_PORT = 9098
    private static final String DEFAULT_SERVER_ROOT = "/"

    public JettyServer(final int port, final String serverRoot) throws Exception {
        server = new Server(port)

        ResourceHandler resourceHandler = new ResourceHandler()
        resourceHandler.directoriesListed = true
        resourceHandler.resourceBase = serverRoot

        HandlerList handlers = new HandlerList()
        handlers.addHandler(resourceHandler)
        handlers.addHandler(new DefaultHandler())
        server.handler = handlers

        println("Starting Jetty server for QUnit tests on port " + port + " with root " + serverRoot)
        server.start();
    }

    public void stop() throws Exception {
        server.stop()
    }

    private void join() throws InterruptedException {
        server.join()
    }

    public static void main(String[] arrrg) throws Exception {
        String[] args = arrrg[0].split(",")

        if (args.length == 0) {
            new JettyServer(DEFAULT_PORT, DEFAULT_SERVER_ROOT).join()
            return
        }

        try {
            final Integer port = Integer.parseInt(args[0])
            String serverRoot = DEFAULT_SERVER_ROOT
            if (args.length == 2) {
                 serverRoot = args[1]
            }

            new JettyServer(port, serverRoot).join()

        } catch (Exception e) {
            println("\n\n\nUsage: " + args[0] + " port,relativePath\n\n\n")
        }
    }
}
