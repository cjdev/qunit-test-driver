package com.cj.qunitTestDriver

import com.gargoylesoftware.htmlunit.BrowserVersion


public class QUnitTestPage {
    static final DEFAULT_TIMEOUT = 5000

	PageDriver driver;
    Integer timeout;

    public QUnitTestPage(URL url, timeout=DEFAULT_TIMEOUT, BrowserVersion browserVersion, Boolean waitForTestsToFinish) {
        driver = new PageDriver(url.toString(), browserVersion)
        driver.waitForAjax()
        this.timeout = timeout
        
		if(waitForTestsToFinish)
        	waitForQunitTests()
    }

    public QUnitTestPage(int localPort, String relativePathOfTest, Integer testTimeout, BrowserVersion browserVersion, Boolean waitForTestsToFinish) {
        this("http://localhost:$localPort/$relativePathOfTest".toURL(), testTimeout, browserVersion, waitForTestsToFinish)
    }

    void waitForQunitTests() {
        driver.waitForTextToBePresent("Tests completed in", timeout)
    }

    public void assertTestsPass(){

        driver.shouldContainText("Tests completed in")

        if (failed() > 0){

            def numFailedAssertions = 0
            def brokenTests = ""
			def failedTestNodes = driver.findElementsByXPath("//ol[@id='qunit-tests']//li[@class='fail' and contains(@id,'qunit-test-output')]")

            failedTestNodes.each { testNode -> 
				def module = testNode.getFirstByXPath(".//*[@class='module-name']").asText()
				def test = testNode.getFirstByXPath(".//*[@class='test-name']").asText()

                brokenTests += "\nTest Name: ${module}: ${test}"

                def failedAssertionNodes = testNode.getByXPath(".//li[@class='fail']")

                failedAssertionNodes.each { failure -> 
                    numFailedAssertions++
                    brokenTests += "\n\tFailed Assertion: ${failure.asText()}" 
                }
            }

            throw new AssertionError("${numFailedAssertions} assertions failed. ${brokenTests}\n\n\n${driver.getPage().asText()}")
        }
        
        if(passed()<=0){
            throw new AssertionError("There were no tests found in this html file.")
        }
    }
    
    Integer failed(){
        def failed = new QUnitElement(driver).withClass("failed")
        Integer.parseInt(failed.asText())
    }
    
    Integer passed(){
        Integer.parseInt(new QUnitElement(driver).withClass("passed").asText())
    }
}
