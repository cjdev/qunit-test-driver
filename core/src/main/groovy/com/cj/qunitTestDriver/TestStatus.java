package com.cj.qunitTestDriver;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.gargoylesoftware.htmlunit.html.DomNode;

public class TestStatus {
    public final String name;
    public final String moduleName;
    public final Boolean passed;
    public final Boolean failed;
    public final Boolean isRunning;
    public final Integer assertionsPassed;
    public final Integer assertionsFailed;
    public final List<String> failures;
    
    public TestStatus(String name, String moduleName, DomNode node) {
        super();
        this.name = name;
        this.moduleName = moduleName;
        
        String testNodeClass = node.getAttributes().getNamedItem("class").getNodeValue();
        passed = "pass".equals(testNodeClass);
        failed = "fail".equals(testNodeClass);
        isRunning = "running".equals(testNodeClass);
        

        assertionsPassed = Integer.valueOf(((DomNode)node.getFirstByXPath(".//*[@class='counts']//*[@class='passed']")).asText());
        assertionsFailed = Integer.valueOf(((DomNode)node.getFirstByXPath(".//*[@class='counts']//*[@class='failed']")).asText());
        
        List<DomNode> failedAssertionNodes = (List<DomNode>) node.getByXPath(".//li[@class='fail']");

        List<String> failures = new ArrayList<String>();
        for(DomNode next : failedAssertionNodes){
          failures.add(next.asText());
        }
        
        this.failures = Collections.unmodifiableList(failures);
    }
    
}
