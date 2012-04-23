package com.flotype.bridge;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class ReferenceTest {

	Bridge bridge;

	String destinationType = "a";
	String destinationId = "b";
	String objectId = "c";
	String methodName = "d";

	private List<String> operations;
	private Reference reference;

	private Reference ref3;

	@Before
	public void setUp() {
		bridge = mock(Bridge.class);
		when(bridge.getClientId()).thenReturn("abcdefgh");

		operations = Arrays.asList(new String[] { "foo", "bar" });
		reference = new Reference(bridge, destinationType, destinationId,
				objectId, methodName, operations);

		ref3 = new Reference(bridge, destinationType, destinationId, objectId,
				null, operations);
	}

	public void testgetDestinationType() {
		assertEquals(reference.getDestinationType(), destinationType);
		assertEquals(ref3.getDestinationType(), destinationType);
	}

	public void testgetDestinationId() {
		assertEquals(reference.getDestinationId(), destinationId);
		assertEquals(ref3.getDestinationId(), destinationId);
	}

	@Test
	public void testgetObjectId() {
		assertEquals(reference.getObjectId(), objectId);

		// Getting on a 3 part service
		assertEquals(ref3.getObjectId(), objectId);

	}

	@Test
	public void testGetMethodName() {
		// Set method name on 4 part
		assertEquals(reference.getMethodName(), methodName);

		// Set method name on 3 part
		assertNull(ref3.getMethodName());
	}

	@Test
	public void testsetDestinationType() {
		// Create reference. Set to something. Check via getter
		reference.setDestinationType("foobar");
		assertEquals(reference.getDestinationType(), "foobar");
	}

	@Test
	public void testsetDestinationId() {
		// Create reference. Set to something. Check via getter
		reference.setDestinationId("foobar");
		assertEquals(reference.getDestinationId(), "foobar");

	}

	@Test
	public void testsetObjectId() {
		// Create reference. Set to something. Check via getter
		reference.setObjectId("foobar");
		assertEquals(reference.getObjectId(), "foobar");
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
	public void testHashCode() {
		// hashcode of reference is same as hashcode of its tostring
		Reference newRef = new Reference(bridge, destinationType,
				destinationId, objectId, methodName, operations);
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
		Reference setterRef = new Reference(bridge, null, null, null, null,
				null);
		setterRef.setDestinationType(destinationType);
		setterRef.setDestinationId(destinationId);
		setterRef.setObjectId(objectId);
		setterRef.setMethodName(methodName);

		assertEquals(reference, setterRef);
	}

	@Test
	public void testProxy() {
		Reference methodReference = new Reference(reference);
		methodReference.setMethodName("add");

		List a = Utils.createProxy(reference, List.class);
		a.add(1);
		verify(bridge).send(methodReference, new Object[] { 1 });
	}
}
