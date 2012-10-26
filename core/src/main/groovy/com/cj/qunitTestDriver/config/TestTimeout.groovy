package com.cj.qunitTestDriver.config

import com.cj.qunitTestDriver.QUnitTestDriver

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
