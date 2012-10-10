package com.cj.qunitTestDriver.config

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertTrue
import org.junit.Test

import com.cj.qunitTestDriver.config.RandomPortSet;

class RandomPortSetTest {

    @Test void defaultSettings(){
        def ports = new RandomPortSet().ports.sort()
        assertEquals(100, ports.size())
        assertTrue( ports[0] >= 8000 )
        assertTrue( ports[99] < 9000 )
    }

    @Test void customSize(){
        def ports = new RandomPortSet(25).ports.sort()
        assertEquals(25, ports.size())
    }

    @Test void customRange(){
        def ports = new RandomPortSet(25,10000,11000).ports.sort()
        assertEquals(25, ports.size())
        assertTrue( ports[0] >= 10000 )
        assertTrue( ports[24] < 11000 )
    }
}
