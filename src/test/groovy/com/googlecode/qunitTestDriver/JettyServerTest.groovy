package com.googlecode.qunitTestDriver

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertTrue
import static org.junit.Assert.assertFalse
import static org.junit.Assert.fail
import org.junit.Test

class JettyServerTest {
	static final List<Integer> GOOD_PORT_RANGE = [BAD_PORT, 8090, 9080, 2012, 9911];
	static final Integer BAD_PORT = 99999999;
	
	
	@Test (expected=RuntimeException.class)
	void noAvailablePortsThrowsAPortNotFoundException(){
		new JettyServer("", 99999999);
	}
	
	@Test
	void oneOfThesePortsShouldWork(){
		JettyServer server = new JettyServer("", GOOD_PORT_RANGE as Integer[]);
		assertFalse(server.getPort() == BAD_PORT)
		assertTrue(GOOD_PORT_RANGE.contains(server.getPort()))
	}


}
