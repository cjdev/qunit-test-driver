package com.googlecode.qunitTestDriver

import static org.junit.Assert.assertTrue
import static org.junit.Assert.assertFalse
import org.junit.Test
import static org.junit.Assert.assertEquals

class JettyServerTest {
	private static final List<Integer> GOOD_PORT_RANGE = [BAD_PORT, 8090, 9080, 2012, 9911];
	private static final Integer BAD_PORT = 99999999;
	
	
	@Test(expected=RuntimeException.class)
	void noAvailablePortsThrowsAPortNotFoundException(){
		new JettyServer("", 99999999).start();
	}
	
	@Test
	void oneOfThesePortsShouldWork(){
		JettyServer server = new JettyServer([:], GOOD_PORT_RANGE).start();
		
		assertFalse(server.getPort() == BAD_PORT)
		assertTrue(GOOD_PORT_RANGE.contains(server.getPort()))
	}

	@Test
	void canRunMainWithoutUserInput(){
		JettyServer server = JettyServer.createForMain(null).start();
		
        assertEquals(JettyServer.DEFAULT_PORT, server.port)
        assertEquals(["/" : [JettyServer.DEFAULT_SERVER_ROOT]], server.pathMappings)
	}

	@Test
	void canRunMainWithUserInput(){
		String[] args = ["9091", "/user=/usr", "/user=/home"]
		JettyServer server = JettyServer.createForMain(args).start();
		
        assertEquals(9091, server.port)
        assertEquals(["/" : ["./"], "/user" : ["/usr", "/home"]], server.pathMappings)
    }
}
