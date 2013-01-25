package com.cj.qunitTestDriver

import java.util.Map.Entry

import org.eclipse.jetty.server.Server
import org.eclipse.jetty.server.handler.ContextHandler
import org.eclipse.jetty.server.handler.DefaultHandler
import org.eclipse.jetty.server.handler.HandlerList
import org.eclipse.jetty.server.handler.ResourceHandler


public class JettyServer {
    public static final Integer DEFAULT_PORT = 9098
    public static final String DEFAULT_SERVER_ROOT = "./"

    private Server server;
    private Integer finalPort
	private final Map<String,List<String>> pathMappings
    private final List<Integer> ports
    private final List<ResourceHandler> resourceHandlers

    public JettyServer(Map<String,List<String>> pathMappings, List<Integer> ports) throws Exception {
    	this.ports = ports
        this.resourceHandlers = []
		this.pathMappings = pathMappings
		
		for(Entry<String, List<String>> entry : pathMappings.entrySet()) {
			for(String directory : entry.getValue()) {
				ContextHandler contextHandler = new ContextHandler()
				contextHandler.contextPath = entry.key
				
				ResourceHandler resourceHandler = new ResourceHandler()
				resourceHandler.directoriesListed = true
				resourceHandler.resourceBase = directory
				
				contextHandler.handler = resourceHandler
				
				this.resourceHandlers.add(contextHandler)
			}
		}
    }

    public JettyServer start() {
        for (Integer port: ports) {
            try {
                println("Starting Jetty server for QUnit tests on port " + port + " with root(s) " + pathMappings.values().join(":"))

                HandlerList handlers = new HandlerList()
				
				for(ResourceHandler resourceHandler : resourceHandlers)
                	handlers.addHandler(resourceHandler)

            	handlers.addHandler(new DefaultHandler())
				
                finalPort = port
                server = new Server(port)

                server.setHandler(handlers)

                server.start();
                break;
            } catch (Exception e) {
				Integer millisToWait = new Random().nextInt(500)
                println("That port didn't work; waiting "+millisToWait)
				Thread.sleep(millisToWait)
            }
        }

        if (server == null || !server.isStarted()) {
            throw new RuntimeException("No Available Ports To Start Jetty")
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
	
    public static void main(String[] args) throws Exception {
        try {
            createForMain(args).start().join()

        } catch (Exception e) {
            println("\n\n\nUsage: " + args[0] + " port,relativePath\n\n\n")
        }
    }

    static JettyServer createForMain(String[] commandLineArgs) {
        if (!commandLineArgs) {
            return new JettyServer(["/" : [DEFAULT_SERVER_ROOT]], [DEFAULT_PORT])
        }

        List<Integer> port = [Integer.parseInt(commandLineArgs[0])]
        Map<String, List<String>> directoryMap = ["/" : [DEFAULT_SERVER_ROOT]]
		
        if (commandLineArgs.length >= 2) {
			for(int i=1; i<commandLineArgs.length; i++) {
				String[] pathMapping = commandLineArgs[i].split("=")
				List<String> directories = directoryMap.get(pathMapping[0])
				
				if(directories == null) {
					directories = []
					directoryMap.put(pathMapping[0], directories)
				}
				
            	directories.add(pathMapping[1])
			}
        }

        return new JettyServer(directoryMap, port)
    }


}
