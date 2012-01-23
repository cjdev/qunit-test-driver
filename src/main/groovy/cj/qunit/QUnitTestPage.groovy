package cj.qunit


import com.gargoylesoftware.htmlunit.html.DomNode
import cj.htmlunit.CJPage
import cj.htmlunit.PageDriver

public class QUnitTestPage extends CJPage{

    public QUnitTestPage(URL url){
        super(new PageDriver(url.toString(), Locale.ENGLISH))
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
        def failed = element().withClass("failed")
        Integer.parseInt(failed.getText())
    }
    
    Integer passed(){
        Integer.parseInt(element().withClass("passed").getText())
    }
}
