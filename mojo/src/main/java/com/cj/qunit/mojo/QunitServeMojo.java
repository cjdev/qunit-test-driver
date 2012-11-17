package com.cj.qunit.mojo;

import org.apache.maven.plugin.MojoFailureException;

import com.cj.qunit.mojo.http.WebServerUtils;
import com.cj.qunit.mojo.http.WebServerUtils.JettyPlusPort;


/**
 * @phase test
 * @goal serve
 */
public class QunitServeMojo extends AbstractQunitMojo {
    
    public void execute() throws MojoFailureException {
        JettyPlusPort jetty = WebServerUtils.launchHttpServer(codePaths(), extraPathsToServe(), super.requireDotJsShim());
        
        getLog().info("Server started: visit http://localhost:" + jetty.port + " to run your tests.");
        Object o = new Object();
        try {
            synchronized(o){
                o.wait();
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}

