package cj.htmlunit

import com.gargoylesoftware.htmlunit.html.HtmlCheckBoxInput

class CJCheckBox {
    private HtmlCheckBoxInput checkBox
    private final PageDriver driver

    CJCheckBox(PageDriver driver) {
        this.driver = driver
    }

    CJCheckBox withId(String id) {
        checkBox = (HtmlCheckBoxInput) driver.findElementByXPath("//input[@type='checkbox' and @id='" + id + "']")
        this
    }

    CJCheckBox click() {
        checkBox.click()
        this
    }

    CJCheckBox shouldBeVisible() {
        driver.shouldBeVisible(checkBox)
        this
    }
}
