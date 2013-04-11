package com.cj.qunitTestDriver

import java.util.logging.Logger
import java.util.logging.Level

import com.gargoylesoftware.htmlunit.IncorrectnessListener
import com.gargoylesoftware.htmlunit.javascript.JavaScriptErrorListener
import com.gargoylesoftware.htmlunit.html.HTMLParserListener
import com.gargoylesoftware.htmlunit.SilentCssErrorHandler
import com.gargoylesoftware.htmlunit.WebClient
import com.gargoylesoftware.htmlunit.BrowserVersion

public class WebClientFactory {
    public WebClient getWebClient(BrowserVersion browserVersion) {
        def client = new WebClient(browserVersion)
            client.getOptions().setUseInsecureSSL(true)
        // turn off chatty htmlunit - TODO: make configurable
        return silenceHTMLUnit(client)
    }

    private WebClient silenceHTMLUnit(WebClient webClient) {
        // silence all html unit related errors
        // this is a qunit runner, not a html/css/js/http validator

        Logger.getLogger("com.gargoylesoftware.htmlunit").setLevel(Level.OFF)
            Logger.getLogger("org.apache.commons.httpclient").setLevel(Level.OFF)

            webClient.setIncorrectnessListener([
                    notify: {a, b -> null}
                    ] as IncorrectnessListener)

            webClient.setJavaScriptErrorListener([
                    timeoutError: {a, b, c -> null},
                    scriptException: {a, b -> null},
                    malformedScriptURL: {a, b, c -> null},
                    loadScriptError: {a, b, c -> null}
                    ] as JavaScriptErrorListener)

            webClient.setHTMLParserListener([
                    warning: {a, b, c, d, e, f -> null},
                    error: {a, b, c, d, e, f -> null}
                    ] as HTMLParserListener);

        webClient.setCssErrorHandler(new SilentCssErrorHandler())

            webClient.setThrowExceptionOnFailingStatusCode(false)
            webClient.setThrowExceptionOnScriptError(false)

            return webClient

    }
}
