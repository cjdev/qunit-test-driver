package cj.htmlunit

import com.gargoylesoftware.htmlunit.html.HtmlAnchor

class CJAnchor {

    private final PageDriver driver
    private HtmlAnchor anchor

    CJAnchor(PageDriver driver) {
        this.driver = driver
    }

    CJAnchor withId(String id) {
        anchor = (HtmlAnchor) driver.findElementByXPath("//a[@id='$id']")
        this
    }

    CJAnchor withPropertyText(String key) {
        String text = driver.getPropertyValue(key)
        withText(text)
    }

    CJAnchor withXPath(String xPath) {
        anchor = (HtmlAnchor) driver.findElementByXPath(xPath)
        this
    }

    CJAnchor withText(String text) {
        anchor = (HtmlAnchor) driver.findElementByXPath("//a[text()='$text']")
        this
    }

    PageDriver click() {
        driver.click(anchor)
    }
}
