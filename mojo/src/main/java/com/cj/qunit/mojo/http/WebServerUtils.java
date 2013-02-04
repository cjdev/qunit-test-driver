package com.cj.qunit.mojo.http;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.httpobjects.HttpObject;
import org.httpobjects.jetty.HttpObjectsJettyHandler;
import org.httpobjects.util.ClasspathResourcesObject;
import org.httpobjects.util.FilesystemResourcesObject;
import org.mortbay.jetty.Server;

public class WebServerUtils {

    public static class JettyPlusPort {
        public final int port;
        public final Server server;
        private JettyPlusPort(int port, Server server) {
            super();
            this.port = port;
            this.server = server;
        }

    }

    public static JettyPlusPort launchHttpServer(final List<File> codePaths, final List<File> extraPathsToServe, final String webPathToRequireDotJsConfig ) {
        
        List<File> pathsToServe = new ArrayList<File>(codePaths);
        pathsToServe.addAll(extraPathsToServe);
        
        List<HttpObject> resources = new ArrayList<HttpObject>(Arrays.asList(new TestListingResource("/", codePaths)));
        
        for(File projectDirectory: pathsToServe){
            resources.add(new FilesystemResourcesObject("/{resource*}", projectDirectory));
        }
        resources.add(new ClasspathResourcesObject("/qunit-mojo/{resource*}", WebServerUtils.class, "/qunit-mojo"));
        resources.add(new AutoGeneratedQunitHtmlWrappers(webPathToRequireDotJsConfig));
        
        
        for(int port: new Integer[]{8098, 8198, 8298, 8398, 8498, 8598, 8695, 8796}){
            try{
                Server jetty = HttpObjectsJettyHandler.launchServer(port, resources.toArray(new HttpObject[]{}));
                return new JettyPlusPort(port, jetty);
            }catch(Exception e){
                System.out.println("Looks like port " + port + " didn't work for me.  I'm assuming it is in use?");
            }
        }

        throw new RuntimeException("Could not find a port to which to bind :(");
    }
}
