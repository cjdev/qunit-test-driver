package cj.htmlunit

import static groovy.util.GroovyTestCase.assertEquals

class CJTextBox {
    private final CJInput input

    CJTextBox(PageDriver driver) {
        input = new CJInput(driver)
    }

    CJTextBox withName(String name) {
        input.withName(name)
        this
    }

    CJTextBox withId(String id) {
        input.withId(id)
        this
    }

    public CJTextBox setValue(String value) {
        input.getInput().setValueAttribute(value)
        return this;
    }

    public String getValue() {
        input.getInput().getValueAttribute()
    }

    public CJTextBox shouldBeEmpty() {
        return valueShouldBe("")
    }

    public CJTextBox valueShouldBe(String expected) {
        assertEquals(expected, value)
        return this
    }

     CJTextBox shouldBeVisible() {
        input.shouldBeVisible()
        this
    }
}
