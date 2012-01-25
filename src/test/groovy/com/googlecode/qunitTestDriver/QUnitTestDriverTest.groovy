package com.googlecode.qunitTestDriver

import com.googlecode.qunitTestDriver.config.JoinToServer;
import com.googlecode.qunitTestDriver.config.PortRange
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
		QUnitTestDriver runner = new QUnitTestDriver(testPageUrl, new PortRange(9876))
		assertEquals(9876, runner.getServer().getPort())
	}
}