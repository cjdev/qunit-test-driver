package com.cj.qunitTestDriver.junit;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

import com.cj.qunitTestDriver.config.Configuration;
import com.cj.qunitTestDriver.junit.QUnitTest;

public class QUnitTestTest  {
    
    public static class TestClass extends QUnitTest {
        @Override
        public String getTestPageUrl() {
            
            return "src/test/resources/QUnitTestPageTest.html";
        }
        @Override
        public List<Configuration> getConfigurations() {
            return null;
        }
    }
    
    
    @Test
	public void relaysTestFailuresToJunit() throws Exception {
        // given
        Class<?> aFailingTest = TestClass.class;
        
        // when
        Result result = JUnitCore.runClasses(aFailingTest);
	    
        // then
        Assert.assertEquals(0, result.getIgnoreCount());
        Assert.assertEquals(1, result.getFailureCount());
        Assert.assertEquals(5, result.getRunCount());
        final Failure failure = result.getFailures().get(0);
        Assert.assertEquals("Passed: 1, Failed: 2, Total: 3", failure.getMessage());
        Assert.assertEquals("a broken qunit", failure.getDescription().getMethodName());
        Assert.assertEquals(1, failure.getDescription().testCount());
        Assert.assertTrue(failure.getDescription().getDisplayName().contains("a broken qunit"));
	}

}
