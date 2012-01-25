package com.googlecode.qunitTestDriver

import org.eclipse.jetty.server.Server
import org.eclipse.jetty.server.handler.ResourceHandler
import org.eclipse.jetty.server.handler.HandlerList
import org.eclipse.jetty.server.handler.DefaultHandler

public class JettyServer {
    private final Server server=null;
    public static final Integer DEFAULT_PORT = 9098
    private static final String DEFAULT_SERVER_ROOT = "/"
	Integer finalPort

    public JettyServer(String serverRoot, Integer... ports) throws Exception {
		
		ResourceHandler resourceHandler = new ResourceHandler()
		resourceHandler.directoriesListed = true
		resourceHandler.resourceBase = serverRoot

				
		for(Integer port: ports){
			try{
				println("Starting Jetty server for QUnit tests on port " + port + " with root " + serverRoot)
				
				HandlerList handlers = new HandlerList()
				handlers.addHandler(resourceHandler)
				handlers.addHandler(new DefaultHandler())

				finalPort = port
				server = new Server(port)

				server.setHandler(handlers)
		
				server.start();
				break;
			}catch(Exception e){
				println("That port didn't work...")
			}
		}
		
		if (server==null || !server.isStarted()){
			throw new RuntimeException("No Available Ports To Start Jetty");
		}
    }

    public void stop() throws Exception {
        server.stop()
    }

    public void join() throws InterruptedException {
        server.join()
    }
	
	public Integer getPort(){
		return finalPort
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
