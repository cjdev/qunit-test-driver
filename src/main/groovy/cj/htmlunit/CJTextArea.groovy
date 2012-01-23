package cj.htmlunit

import com.gargoylesoftware.htmlunit.html.HtmlTextArea
import static groovy.util.GroovyTestCase.assertEquals

class CJTextArea {
    private final PageDriver driver
    private HtmlTextArea textArea

    CJTextArea(PageDriver driver) {
        this.driver = driver
    }

    CJTextArea withId(String id) {
        textArea = (HtmlTextArea) driver.findElementByXPath("//textarea[@id='" + id + "']")
        this
    }

    public String getValue() {
        textArea.getText()
    }

    public CJTextArea valueShouldBe(String expected) {
        assertEquals(expected, value)
        return this
    }

    CJTextArea shouldBeVisible() {
        driver.shouldBeVisible(textArea)
        this
    }
}
