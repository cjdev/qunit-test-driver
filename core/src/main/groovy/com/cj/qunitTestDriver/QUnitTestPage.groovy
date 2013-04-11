package com.cj.qunitTestDriver

import java.util.List;

import com.gargoylesoftware.htmlunit.BrowserVersion
import com.gargoylesoftware.htmlunit.html.DomNode;

import com.cj.qunitTestDriver.WebClientFactory

public class QUnitTestPage {
    private static final String TESTS_COMPLETED_STRING = "Tests completed in";
    private static final String GLOBAL_FAILURE_STRING = "global failure";
    public PageDriver driver;
    

    public QUnitTestPage(URL url, timeout, BrowserVersion browserVersion, Boolean waitForTestsToFinish) {
        driver = new PageDriver(
                new WebClientFactory().getWebClient(browserVersion),
                url.toString()
                )

        driver.waitForAjax();

        if(waitForTestsToFinish) {
            if(!waitForQunitTests(timeout)) {
		//never saw the text!
		def text = driver.getPage().asText()
		throw new RuntimeException(
                    "Failed to find test termination marker (" +
                    [TESTS_COMPLETED_STRING, GLOBAL_FAILURE_STRING] +
                    ") in page text:\n\n" +
                    text)
            }
        }
    }

    public QUnitTestPage(int localPort, String relativePathOfTest, Integer testTimeout, BrowserVersion browserVersion, Boolean waitForTestsToFinish) {
        this("http://localhost:$localPort/$relativePathOfTest".toURL(), testTimeout, browserVersion, waitForTestsToFinish);
    }

    boolean waitForQunitTests(timeout) {
        return driver.waitForTextToBePresent(
            [TESTS_COMPLETED_STRING, GLOBAL_FAILURE_STRING],
            timeout
            )
    }


    public Status status(){
        boolean isComplete = driver.containsText(TESTS_COMPLETED_STRING) || driver.containsText(GLOBAL_FAILURE_STRING);
        int numPassed = passed();
        int numFailed = failed();
        int numFailedAssertions = 0;
        String brokenTests = "";
        List<TestStatus> tests = [];

        if(numPassed > 0 || numFailed > 0) {
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


    /**
    * How many different ways can our tests fail?
    */
    public void assertTestsPass(){
        if( driver.containsText(GLOBAL_FAILURE_STRING) ) {
            throw new AssertionError(
                "There was a global failure while running the QUnit test(s). This is likely due to poorly managed global variables." +
                "\n\n" +
                "Please review your globals and see if they can be replaced with require modules"
            );
        }

        if ( !driver.containsText(TESTS_COMPLETED_STRING) ) {
            throw new AssertionError(
                "Something funny happened, the test run did not finish, nor was there a global failure."
            );
        }

        Status status = status();

        if(status.numFailed > 0) {
            throw new AssertionError("${status.numFailedAssertions} assertions failed. ${status.brokenTests}\n\n\n${driver.getPage().asText()}")
        }

        if(status.numPassed<=0){
            throw new AssertionError("There were no tests found in this html file.")
        }
    }

    Integer failed(){
        return getTestResultCount("failed")
    }

    Integer passed(){
        return getTestResultCount("passed")
    }

    private Integer getTestResultCount(String resultType) {
        return Integer.parseInt(
            ((List<DomNode>)
                driver.page.getByXPath(
                    "/html/body/p[@id=\"qunit-testresult\"]/span[contains(@class, '" +
                    resultType +
                    "')]")
            ).get(0).asText()
        )
    }
}
