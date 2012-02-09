package com.googlecode.qunitTestDriver.config

import com.googlecode.qunitTestDriver.QUnitTestDriver;

/**
 * This class allows you to set which ports are used in the internal Jetty server.
 * The ports are tried one-by-one until one of them works.
 * This is nice for an environment where tests may be running in parallel on single machine.
 */
class RandomPortSet implements Configuration{

    def ports = [] as Set
    
    public RandomPortSet(size=100, minPort=8000, maxPort=9000){

        def r = new Random();
        def range = maxPort - minPort;
        
        while (ports.size() < size) {
            ports << (minPort + r.nextInt(range))
        }
    }

    void configure(QUnitTestDriver runner){
        runner.setPortSet(ports.toArray(new Integer[0]))
    }

}
