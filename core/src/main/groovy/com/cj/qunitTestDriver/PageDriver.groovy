package com.cj.qunitTestDriver

import java.util.ArrayList

import com.gargoylesoftware.htmlunit.WebClient
import com.gargoylesoftware.htmlunit.html.DomNode
import com.gargoylesoftware.htmlunit.html.HtmlPage
import static org.junit.Assert.assertTrue


class PageDriver {

	private final WebClient webClient
	private HtmlPage page

	public PageDriver(WebClient client, String url) {
            webClient = client
            page = (HtmlPage) webClient.getPage(url)
	}

	public PageDriver waitForAjax(duration=3000) {
		webClient.waitForBackgroundJavaScript(duration)
		return this;
	}

	public void waitForTextToBePresent(text, Integer timeout){
		String potentialError = "'"+text+"' didn't show up in "+timeout+" milliseconds."

		int millisToWait = 100

		for(int t=timeout; t>0; t+=-millisToWait) {
                    if (containsText(text)) {
                        return; 
                    }

                    Thread.sleep(millisToWait)
		}

		//never saw the text!
		println page.asText()
		throw new AssertionError(potentialError + page.asText())
	}

	public boolean containsText(String text){
                try {
                    return page.asText().contains(text);

                // There appears to be a bug in HTML Unit where it throws an NPE
                // when attempting convert the page to text. As far as we can 
                // determine, this situation is analagous to the text not existing 
                // so, rather than exploding, return false.
                //
                // See: http://sourceforge.net/p/htmlunit/bugs/891/#663c
                } catch (Throwable e) {
                    println("Exception `" + e.class + "` while converting the page to text.")
                    throw e
                    return false;
                }
	}

        public boolean containsText(ArrayList<String> list) {
            for( item in list ) {
                if (containsText(item)) {
                    return true;
                }
            }

            return false;
        }

	public List<DomNode> findElementsByXPath(String xPath) {
		return (List<DomNode>) page.getByXPath(xPath)
	}

	public HtmlPage getPage(){
		return page
	}

}
