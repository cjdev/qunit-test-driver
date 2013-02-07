package com.cj.qunitTestDriver.junit;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import junit.framework.AssertionFailedError;

import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;

import com.cj.qunitTestDriver.QUnitTestDriver;
import com.cj.qunitTestDriver.QUnitTestPage;
import com.cj.qunitTestDriver.Status;
import com.cj.qunitTestDriver.TestStatus;
import com.cj.qunitTestDriver.config.Configuration;

public class QUnitSuite extends Runner {

    private final Description d;
    private QUnitTest junitTestInstance;
    private QUnitTestDriver driver;
    private QUnitTestPage page;

    public QUnitSuite(Class<QUnitTest> klass)  {
        try {
            d = Description.createSuiteDescription(klass);
            junitTestInstance = klass.getConstructor(new Class[]{}).newInstance();

            driver = new QUnitTestDriver(
                    junitTestInstance.testPageUrl, 
                    junitTestInstance.configurations.toArray(new Configuration[]{}));

            page = driver.getTestPageImmediate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    Map<String, Description> modules = new HashMap<String, Description>();
    Map<String, Description> tests = new HashMap<String, Description>();
    
    @Override
    public Description getDescription() {

        updateTestSuiteShape();
        
        return d;
    }

    private void updateTestSuiteShape() {
        Status status = page.status();


        for(TestStatus test : status.tests){

            final String moduleName = test.moduleName;
            
            final Description md;
            if(modules.containsKey(moduleName)){
                md = modules.get(moduleName);
            }else{
                System.out.println("Module: " + moduleName);
                md = Description.createSuiteDescription(moduleName);
                this.d.addChild(md);
                modules.put(moduleName, md);
            }

            if(!tests.containsKey(test.name)){
                Description d = Description.createSuiteDescription(test.name);
                md.addChild(d);
                tests.put(test.name, d);
            }

        }
    }

    @Override
    public void run(RunNotifier notifier) {
        notifier.fireTestStarted(getDescription());

        doIt(notifier);

        notifier.fireTestFinished(getDescription());
    }


    private void doIt(RunNotifier notifier) {
        try {

            while(true){

                updateTestSuiteShape();
                
                Status status = page.status();

                Map<String, Description> startedModules = new HashMap<String, Description>();
                Map<String, Description> startedTests = new HashMap<String, Description>();
                Set<String> failedTests = new HashSet<String>();
                Set<String> passedTests = new HashSet<String>();

                for(TestStatus test : status.tests){
                    final Description md;
                    final Description d; 

                    final String moduleName = test.moduleName;

                    if(startedModules.containsKey(moduleName)){
                        md = startedModules.get(moduleName);
                    }else{
                        md = modules.get(moduleName);
                        notifier.fireTestStarted(md);
                        startedModules.put(moduleName, md);
                        notifier.fireTestStarted(md);
                    }

                    if(startedTests.containsKey(test.name)){
                        d = startedTests.get(test.name);
                    }else{
                        d = tests.get(test.name);
                        startedTests.put(test.name, d);
                        notifier.fireTestStarted(d);
                    }

                    if(test.failed && !failedTests.contains(test.name)){
                        
                        int total = test.assertionsPassed + test.assertionsFailed;
                        String description = "Passed: " + test.assertionsPassed + ", Failed: " + test.assertionsFailed + ", Total: " + total;
                        notifier.fireTestFailure(new Failure(d, new AssertionFailedError(description)));
                        failedTests.add(test.name);
                    }

                    if(test.passed && !passedTests.contains(test.name)){
                        notifier.fireTestFinished(d);
                        passedTests.add(test.name);
                    }
                }

                if(status.isComplete){
                    break;
                }

                for(Description d : startedModules.values()){
                    notifier.fireTestFinished(d);
                }

                Thread.sleep(200);
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
