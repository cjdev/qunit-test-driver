package com.googlecode.qunitTestDriver
import static org.junit.Assert.*

import java.util.List;

import com.gargoylesoftware.htmlunit.BrowserVersion
import com.gargoylesoftware.htmlunit.WebClient
import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.HtmlPage



class PageDriver {
	
	private final WebClient webClient
	private HtmlPage page
	
	
	
	public PageDriver(String url) {
		webClient = new WebClient(BrowserVersion.FIREFOX_3)
		webClient.setUseInsecureSSL(true)
		navigateTo(url)
	}
	
	PageDriver navigateTo(String url) {
		page = (HtmlPage) webClient.getPage(url)
		this
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
	
	PageDriver shouldContainText(String text) {
		assertTrue("Expected '$text' in '${page.asText()}'", page.asText().contains(text))
		return this
	}
	
	public List<DomNode> findElementsByXPath(String xPath) {
		return (List<DomNode>) page.getByXPath(xPath)
	}

}
