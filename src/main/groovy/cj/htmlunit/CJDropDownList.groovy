package cj.htmlunit

import com.gargoylesoftware.htmlunit.ElementNotFoundException
import com.gargoylesoftware.htmlunit.html.HtmlSelect
import static org.junit.Assert.fail

class CJDropDownList {
    private final PageDriver driver
    private HtmlSelect dropDownList

    CJDropDownList(PageDriver driver) {
        this.driver = driver
    }

    CJDropDownList withId(String id) {
        dropDownList = (HtmlSelect) driver.findElementByXPath("//select[@id='" + id + "']")
        this
    }

    CJDropDownList withName(String listName) {
        dropDownList = (HtmlSelect) driver.findElementByXPath("//select[@name='" + listName + "']")
        this
    }

    CJDropDownList setValue(String value){
        dropDownList.setSelectedAttribute(value, true)
        this
    }

    CJDropDownList setText(String text){
        def option = dropDownList.getOptionByText(text)
        dropDownList.setSelectedAttribute(option, true)
        this
    }

     CJDropDownList shouldBeVisible() {
        driver.shouldBeVisible(dropDownList)
        this
    }

     CJDropDownList shouldNotContainValue(String text) {
          try{
            dropDownList.getOptionByValue(text)
             fail("Expected: $text NOT in ${dropDownList.asText()}")
         } catch (ElementNotFoundException e) { }
        this
     }

    CJDropDownList shouldContainValue(String text) {
        try{
            dropDownList.getOptionByValue(text)
         } catch (ElementNotFoundException e) {
            fail("Expected: $text in ${dropDownList.asText()}")
        }
        this
    }

    CJDropDownList shouldContainText(String text) {
        try{
            dropDownList.getOptionByText(text)
         } catch (ElementNotFoundException e) {
            fail("Expected: $text in ${dropDownList.asText()}")
        }
        this
    }

    CJDropDownList hasAllValues(List<String> allValues){
        allValues.each({
            try{
                dropDownList.getOptionByValue(it)
            } catch (ElementNotFoundException e) {
                fail("Expected: $it in ${dropDownList.asText()}")
            }
        })
        this
    }

    CJDropDownList hasAllTextOptions(List<String> allTextOptions){
        allTextOptions.each({
            try{
                dropDownList.getOptionByText(it)
            } catch (ElementNotFoundException e) {
                fail("Expected: $it in ${dropDownList.asText()}")
            }
        })
        this
    }
}
