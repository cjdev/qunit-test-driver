package cj.htmlunit

import com.gargoylesoftware.htmlunit.html.HtmlRadioButtonInput

class CJRadio {
    private HtmlRadioButtonInput radio
    private final PageDriver driver

    CJRadio(PageDriver driver) {
      this.driver = driver
    }

    CJRadio withId(String id) {
        radio = (HtmlRadioButtonInput) driver.findElementByXPath("//input[@type='radio' and @id='" + id + "']")
        this
    }

    CJRadio click() {
        radio.click()
        this
    }
}
