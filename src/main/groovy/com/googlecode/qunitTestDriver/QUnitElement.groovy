package com.googlecode.qunitTestDriver

import static org.junit.Assert.*

import com.gargoylesoftware.htmlunit.html.DomNode
import com.gargoylesoftware.htmlunit.html.HtmlElement

class QUnitElement {
    private HtmlElement element
    private final PageDriver driver

    QUnitElement(PageDriver driver) {
        this.driver = driver
    }
	
	HtmlElement withClass(String className) {
		return (HtmlElement) getElementByXPath("//*[contains(@class, '" + className + "')]").get(0);
	}
	
	private List<DomNode> getElementByXPath(String xPath){
		return (List<DomNode>) driver.page.getByXPath(xPath)
	}	
	

	
}
