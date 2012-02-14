package com.googlecode.qunitTestDriver

import com.googlecode.qunitTestDriver.config.JoinToServer;
import com.googlecode.qunitTestDriver.config.PortSet
import com.googlecode.qunitTestDriver.config.RandomPortSet
import com.googlecode.qunitTestDriver.config.ServerRoot
import org.junit.Test
import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertTrue


class QUnitTestDriverTest {
    final String testPageUrl="src/test/resources/QUnitTestPageTest.html"
    
    @Test
    void pageErrorsAreThrownProperly(){
        boolean exception = false
        try{
            QUnitTestDriver.run(testPageUrl)
        }catch(AssertionError e){
            assertTrue(e.toString().contains("This is a qunit failure"))
            assertTrue(e.toString().contains("This is another qunit failure"))
            exception=true
        }
        assertTrue("qUnit should have reported back two errors.", exception)
    }
    
    @Test 
    void twoTestsPassAndTwoTestsFail(){
        QUnitTestDriver runner = new QUnitTestDriver(testPageUrl)
        QUnitTestPage page = runner.getTestPage()
        assertEquals(2,page.passed())
        assertEquals(2,page.failed())
        runner.getServer().stop()
    }

    @Test 
    void failuresContainTestNameAndFailedAssertion(){
        def exceptionCaught = false
        QUnitTestDriver runner = new QUnitTestDriver(testPageUrl)
        try{
            runner.getTestPage().assertTestsPass()
        } catch(AssertionError e) {
            assertTrue("assertions\n$e", e.toString().contains("2 assertions failed"))
            assertTrue("test name\n$e", e.toString().contains("Test Name: a broken qunit"))
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
        QUnitTestDriver runner = new QUnitTestDriver(noTestPageUrl)
        QUnitTestPage page = runner.getTestPage()
        assertEquals(0,page.passed())
        assertEquals(0,page.failed())
        runner.getServer().stop()
        Boolean exception=false
        try{
            page.assertTestsPass()
        }catch(AssertionError e){
            assertTrue(e.toString().contains("no tests found"))
            exception=true
        }
        assertTrue("qUnit should have reported back no tests.", exception)       
    }
    
    @Test
    void canConfigurePortRange(){
        QUnitTestDriver runner = new QUnitTestDriver(testPageUrl, new PortSet(9876))
        assertEquals(9876, runner.getServer().getPort())
    }

    @Test
    void canConfigurePortRangeWithRandomPortSet(){
        QUnitTestDriver runner = new QUnitTestDriver(testPageUrl, new RandomPortSet())
        assertEquals(100, runner.portSet.size())
    }

    @Test
    void canConfigureServerRoot(){
        def newRoot = "/new/root"
        QUnitTestDriver runner = new QUnitTestDriver(testPageUrl, new ServerRoot(newRoot))
        assertEquals(newRoot, runner.serverRoot)
    }
}
