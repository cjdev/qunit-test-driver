package cj.htmlunit


import com.gargoylesoftware.htmlunit.html.HtmlInput
import com.gargoylesoftware.htmlunit.html.DomNode
import com.gargoylesoftware.htmlunit.html.HtmlSelect
import com.gargoylesoftware.htmlunit.html.HtmlElement
import java.util.regex.Pattern

class CJPage {

    public static final Pattern DISPLAY_NONE = Pattern.compile("display:\\s*none;")

    protected final PageDriver driver

    public CJPage(String url) {
        this(new PageDriver(url, Locale.ENGLISH))
    }

    protected CJPage(PageDriver driver) {
        this.driver = driver
    }

    public CJElement element() {
        driver.element();
    }

    public String getTitleText() {
        driver.getTitleText()
    }

    public CJPage titleShouldContain(String text) {
        driver.titleShouldContain(text)
        return this
    }

    public String asText() {
        driver.pageAsText()
    }

    public String asXml() {
        driver.pageAsXml()
    }

    public HtmlInput findInputByName(String name) {
        driver.findInputByName(name)
    }

    public HtmlInput findInputByClass(String className) {
        driver.findInputByClass(className)
    }


    public HtmlSelect findSelectByName(String name) {
        driver.findSelectByName(name)
    }

    public HtmlSelect findSelectByClass(String className) {
        driver.findSelectByClass(className)
    }

    @Deprecated
    public DomNode findElementByXPath(String xPath) {
        driver.findElementByXPath(xPath)
    }

    @Deprecated
    protected HtmlElement findHtmlElementByXPath(String selector) {
        driver.findHtmlElementByXPath(selector)
    }

    @Deprecated //This should be a protected method for assertions and errors only!
    public List<DomNode> findElementsByXPath(String xPath) {
        driver.findElementsByXPath(xPath)
    }

    public DomNode elementShouldExist(String xPath) {
        driver.elementShouldExist(xPath)
    }

    public void elementShouldNotExist(String xPath) {
        driver.elementShouldNotExist(xPath)
    }

    public <T> T js(String script) {
        driver.js(script)
    }

    public def fireWindowResizeEvent() {
        driver.fireWindowResizeEvent()
    }

    public def fireChangeEventFor(String jquerySelector) {
        driver.fireChangeEventFor(jquerySelector)
    }

    public void dropDownShouldNotContainText(HtmlSelect dropDown, String countryCurrency) {
        driver.dropDownShouldNotContainText(dropDown, countryCurrency)
    }

    public CJPage waitForAjax() {
        driver.waitForAjax()
        this
    }

    public type(HtmlInput target, String value){
        driver.type(target, value);
    }

    @Deprecated
    String getPropertyValue(String key) {
        driver.getPropertyValue(key)
    }

    String getPropertyValue(String key, List<String> params) {
        driver.getPropertyValue(key, params)
    }

    @Deprecated
    void shouldBeVisible(HtmlElement element) {
        driver.shouldBeVisible(element)
    }

    @Deprecated
    void shouldBeHidden(HtmlElement element) {
        driver.shouldBeHidden(element)
    }

    CJPage shouldContainText(String text) {
        driver.shouldContainText(text)
        this
    }

    CJPage shouldContainPropertyText(String key){
        driver.shouldContainPropertyText(key)
        this
    }

    CJPage shouldNotContainText(String text) {
        driver.shouldNotContainText(text)
        this
    }

    CJPage shouldContainLinkWithText(String text){
        def theAnchor = anchor().withText(text)
        theAnchor != null
        this
    }

    String getWindowName() {
        driver.getCurrentWindowName()
    }

    CJPage titleShouldMatchProperty(String key) {
        driver.titleShouldMatchProperty(key)
        this
    }

    CJPage titleShouldMatch(String title) {
        driver.titleShouldMatch(title)
        this
    }

    CJPage clickTabByProperty(String key) {
        driver.clickTabByProperty(key)
        this
    }
}
