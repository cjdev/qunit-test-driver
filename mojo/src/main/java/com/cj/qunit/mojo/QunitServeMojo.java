package com.cj.qunit.mojo;

import java.io.File;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoFailureException;

import com.cj.qunit.mojo.http.WebServerUtils;
import com.cj.qunit.mojo.http.WebServerUtils.JettyPlusPort;


/**
 * @phase test
 * @goal serve
 */
public class QunitServeMojo extends AbstractMojo {
    
    /**
     * @parameter default-value="${basedir}
     * @readonly
     * @required
     */
    private File basedir;
    
    public void execute() throws MojoFailureException {
        JettyPlusPort jetty = WebServerUtils.launchHttpServer(basedir);
        
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

