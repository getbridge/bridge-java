package com.flotype.bridge;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.*;

public class JSONCodecTest {

	private static Bridge bridge;

	@Before
	public void setUp() {
		bridge = mock(Bridge.class);
	}

	@Test
	public void testCreateSEND() {
		fail("Not yet implemented");
	}

	@Test
	public void testCreateJWP() {
		String noCallbackJSON = JSONCodec.createJWP(bridge, "FOO", null);
		assertEquals(
				"{\"command\":\"JOINWORKERPOOL\",\"data\":{\"name\":\"FOO\"}}",
				noCallbackJSON);
	}

	@Test
	public void testCreateGETCHANNEL() {
		fail("Not yet implemented");
	}

	@Test
	public void testCreateLEAVECHANNEL() {
		fail("Not yet implemented");
	}

	@Test
	public void testCreateJC() {
		fail("Not yet implemented");
	}

	@Test
	public void testCreateCONNECT() {
		fail("Not yet implemented");
	}

	@Test
	public void testParseRedirector() {
		fail("Not yet implemented");
	}

}
