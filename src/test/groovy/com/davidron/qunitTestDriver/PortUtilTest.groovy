package com.davidron.qunitTestDriver;

import static org.junit.Assert.*;

import org.junit.Test;

import com.davidron.qunitTestDriver.PortUtil;

class PortUtilTest {
	@Test
	public void appendsPortDiscriminatorProperly(){
		// given
		Properties props = new Properties();
		props.put("partition.port.prefix", "300");
		
		// when 
		int result = PortUtil.getPort(props, 33);
		
		// then	
		assertEquals(30098, result);
		
	}
	
	@Test
	public void fallsBackToDefaults(){
		// given
		Properties props = new Properties();
		
		// when
		int result = PortUtil.getPort(props, 33);
		
		// then
		assertEquals(33, result);
		
	}
	
	@Test
	public void handlesTheStringThatSaysNull(){
		// given
		Properties props = new Properties();
		props.put("partition.port.prefix", "null");
		
		// when
		int result = PortUtil.getPort(props, 33);
		
		// then
		assertEquals(33, result);
		
	}
}
