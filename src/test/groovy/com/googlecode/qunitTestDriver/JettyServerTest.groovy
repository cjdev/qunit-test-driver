package com.googlecode.qunitTestDriver

import static org.junit.Assert.assertTrue
import static org.junit.Assert.assertFalse
import org.junit.Test
import static org.junit.Assert.assertEquals

class JettyServerTest {
	static final List<Integer> GOOD_PORT_RANGE = [BAD_PORT, 8090, 9080, 2012, 9911];
	static final Integer BAD_PORT = 99999999;
	
	
	@Test (expected=RuntimeException.class)
	void noAvailablePortsThrowsAPortNotFoundException(){
		new JettyServer("", 99999999).start();
	}
	
	@Test
	void oneOfThesePortsShouldWork(){
		JettyServer server = new JettyServer("", GOOD_PORT_RANGE as Integer[]).start();
		assertFalse(server.getPort() == BAD_PORT)
		assertTrue(GOOD_PORT_RANGE.contains(server.getPort()))
	}

	@Test
	void canRunMainWithoutUserInput(){
		def server = JettyServer.createForMain(null).start();
        assertEquals(JettyServer.DEFAULT_PORT, server.getPort())
        assertEquals(JettyServer.DEFAULT_SERVER_ROOT, server.getRoot())
	}

	@Test
	void canRunMainWithUserInput(){
		def server = JettyServer.createForMain("9091 /usr").start();
        assertEquals(9091, server.getPort())
        assertEquals("/usr", server.getRoot())
    }
}
