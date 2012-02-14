package com.googlecode.qunitTestDriver

import com.gargoylesoftware.htmlunit.html.DomNode

public class QUnitTestPage {
	PageDriver driver;

    public QUnitTestPage(URL url){
        driver = new PageDriver(url.toString())
        driver.waitForAjax()
        
        waitForQunitTests()
    }

    public QUnitTestPage(int localPort, String relativePathOfTest){
        this("http://localhost:$localPort/$relativePathOfTest".toURL())
    }
    
    void waitForQunitTests() {
        driver.waitForTextToBePresent("Tests completed in")
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
        def failed = new CJElement(driver).withClass("failed")
        Integer.parseInt(failed.asText())
    }
    
    Integer passed(){
        Integer.parseInt(new CJElement(driver).withClass("passed").asText())
    }
}
