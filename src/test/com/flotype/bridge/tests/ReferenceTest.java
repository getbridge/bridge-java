package com.flotype.bridge.tests;

import junit.framework.TestCase;

public class ReferenceTest extends TestCase {

	public void testReferenceListOfStringBridge() {
		// Array with 4 part pathchain
		
		// Array with 3 part pathchain
		
		// Array with null pathchain
		
		
	}
	
	public void testGetPathchain() {
		// Reference created from List<String> has pathchain .equals original
		
		// Reference created from other reference has pathchain .equals original
	}
	
	public void testGetRoutingPrefix() {
		
	}

	public void testGetRoutingId() {
		
	}
	
	public void testGetServiceName() {
		// Getting on a 3 part service
		
		// Getting on a 2 part service
		
	}
	
	public void testGetMethodName() {
		// Set method name on 4 part
		
		// Set method name on 3 part
		
		// Set method name on degenerate reference
		
	}
	
	public void testSetRoutingPrefix() {
		// Create reference. Set to something. Check via getter
	}

	public void testSetRoutingId() {
		// Create reference. Set to something. Check via getter

		
	}
	
	public void testSetServiceName() {
		// Create reference. Set to something. Check via getter

		
	}
	
	public void testSetMethodName() {
		// Set method name on 4 part
		
		// Set method name on 3 part
		
		// Set method name on degenerate reference
		
	}
	
	public void testToString() {
		// ToString value of reference is the same as JSON encoding it
	}
	
	public void testHashCode() {
		// hashcode of reference is same as hashcode of its tostring
		
		// .equal references have .equal hashcodes
	}
	
	public void testEquals() {
		// Ref from Ref .equals Ref from list
		
		// Ref via constructor .equals Ref with parts set by setters
		
	}
}
