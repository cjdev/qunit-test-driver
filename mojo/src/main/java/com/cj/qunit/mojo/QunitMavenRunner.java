package com.cj.qunit.mojo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import com.cj.qunit.mojo.http.WebServerUtils;
import com.cj.qunitTestDriver.QUnitTestPage;
import com.gargoylesoftware.htmlunit.BrowserVersion;

public class QunitMavenRunner {
    
    public static interface Listener {
        void runningTest(String relativePath);
    }
    
    private static <T> List<T> concat(List<T> ... lists){
        final List<T> result = new ArrayList<T>();

        for(List<T> list : lists){
            result.addAll(list);
        }
        
        return result;
    }
    
    public List<String> run(final String webRoot, final List<File> codePaths, final List<File> extraPathsToServe, final String webPathToRequireDotJsConfig, final Listener log, final int testTimeout) {
        final String requireDotJsConfig;

        final String normalizedWebRoot = normalizedWebRoot(webRoot);
        
        if(webPathToRequireDotJsConfig!=null && webPathToRequireDotJsConfig.trim().equals("")){
            requireDotJsConfig = null;
        }else{
            requireDotJsConfig = webPathToRequireDotJsConfig;
        }
        
        
        validateJsConfigpath(normalizedWebRoot, codePaths, extraPathsToServe, requireDotJsConfig);
        
        final WebServerUtils.JettyPlusPort jetty = WebServerUtils.launchHttpServer(normalizedWebRoot, codePaths, extraPathsToServe, requireDotJsConfig);
        
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

    private String normalizedWebRoot(final String webRoot) {
        final String normalizedWebRoot = webRoot.endsWith("/") ? webRoot.substring(0, webRoot.length()-1) : webRoot;
        return normalizedWebRoot;
    }

    @SuppressWarnings("unchecked")
    private void validateJsConfigpath(final String webRoot, final List<File> codePaths,
            final List<File> extraPathsToServe,
            final String webPathToRequireDotJsConfig) {
        
        if(webPathToRequireDotJsConfig==null) return;
        
        boolean found = false;
        
        final String relativeFilesystemPathToRequireDotJsConfig = webPathToRequireDotJsConfig.replaceFirst(Pattern.quote(webRoot), "");
        
        List<File> placesLooked = new ArrayList<File>();
        for(File codeDir : concat(codePaths, extraPathsToServe)){
            final File config = new File(codeDir, relativeFilesystemPathToRequireDotJsConfig);
            placesLooked.add(config);
            if(config.exists()){
               found = true; 
            }
        }
        
        if(!found){
            
            StringBuffer text = new StringBuffer("You configured a require.js configuration path of \"" + webPathToRequireDotJsConfig + "\".  However, it doesn't seem to exist.  Here's where I looked for it:");
            for(File path : placesLooked){
                text.append("\n    " + path.getAbsolutePath() + "\n");
            }
            throw new RuntimeException(text.toString());
        }
    }

    
}
