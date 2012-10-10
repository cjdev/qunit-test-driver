package com.cj.qunitTestDriver

import com.cj.qunitTestDriver.QUnitTestDriver;
import com.cj.qunitTestDriver.QUnitTestPage;
import com.cj.qunitTestDriver.config.PortSet
import com.cj.qunitTestDriver.config.RandomPortSet
import com.cj.qunitTestDriver.config.PathMapping
import com.cj.qunitTestDriver.config.TestTimeout

import org.junit.After;
import org.junit.Test
import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertTrue

class QUnitTestDriverTest {
    private final String testPageUrl="src/test/resources/QUnitTestPageTest.html"
	private QUnitTestDriver runner
    
    @Test
    void pageErrorsAreThrownProperly(){
        Boolean exception = false
		
        try {
            QUnitTestDriver.run(testPageUrl)
        } catch(AssertionError e){
            assertTrue(e.toString().contains("This is a qunit failure"))
            assertTrue(e.toString().contains("This is another qunit failure"))
            exception=true
        }
		
        assertTrue("qUnit should have reported back two errors.", exception)
    }
    
    @Test 
    void twoTestsPassAndTwoTestsFail(){
        runner = new QUnitTestDriver(testPageUrl)
        QUnitTestPage page = runner.getTestPage()
		
        assertEquals(4,page.passed())
        assertEquals(2,page.failed())
		
        runner.getServer().stop()
    }

    @Test 
    void failuresContainTestNameAndFailedAssertion(){
        Boolean exceptionCaught = false
        runner = new QUnitTestDriver(testPageUrl)
		
        try {
            runner.getTestPage().assertTestsPass()
        } catch(AssertionError e) {
            assertTrue("assertions\n$e", e.toString().contains("2 assertions failed"))
            assertTrue("test name\n$e", e.toString().contains("Test Name: broken: a broken qunit"))
            assertTrue("expected failure with label\n$e", e.toString().contains("Failed Assertion: This is a qunit failure"))
            assertTrue("expected failure without label\n$e",e.toString().contains("Failed Assertion: This is another qunit failure"))
            exceptionCaught=true
        }
		
        runner.getServer().stop()
        assertTrue("should have thrown AssertionError after tests failed", exceptionCaught)       
    }
    
    @Test
    void allTestsShouldPass(){
        String noTestPageUrl="src/test/resources/QUnitTestPageWithNoTests.html"
		Boolean exception = false
        runner = new QUnitTestDriver(noTestPageUrl)
        QUnitTestPage page = runner.getTestPage()
		
        assertEquals(0,page.passed())
        assertEquals(0,page.failed())
		
        try {
            page.assertTestsPass()
        } catch(AssertionError e){
            assertTrue(e.toString().contains("no tests found"))
            exception = true
        }
		
        assertTrue("qUnit should have reported back no tests.", exception)       
    }
    
    @Test
    void canConfigurePortRange(){
        runner = new QUnitTestDriver(testPageUrl, new PortSet(9876))
		
        assertEquals(9876, runner.getServer().getPort())
    }

    @Test
    void canConfigurePortRangeWithRandomPortSet(){
        runner = new QUnitTestDriver(testPageUrl, new RandomPortSet())
		
        assertEquals(100, runner.portSet.size())
    }

    @Test
    void canConfigureServerRoots(){
        String newRootOne = "/new/root/one"
		String newRootTwo = "/new/root/two"
		
        runner = new QUnitTestDriver(testPageUrl, new PathMapping("/one", newRootOne), new PathMapping("/one", newRootTwo))
        assertEquals(["/":["./"], "/one":[newRootOne, newRootTwo]], runner.pathMappings)
    }

    @Test
	void canConfigureWithTimeout() {
        Integer timeout = 30000
        runner = new QUnitTestDriver(testPageUrl, new TestTimeout(timeout))
		
        assertEquals(timeout, runner.timeout)
        assertEquals(timeout, runner.getTestPage().timeout)
    }
	
	@After
	void teardown() {
		if(runner != null)
			runner.getServer().stop()
	}
}
