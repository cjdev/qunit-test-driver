package com.cj.qunitTestDriver.junit;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.runner.RunWith;

import com.cj.qunitTestDriver.config.Configuration;

@RunWith(QUnitSuite.class)
public abstract class QUnitTest  {

    public final String testPageUrl;
    public final List<Configuration> configurations;
    
    public QUnitTest(String testPageUrl, Configuration ... configurations) {
        super();
        this.testPageUrl = testPageUrl;
        if(configurations==null){
            this.configurations = Collections.emptyList();
        }else{
            this.configurations = Arrays.asList(configurations);
        }
    }
}
