package cj.htmlunit

import com.gargoylesoftware.htmlunit.html.HtmlInput
import static org.junit.Assert.assertTrue
import static org.junit.Assert.assertFalse

class CJInput {

    private final PageDriver driver
    private HtmlInput input

    CJInput(PageDriver driver) {
        this.driver = driver
    }

    CJInput withValue(String value) {
        input = (HtmlInput) driver.findElementByXPath("//input[@value='" + value + "']")
        this
    }

    public CJInput withId(String id) {
        input = (HtmlInput) driver.findElementByXPath("//input[@id='" + id + "']")
        this
    }

    CJInput withName(String name) {
        input = (HtmlInput) driver.findElementByXPath("//input[@name='" + name + "']")
        this
    }

    CJInput withClass(String className) {
        input = (HtmlInput) driver.findElementByXPath("//input[contains(@class, '" + className + "')]")
        this
    }

    CJInput setValue(String value) {
        input.setValueAttribute(value)
        this
    }

    PageDriver click() {
        input.click()
        driver.useMostRecentlyOpenedPage()
    }

    HtmlInput getInput() {
        input
    }

    CJInput shouldBeVisible() {
        driver.shouldBeVisible(input)
        this
    }

    boolean isEnabled() {
        !input.hasAttribute("disabled")
    }

    CJInput shouldBeHidden() {
        driver.shouldBeHidden(input)
        this
    }

    CJInput shouldBeDisabled() {
        assertFalse(isEnabled())
        this
    }

    CJInput shouldBeEnabled() {
        assertTrue(isEnabled())
        this
    }
}
