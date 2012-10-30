package sample.plugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import sample.plugin.http.WebServerUtils;

import com.cj.qunitTestDriver.QUnitTestPage;
import com.gargoylesoftware.htmlunit.BrowserVersion;

public class QunitMavenRunner {
    
    public static interface Listener {
        void runningTest(String relativePath);
    }
    
    public List<String> run(final File projectDirectory, final Listener log) {
        final WebServerUtils.JettyPlusPort jetty = WebServerUtils.launchHttpServer(projectDirectory);

        try{

            final List<String> problems = new ArrayList<String>(); 
            for(QunitTestLocator.LocatedTest test: new QunitTestLocator().locateTests(projectDirectory)){
                final String name = test.name;
                System.out.println("Running " + name);
                log.runningTest(name);
                
                try {
                    QUnitTestPage page = new QUnitTestPage(jetty.port, test.relativePath, 5000, BrowserVersion.FIREFOX_3_6, true);
                    page.assertTestsPass();
                } catch (Throwable m){
                    problems.add("Problems found in '" + name +"':\n"+m.getMessage());
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
