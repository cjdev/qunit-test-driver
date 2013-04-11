package com.cj.qunitTestDriver

import java.util.ArrayList

import com.gargoylesoftware.htmlunit.WebClient
import com.gargoylesoftware.htmlunit.html.DomNode
import com.gargoylesoftware.htmlunit.html.HtmlPage


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

	public boolean waitForTextToBePresent(text, Integer timeout){

		int millisToWait = 100

		for(int remaining=timeout; remaining>0; remaining -= millisToWait) {
                    if (containsText(text)) {
                        return true; 
                    }

                    Thread.sleep(millisToWait)
		}

                return false;
	}

	public boolean containsText(String text){
                try {
                    def element = ((DomNode)
                        page.getFirstByXPath(
                            "/html/body/p[@id=\"qunit-testresult\"]/text()"
                        ))

                        if(element != null) {
                            if( element.asText().contains(text) ) {
                                return true;
                            }
                        }
                        
                        
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
