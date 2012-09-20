package com.googlecode.qunitTestDriver.config

import com.googlecode.qunitTestDriver.QUnitTestDriver;

/**
 * This class allows you to set which ports are used in the internal Jetty server.
 * The ports are tried one-by-one until one of them works.
 * This is nice for an environment where tests may be running in parallel on single machine.
 */
class RandomPortSet implements Configuration{
    List<Integer> ports = []
    
    public RandomPortSet(Integer size=100, Integer minPort=8000, Integer maxPort=9000){
        Random r = new Random();
        Integer range = maxPort - minPort;
        
        while (ports.size() < size) {
            ports << (minPort + r.nextInt(range))
        }
    }

    void configure(QUnitTestDriver runner){
        runner.setPortSet(ports)
    }

}
