package com.davidron.qunitTestDriver

import org.junit.Test

class JettyServerTest {
	static final Integer[] GOOD_PORT_RANGE = [BAD_PORT, 8090, 9080, 2012, 9911];
	static final Integer BAD_PORT = 99999999;
	
	
	@Test (expected=Exception.class)
	void noAvailablePortsThrowsAPortNotFoundException(){
		new JettyServer("", 99999999);
	}
	
	@Test (expected=Exception.class)
	void oneOfThesePortsShouldWork(){
		JettyServer server = new JettyServer("", GOOD_PORT_RANGE);
		assertNotEquals(server.getPort(), BAD_PORT)
	}


}
