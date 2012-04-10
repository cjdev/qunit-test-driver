package com.googlecode.qunitTestDriver

import com.gargoylesoftware.htmlunit.BrowserVersion


public class QUnitTestPage {

    static final DEFAULT_TIMEOUT = 5000

	PageDriver driver;
    Integer timeout;

    public QUnitTestPage(URL url, timeout=DEFAULT_TIMEOUT, BrowserVersion browserVersion){
        driver = new PageDriver(url.toString(), browserVersion)
        driver.waitForAjax()
        this.timeout = timeout
        
        waitForQunitTests()
    }

    public QUnitTestPage(int localPort, String relativePathOfTest, Integer testTimeout, BrowserVersion browserVersion){
        this("http://localhost:$localPort/$relativePathOfTest".toURL(), testTimeout, browserVersion)
    }

    void waitForQunitTests() {
        System.out.println("Test timeout is $timeout milliseconds")
        driver.waitForTextToBePresent("Tests completed in", timeout)
    }

    public void assertTestsPass(){

        driver.shouldContainText("Tests completed in")

        if (failed() > 0){

            def numFailedAssertions = 0
            def brokenTests = ""
            def failedTestNameNodes = driver.findElementsByXPath("//ol[@id='qunit-tests']//li[@class='fail' and contains(@id,'qunit-output')]")

            failedTestNameNodes.each { testNameNode -> 

                def testName = testNameNode.getFirstByXPath(".//*[@class='qunit-name']").textContent

                brokenTests += "\n\nTest Name: ${testName}\n\n"

                def failedAssertionNodes = testNameNode.getByXPath(".//li[@class='fail']")

                failedAssertionNodes.each { failure -> 
                    numFailedAssertions++
                    brokenTests += "\tFailed Assertion: ${failure.asText()}\n" 
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
