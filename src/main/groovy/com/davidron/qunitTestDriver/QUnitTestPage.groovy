package com.davidron.qunitTestDriver

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
        //driver.waitForTextToBePresent("Tests completed in")
    }

    public void assertTestsPass(){
        driver.shouldContainText("Tests completed in")
        if (failed() > 0){
            List<DomNode> nodes = driver.findElementsByXPath("//li[contains(@class, 'fail')]//li[contains(@class, 'fail')]")
            String brokenTests=""
            nodes.each{node->
                brokenTests+=node.asText()+"\n"
            }
            throw new AssertionError(brokenTests)
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
