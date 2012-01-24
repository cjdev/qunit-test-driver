package cj.htmlunit

import static org.junit.Assert.assertTrue
import static org.junit.Assert.fail

import com.gargoylesoftware.htmlunit.BrowserVersion
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController
import com.gargoylesoftware.htmlunit.WebClient
import com.gargoylesoftware.htmlunit.WebWindow
import com.gargoylesoftware.htmlunit.html.DomNode
import com.gargoylesoftware.htmlunit.html.HtmlElement
import static org.junit.Assert.assertFalse
import com.gargoylesoftware.htmlunit.html.HtmlAnchor
import java.util.regex.Pattern
import java.util.regex.Matcher
import java.text.MessageFormat
import static org.junit.Assert.assertEquals
import com.gargoylesoftware.htmlunit.html.HtmlInput
import com.gargoylesoftware.htmlunit.html.HtmlPage
import com.gargoylesoftware.htmlunit.html.HtmlSelect
import com.gargoylesoftware.htmlunit.TopLevelWindow
import com.gargoylesoftware.htmlunit.javascript.host.Event

class PageDriver {
    public static final Pattern DISPLAY_NONE = Pattern.compile("display:\\s*none;")

    private HtmlPage page
    private final Locale locale
    private final String MAIN_WINDOW_NAME = ""
    private final WebClient webClient

    public PageDriver(String url, Locale locale) {
        webClient = new WebClient(BrowserVersion.FIREFOX_3)
        webClient.setUseInsecureSSL(true)
        navigateTo(url)
        this.locale = locale
    }
    protected HtmlPage getPage() {
        return page
    }

    public String getTitleText() {
        return getPage().getTitleText()
    }

    public String pageAsText() {
        return getPage().asText()
    }

    public String pageAsXml() {
        return getPage().asXml()
    }

    public HtmlInput findInputByName(String name) {
        return (HtmlInput) findElementByXPath("//input[@name='" + name + "']")
    }

    public HtmlInput findInputByClass(String className) {
        return (HtmlInput) findElementByXPath("//input[contains(@class, '$className')]")
    }


    public HtmlSelect findSelectByName(String name) {
        return (HtmlSelect) findElementByXPath("//select[@name='" + name + "']")
    }

    public HtmlSelect findSelectByClass(String className) {
        return (HtmlSelect) findElementByXPath("//select[contains(@class, '$className')]")
    }

    public DomNode findElementByXPath(String xPath) {
        List elements = findElementsByXPath(xPath)
        assertTrue("Could not find an element matching: " + xPath + " in ${pageAsXml()}", elements.size() > 0)
        return elements.get(0)
    }

    protected HtmlElement findHtmlElementByXPath(String selector) {
        return (HtmlElement) findElementByXPath(selector)
    }


    public List<DomNode> findElementsByXPath(String xPath) {
        return (List<DomNode>) getPage().getByXPath(xPath)
    }

    @Deprecated //Use CJElement instead.
    public DomNode elementShouldExist(String xPath) {
        def elements = findElementsByXPath(xPath)
        assertTrue("Element does not exist: " + xPath, elements.size() > 0)
        return elements.get(0)
    }

    public void elementShouldNotExist(String xPath) {
        def elements = findElementsByXPath(xPath)
        assertTrue("Found elements matching xPath: " + xPath, elements.size() == 0)
    }

    public <T> T js(String script) {
        return (T) page.executeJavaScript(script).javaScriptResult
    }

    public def fireWindowResizeEvent() {
        js("jQuery(window).resize()");
    }

    public def fireChangeEventFor(String jquerySelector) {
        js('jQuery("' + jquerySelector + '").change()');
    }

     /**
     * Workaround for HtmlElement.type():
     * - throwing IndexOutOfBounds when control does not have default value
     * - not firing keydown event on controls injected into DOM dynamically
     * @param target
     * @param value
     * @return
     */
    public def type(HtmlInput target, String value){
        target.setValueAttribute(value);
        target.fireEvent(new Event(target, Event.TYPE_KEY_DOWN));
    }

    public void dropDownShouldNotContainText(HtmlSelect dropDown, String countryCurrency) {
        try {
            dropDown.getOptionByText(countryCurrency)
            fail("Found an option for: $countryCurrency")
        }
        catch (ElementNotFoundException) {
            //This is what we want to happen
        }
    }

    public PageDriver waitForAjax() {
        webClient.waitForBackgroundJavaScript(3000);
        return this;
    }
    
    public PageDriver waitForTextToBePresent(String text, Integer timeout=20000){
        String potentialError = "'"+text+"' didn't show up in "+timeout+" milliseconds."
        int millisToWait = 100
        for(int t=timeout; t>0; t+=-millisToWait){
            try{
                shouldContainText(text)
                return;
            }catch(Throwable th){}
            System.out.println("Waiting For Javascript..."+t)
            Thread.sleep(millisToWait)
        }
        println(page.asText())
        throw new AssertionError(potentialError)
    }

    public PageDriver wait(int ms) {
        Thread.sleep(ms)
        this
    }

    public static String getPropertyValue(String bundleLocation, Locale locale, String propertyName) {
        String value = ResourceBundle.getBundle(bundleLocation, locale).getString(propertyName)

        String ESCAPED_SINGLE_QUOTE = "''"
        String SINGLE_QUOTE = "'"
        return value.replaceAll(ESCAPED_SINGLE_QUOTE, SINGLE_QUOTE)
    }

    public static String getPropertyValue(String bundleLocation, Locale locale, String propertyName, List<String> params) {
        return MessageFormat.format(getPropertyValue(bundleLocation, locale, propertyName), params.toArray())
    }

    String getPropertyValue(String key) {
        return getPropertyValue("cj.cjo.language.content", locale, key)
    }

    String getPropertyValue(String key, List<String> params) {
        return getPropertyValue("cj.cjo.language.content", locale, key, params)
    }

    PageDriver useWindow(String name) {
        this.page = (HtmlPage) webClient.getWebWindowByName(name).getEnclosedPage()
        this
    }

    PageDriver useMainWindow() {
        useWindow(MAIN_WINDOW_NAME)
    }

    private HtmlPage getHtmlPageFrom(WebWindow window) {
        (HtmlPage) window.getEnclosedPage()
    }

    private List<WebWindow> getWebWindows() {
        webClient.getWebWindows()
    }


    protected void shouldBeVisible(HtmlElement element) {
        def styleAttr = element.getAttributes().getNamedItem("style")
        if (!styleAttr) {
            return
        }
        Matcher m = DISPLAY_NONE.matcher(styleAttr.nodeValue)
        assertFalse(m.find())
    }

    protected void shouldBeHidden(HtmlElement element) {
        def styleAttr = element.getAttribute("style")
        Matcher m = DISPLAY_NONE.matcher(styleAttr)
        assertTrue("Style attr was: ${styleAttr}\n", m.find())
    }

    PageDriver shouldContainText(String text) {
        assertTrue("Expected '$text' in '${pageAsText()}'", pageAsText().contains(text))
        return this
    }

    PageDriver shouldContainPropertyText(String key){
        String text = getPropertyValue(key)
        shouldContainText(text)
    }

    PageDriver shouldNotContainText(String text) {
        assertFalse("Did not expect '$text' in '${pageAsText()}'", pageAsText().contains(text))
        return this
    }

    PageDriver titleShouldMatch(String text) {
        assertTrue("Expected '$text' in '${getTitleText()}'", getTitleText().contains(text))
        this
    }

    PageDriver useMostRecentlyOpenedPage() {
        final List<WebWindow> webWindows = webClient.getWebWindows()
        this.page = (HtmlPage) webWindows.get(webWindows.size() - 1).getEnclosedPage()
        this
    }

    String getCurrentWindowName() {
        page.getEnclosingWindow().getName()
    }

    PageDriver closeCurrentWindow() {
        def currentWindow = (TopLevelWindow) page.getEnclosingWindow()
        currentWindow.close()
        useMostRecentlyOpenedPage()
        this
    }

    PageDriver clickTabByProperty(String key) {
        String value = getPropertyValue(key)
        anchor().withXPath("//a[contains(@class,'tab') and contains(.,'$value')]").click()
    }

    PageDriver titleShouldMatchProperty(String key) {
        titleShouldMatch(getPropertyValue(key).replaceAll("''", "'"))
        this
    }

    PageDriver titleShouldContain(String text) {
        assertTrue("Expected: $text in ${getTitleText()}", getTitleText().contains(text))
        this
    }

    PageDriver click(HtmlAnchor anchor) {
        page = (HtmlPage) anchor.click()
        this
    }

    PageDriver ignoreJavascriptErrors() {
        webClient.setThrowExceptionOnScriptError(false)
        this
    }

    PageDriver doNotIgnoreJavascriptErrors() {
        webClient.setThrowExceptionOnScriptError(true)
        this
    }

    void turnOffStatusCodeErrors(){
        webClient.setThrowExceptionOnFailingStatusCode(false)
    }

    PageDriver shouldBeAtMainWindow() {
        assertEquals(MAIN_WINDOW_NAME, getCurrentWindowName())
        this
    }

    PageDriver navigateTo(String url) {
        page = (HtmlPage) webClient.getPage(url)
        this
    }
}
