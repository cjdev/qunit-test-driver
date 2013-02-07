package com.cj.qunitTestDriver.junit;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

public class QUnitTestTest  {
    
    public static class TestClass extends QUnitTest {
        public TestClass() {
            super("src/test/resources/QUnitTestPageTest.html");
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
        Assert.assertEquals(2, result.getRunCount());
        final Failure failure = result.getFailures().get(0);
        Assert.assertEquals("Passed: 1, Failed: 2, Total: 3", failure.getMessage());
        Assert.assertEquals("a broken qunit", failure.getDescription().getDisplayName());
        Assert.assertEquals(1, failure.getDescription().testCount());
        Assert.assertTrue(failure.getDescription().getDisplayName().contains("a broken qunit"));
	}

}
