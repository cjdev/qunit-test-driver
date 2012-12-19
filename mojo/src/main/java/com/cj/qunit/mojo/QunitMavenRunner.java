package com.cj.qunit.mojo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


import com.cj.qunit.mojo.http.WebServerUtils;
import com.cj.qunitTestDriver.QUnitTestDriver;
import com.cj.qunitTestDriver.QUnitTestPage;
import com.gargoylesoftware.htmlunit.BrowserVersion;

public class QunitMavenRunner {
    
    public static interface Listener {
        void runningTest(String relativePath);
    }
    
    public List<String> run(final List<File> codePaths, final List<File> extraPathsToServe, final String requireDotJsShim, final Listener log, final int testTimeout) {
        final WebServerUtils.JettyPlusPort jetty = WebServerUtils.launchHttpServer(codePaths, extraPathsToServe, requireDotJsShim);

        try{

            final List<String> problems = new ArrayList<String>(); 
            for(File codePath : codePaths){
                for(QunitTestLocator.LocatedTest test: new QunitTestLocator().locateTests(codePath)){
                    final String name = test.name;
                    System.out.println("Running " + name);
                    log.runningTest(name);
                    
                    try {
                        QUnitTestPage page = new QUnitTestPage(jetty.port, test.relativePath, testTimeout, BrowserVersion.FIREFOX_3_6, true);
                        page.assertTestsPass();
                    } catch (Throwable m){
                        problems.add("Problems found in '" + name +"':\n"+m.getMessage());
                    }   
                }
            }
            
            return problems;
        }finally{
            try {
                jetty.server.stop();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    
}
