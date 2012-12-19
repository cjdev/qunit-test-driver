package com.cj.qunit.mojo;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoFailureException;

/**
 * @phase test
 * @goal test
 */
public class QunitMavenRunnerMojo extends AbstractQunitMojo {
    
    public void execute() throws MojoFailureException {
        if(shouldSkipTests()) return;
        
        final List<String> filesRun = new ArrayList<String>();
        final List<String> problems = new QunitMavenRunner().run(codePaths(), extraPathsToServe(), requireDotJsShim(), new QunitMavenRunner.Listener() {
            @Override
            public void runningTest(String relativePath) {
                getLog().info("Running " + relativePath);
                filesRun.add(relativePath);
            }
        }, returnTimeout());
        
        if(!problems.isEmpty()){
            StringBuffer problemsString = new StringBuffer();
            
            for(String next : problems){
                problemsString.append(next);
                problemsString.append('\n');
            }

            throw new MojoFailureException(problemsString.toString());
        }else{
            getLog().info("Ran qunit on " + filesRun.size() + " files");
        }
    }

    private boolean shouldSkipTests() {
        boolean skipTests = false;
        
        final String[] skipFlags = {"maven.test.skip", "skipTests", "qunit.skip"};
        
        for(String skipFlag : skipFlags){
            String value = System.getProperty(skipFlag);
            if(value!=null && !value.trim().toLowerCase().equals("false")){
                getLog().warn("###########################################################################");
                getLog().warn("## Skipping Qunit tests because the \"" + skipFlag + "\" property is set.");
                getLog().warn("###########################################################################");
                skipTests = true;
                break;
            }
        }
        return skipTests;
    }
}

