package com.cj.qunitTestDriver

import com.gargoylesoftware.htmlunit.IncorrectnessListener;
import com.gargoylesoftware.htmlunit.BrowserVersion
import com.gargoylesoftware.htmlunit.WebClient
import com.gargoylesoftware.htmlunit.html.DomNode
import com.gargoylesoftware.htmlunit.html.HtmlPage
import static org.junit.Assert.assertTrue

class PageDriver {
	
	private final WebClient webClient
	private HtmlPage page

    private final IncorrectnessListener ignoreObsoleteContentTypes = [notify: {String arg, Object obj ->
        if (!arg.find(/Obsolete content type/)) {
            System.err.println(arg.toString());
        }
    }] as IncorrectnessListener;
	
	public PageDriver(String url, BrowserVersion browserVersion) {
		webClient = new WebClient(browserVersion)
		webClient.setUseInsecureSSL(true)
		webClient.setIncorrectnessListener(ignoreObsoleteContentTypes);
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
	
	public PageDriver waitForTextToBePresent(String text, Integer timeout){
		String potentialError = "'"+text+"' didn't show up in "+timeout+" milliseconds."
		
		int millisToWait = 100
		
		for(int t=timeout; t>0; t+=-millisToWait) {
			try {
				shouldContainText(text)
				return;
			} catch(Throwable th){}
			
			Thread.sleep(millisToWait)
		}
		
		//never saw the text!
		println page.asText()
		throw new AssertionError(potentialError + page.asText())
	}
	
    public boolean containsText(String text){
        return page.asText().contains(text)
    }
    
	PageDriver shouldContainText(String text) {
		assertTrue("Expected '$text' in '${page.asText()}'", containsText(text))
		return this
	}
	
	public List<DomNode> findElementsByXPath(String xPath) {
		return (List<DomNode>) page.getByXPath(xPath)
	}
	
	public HtmlPage getPage(){
		return page
	}

}
