package com.cj.qunit.mojo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import com.cj.qunit.mojo.http.WebServerUtils;
import com.cj.qunitTestDriver.QUnitTestPage;
import com.gargoylesoftware.htmlunit.BrowserVersion;

public class QunitMavenRunner {
    public enum Runner{
        HTML_UNIT{

            String runTest(
                    final WebServerUtils.JettyPlusPort jetty,
                    final QunitTestLocator.LocatedTest test,
                    final String name,  int testTimeout) {

                QUnitTestPage page = new QUnitTestPage(jetty.port, test.relativePath, testTimeout, BrowserVersion.FIREFOX_17, true);
                page.assertTestsPass();
                return null;
            }
        }, 
        PHANTOMJS{
            String runTest(
                    final WebServerUtils.JettyPlusPort jetty,
                    final QunitTestLocator.LocatedTest test,
                    final String name,  int testTimeout) {
                
                
                try {
                    File f = File.createTempFile("phantomjs-run-qunit", ".js");
                    f.deleteOnExit();
                    FileUtils.write(f, IOUtils.toString(getClass().getResourceAsStream("/qunit-mojo/phantomjs-run-qunit.js")));
                    
                    
                    String problem;
                    String baseUrl = "http://localhost:" + jetty.port;
                    String url = baseUrl + "/" + test.relativePath;
                    System.out.println("Executing " + url);
                    Process phantomjs = Runtime.getRuntime().exec(new String[]{"phantomjs", f.getAbsolutePath(), url});
                    String stdErr = IOUtils.toString(phantomjs.getErrorStream());
                    String stdOut = IOUtils.toString(phantomjs.getInputStream());
                    System.out.print(stdOut);
                    System.err.print(stdErr);
                    final int exitCode = phantomjs.waitFor();

                    if(exitCode!=0){
                        problem = "Problems found in '" + name +"':\n" + stdOut + "\nSTDERR:\n" + stdErr;
                    }else{
                        problem = null;
                    }
                    return problem;
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        };

        abstract String runTest(
                final WebServerUtils.JettyPlusPort jetty,
                final QunitTestLocator.LocatedTest test,
                final String name,  int testTimeout);
        }
    
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
    
    final int numThreads;
    final Runner runner;
    
    public QunitMavenRunner() {
        this(1, Runner.HTML_UNIT);
    }
    
    public QunitMavenRunner(int numThreads, Runner runner) {
        super();
        this.numThreads = numThreads;
        this.runner = runner;
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

            final List<QunitTestLocator.LocatedTest> allTests = new ArrayList<QunitTestLocator.LocatedTest>();

            for(File codePath : codePaths){
                allTests.addAll(new QunitTestLocator().locateTests(codePath));
            };

            final List<QunitTestLocator.LocatedTest> testsRemaining = new ArrayList<QunitTestLocator.LocatedTest>(allTests);

            System.out.println("Running with " + numThreads + " threads");
            
            runInParallel(numThreads, new Runnable(){
                public void run() {
                    while(true){
                        final QunitTestLocator.LocatedTest test;
                        synchronized(testsRemaining){
                            if(testsRemaining.size()>0){
                                test = testsRemaining.get(0);
                                testsRemaining.remove(0);
                            }else{
                                test = null;
                            }
                        }

                        if(test==null){
                            break;
                        }

                        final String name = test.name;
                        System.out.println("Running " + name);
                        log.runningTest(name);

                        String problem = null;
                        try {
                            problem = runner.runTest(jetty, test, name, testTimeout);
                        } catch (Throwable m){
                            problem = "Problems found in '" + name +"':\n"+m.getMessage();
                        }   

                        if(problem!=null){
                            synchronized(problems){
                                problems.add(problem);
                            }
                        }
                    }
                }
                
            });
            
            return problems;
        }finally{
            try {
                jetty.server.stop();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
    
    
    private void runInParallel(int numThreads, final Runnable runnable) {

        List<Thread> threads = new ArrayList<Thread>();

        for(int x=0;x<numThreads;x++){

            Thread t = new Thread(){
                @Override
                public void run() {
                    runnable.run();
                }

            };

            threads.add(t);
            t.start();

        }
        
        for(Thread t : threads){
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
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
