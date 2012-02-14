package com.flotype.bridge;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class ReferenceTest  {
	
	Bridge bridge;
	
	String routingPrefix = "a";
	String routingId = "b";
	String serviceName = "c";
	String methodName = "d";
	
	private List<String> pathchain;
	private Reference reference;
	
	private List<String> path3;
	private Reference ref3;
	
	@Before
	public void setUp() {
		bridge = new Bridge();
		
		pathchain = Arrays.asList(new String[]{routingPrefix, routingId, serviceName, methodName});
		reference = new Reference(pathchain, bridge);
		
		
		path3 = Arrays.asList(new String[]{routingPrefix, routingId, serviceName});
		ref3 = new Reference(path3, bridge);
	}

	
	@Test(expected=IllegalArgumentException.class)
	public void testReferenceWithTooSmallList() {
		// Array with too few elements
		List<String> tooFewPath = Arrays.asList(new String[]{});
		Reference tooFewRef = new Reference(tooFewPath, bridge);
	}
	
	@Test
	public void testReferenceWithNullPathchain(){
		// Array with null pathchain
		Reference refNull = new Reference(null, bridge);
		assertEquals("client", refNull.getRoutingPrefix());
	}
	
	@Test
	public void testGetPathchain() {
		// Reference created from List<String> has pathchain .equals original
		assertEquals(pathchain, reference.getPathchain());
		
		// Reference created from other reference has pathchain .equals original
		Reference newRef = new Reference(reference);
		assertEquals(reference.getPathchain(), newRef.getPathchain());
	}
	
	public void testGetRoutingPrefix() {
		assertEquals(reference.getRoutingPrefix(), routingPrefix);
		assertEquals(ref3.getRoutingPrefix(), routingPrefix);
	}

	public void testGetRoutingId() {
		assertEquals(reference.getRoutingId(), routingId);
		assertEquals(ref3.getRoutingId(), routingId);
	}
	
	@Test
	public void testGetServiceName() {
		assertEquals(reference.getServiceName(), serviceName);
		
		// Getting on a 3 part service
		assertEquals(ref3.getServiceName(), serviceName);

	}
	
	@Test
	public void testGetMethodName() {
		// Set method name on 4 part
		assertEquals(reference.getMethodName(), methodName);
		
		// Set method name on 3 part
		assertNull(ref3.getMethodName());
	}
	
	@Test
	public void testSetRoutingPrefix() {
		// Create reference. Set to something. Check via getter
		reference.setRoutingPrefix("foobar");
		assertEquals(reference.getRoutingPrefix(), "foobar");
	}

	@Test
	public void testSetRoutingId() {
		// Create reference. Set to something. Check via getter
		reference.setRoutingId("foobar");
		assertEquals(reference.getRoutingId(), "foobar");
		
	}
	
	@Test
	public void testSetServiceName() {
		// Create reference. Set to something. Check via getter
		reference.setServiceName("foobar");
		assertEquals(reference.getServiceName(), "foobar");
	}
	
	@Test
	public void testSetMethodName() {
		// Set method name on 4 part
		reference.setMethodName("foobar");
		assertEquals(reference.getMethodName(), "foobar");
		
		// Set method name on 3 part
		ref3.setMethodName("foobar");
		assertEquals(ref3.getMethodName(), "foobar");
	}
	
	@Test
	public void testToString() {
		// ToString value of reference is the same as JSON encoding it
		
	}
	
	@Test
	public void testHashCode() {
		// hashcode of reference is same as hashcode of its tostring
		Reference newRef = new Reference(pathchain, bridge);
		assertEquals(reference, newRef);
		
		// .equal references have .equal hashcodes
		assertEquals(reference.hashCode(), newRef.hashCode());
	}
	
	@Test
	public void testEquals() {
		// Ref from Ref .equals Ref from list
		Reference newRef = new Reference(reference);
		assertEquals(reference, newRef);
		
		// Ref via constructor .equals Ref with parts set by setters
		Reference setterRef = new Reference(null, bridge);
		setterRef.setRoutingPrefix(routingPrefix);
		setterRef.setRoutingId(routingId);
		setterRef.setServiceName(serviceName);
		setterRef.setMethodName(methodName);
		
		assertEquals(reference, setterRef);
	}
}
