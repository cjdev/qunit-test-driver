package com.googlecode.qunitTestDriver.config

import com.googlecode.qunitTestDriver.QUnitTestDriver

/**
 * Specifies the test timeout in milliseconds
 */
class TestTimeout implements Configuration{
    Integer timeout;

    public TestTimeout(Integer timeout){
        this.timeout = timeout;
    }

    void configure(QUnitTestDriver runner){
        runner.timeout = timeout;
    }

}
