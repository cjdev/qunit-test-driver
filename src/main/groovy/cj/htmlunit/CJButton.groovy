package cj.htmlunit

class CJButton {
    private final CJInput input

    CJButton(PageDriver driver) {
        input = new CJInput(driver)
    }

    CJButton withName(String name) {
        input.withName(name)
        this
    }

    CJButton withId(String id){
        input.withId(id)
        this
    }

    CJButton withClass(String className) {
        input.withClass(className)
        this
    }

    CJButton withValue(String value){
        input.withValue(value)
        this
    }

    boolean isEnabled() {
        input.isEnabled()
    }

    PageDriver click() {
        input.click()
    }

    CJButton shouldBeDisabled() {
        input.shouldBeDisabled()
        this
    }

    CJButton shouldBeEnabled() {
        input.shouldBeEnabled()
        this
    }
}
