package cj.htmlunit

import com.gargoylesoftware.htmlunit.html.DomNode
import com.gargoylesoftware.htmlunit.html.HtmlElement
import com.gargoylesoftware.htmlunit.html.HtmlImage
import com.davidron.qunitTestDriver.PageDriver;
import static org.junit.Assert.*

class CJElement {
    private HtmlElement element
    private final PageDriver driver

    CJElement(PageDriver driver) {
        this.driver = driver
    }

    public CJElement element() {
        driver.element()
    }

     CJElement withId(String id) {
        element = (HtmlElement) driver.findElementByXPath("//*[@id='" + id + "']")
        this
    }

    CJElement withClass(String className) {
        element = (HtmlElement) driver.findElementByXPath("//*[contains(@class, '" + className + "')]")
        this
    }

    CJElement withText(String text) {
        element = (HtmlElement) driver.findElementByXPath("//*[contains(text(), '" + text + "')]")
        this
    }
    
    CJElement withXpath(String xpath) {
        element = (HtmlElement) driver.findElementByXPath(xpath)
        this
    }

    CJElement shouldContainText(String text) {
        assertTrue("Expected: $text in ${element.asText()}", element.asText().contains(text))
        this
    }

    CJElement shouldNotContainText(String text) {
        assertFalse("Expected: $text in ${element.asText()}", element.asText().contains(text))
        this
    }

    CJElement shouldContainImage(String imageName) {
        def found = false
        for (DomNode node: element.getChildElements()) {
            if (node instanceof HtmlImage) {
                found = true
                HtmlImage image = (HtmlImage) node
                assertTrue("Expected: $image in ${image.getSrcAttribute()}", image.getSrcAttribute().contains(imageName))
            }
        }
        if (!found) fail("Expected $imageName in ${element.asText()}")
        this
    }

    CJElement titleAttributeShouldContainText(String text) {
        String title = element.getAttribute("title")
        assertTrue("Expected: $text in $title", title.contains(text))
        this
    }

    CJElement shouldBeVisible() {
        driver.shouldBeVisible(element)
        this
    }

    CJElement shouldBeHidden() {
        driver.shouldBeHidden(element)
        this
    }

    CJElement withXPath(String xPath) {
        element = (HtmlElement) driver.findElementByXPath(xPath)
        this
    }

    String getText(){
        element.getTextContent(); 
    }
}
