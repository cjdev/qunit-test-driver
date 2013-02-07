package com.cj.qunitTestDriver

import java.util.List;

import com.gargoylesoftware.htmlunit.BrowserVersion
import com.gargoylesoftware.htmlunit.html.DomNode;


public class QUnitTestPage {
    private static final String TESTS_COMPLETED_STRING = "Tests completed in"
	public PageDriver driver;
    Integer timeout;

    public QUnitTestPage(URL url, timeout, BrowserVersion browserVersion, Boolean waitForTestsToFinish) {
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
        driver.waitForTextToBePresent(TESTS_COMPLETED_STRING, timeout)
    }

    
    public Status status(){
        
        boolean isComplete = driver.containsText(TESTS_COMPLETED_STRING)
        int numPassed = passed();
        int numFailed = failed();
        int numFailedAssertions = 0;
        String brokenTests = ""
        List<TestStatus> tests = [];
        
        if(numPassed > 0 || numFailed > 0){
            List<DomNode> testNodes = driver.findElementsByXPath("//ol[@id='qunit-tests']//li[contains(@id,'test-output')]");
            
            def modules = [:]
            
            for(DomNode testNode : testNodes) {
                DomNode moduleDomNode = testNode.getFirstByXPath(".//*[@class='module-name']");
                String moduleName = moduleDomNode != null ? moduleDomNode.asText() : "<unnamed module>";
                
                DomNode testNameDomNode = testNode.getFirstByXPath(".//*[@class='test-name']");
                String testName = testNameDomNode.asText();
                
                
                TestStatus test = new TestStatus(testName, moduleName, testNode) 
                tests.add(test);
                
                brokenTests += "\nTest Name: ${moduleName}: ${testName}"
                                
                test.failures.each { failure ->
                    numFailedAssertions++
                    brokenTests += "\n\tFailed Assertion: ${failure}"
                }
            }
        }
        
        return new Status(numPassed, numFailed, numFailedAssertions, brokenTests, isComplete, tests)
    }
    
    
    public void assertTestsPass(){

        driver.shouldContainText("Tests completed in")
        
        Status status = status();
        
        if(status.numFailed > 0){
            throw new AssertionError("${status.numFailedAssertions} assertions failed. ${status.brokenTests}\n\n\n${driver.getPage().asText()}")
        }
        
        
        if(status.numPassed<=0){
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
