package cj.htmlunit

import com.gargoylesoftware.htmlunit.html.HtmlSpan
import static org.junit.Assert.assertTrue

class CJSpan {
    private HtmlSpan span
    private final PageDriver driver

    CJSpan(PageDriver driver){
        this.driver = driver
    }

    CJSpan withId(String id) {
        span = (HtmlSpan) driver.findElementByXPath("//span[@id='" + id + "']")
        this
    }

    CJSpan withClass(String className) {
        span = (HtmlSpan) driver.findElementByXPath("//span[contains(@class, '" + className + "')]")
        this
    }

    CJSpan shouldContainText(String text) {
        assertTrue("Expected: $text in ${span.asText()}", span.asText().contains(text))
        this
    }

    String getText() {
        span.getTextContent()
    }

    CJSpan shouldBeVisible() {
        driver.shouldBeVisible(span)
        this
    }
}
