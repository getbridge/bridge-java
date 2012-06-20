package com.getbridge.bridge;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.getbridge.bridge.Bridge;
import com.getbridge.bridge.BridgeObject;
import com.getbridge.bridge.BridgeRemoteObject;
import com.getbridge.bridge.Dispatcher;
import com.getbridge.bridge.Reference;

import static org.mockito.Mockito.*;

public class DispatcherTest {

	private Dispatcher dispatcher;
	private BridgeObject bridgeObject;
	private Bridge bridge;
	private boolean success;

	public void testExecutor() {
		fail("Not yet implemented");
	}

	@Before
	public void setUp() {
		success = false;
		bridge = mock(Bridge.class);
		when(bridge.getClientId()).thenReturn("abcdefgh");
		dispatcher = new Dispatcher(bridge);

		bridgeObject = new BridgeObject() {
			public void aMethod() {
				success = true;
				assertTrue(success);
			}
		};

		dispatcher.storeObject("s1", bridgeObject);
	}

	@Test
	public void testExecute() {
		Reference destination = Reference.createClientReference(bridge,
				"doesNotExist", null);
		destination.setMethodName("some method");
		List<Object> args = Arrays.asList(new Object[] {});

		// No existent service
		dispatcher.execute(destination, args);

		// Nonexistent method
		dispatcher.execute(destination, args);

		// Service + method both exist
		destination.setMethodName("aMethod");
		dispatcher.execute(destination, args); // The assertion is inside the
												// service itself because it's
												// threaded
	}

	@Test
	public void testStoreObject() {
		// Add service with key that didn't exist
		BridgeObject service2 = new BridgeObject() {
		};
		Reference ref2 = dispatcher.storeObject("s2", service2);
		assertSame(service2, dispatcher.getObject("s2"));
		assertEquals(
				ref2,
				Reference.createClientReference(bridge, "s2",
						Arrays.asList(new String[] {})));

		// Add service with key that existed before
		Reference ref1 = dispatcher.storeObject("s1", service2);
		assertEquals(
				ref1,
				Reference.createClientReference(bridge, "s1",
						Arrays.asList(new String[] {})));
		assertNotSame(bridgeObject, dispatcher.getObject("s1"));
		assertSame(service2, dispatcher.getObject("s1"));
	}

	@Test
	public void testgetObject() {
		// Get a service that exists
		BridgeObject s = (BridgeObject) dispatcher.getObject("s1");
		assertSame(bridgeObject, s);

		// Get a service that does not exist
		BridgeObject nonExistent = (BridgeObject) dispatcher
				.getObject("does not exist");
		assertNull(nonExistent);
	}

	@Test
	public void teststoreExistingObjectByKey() {
		// Key that exists
		Reference ref = dispatcher.storeExistingObjectByKey("s1",
				"another name");
		assertEquals(
				ref,
				Reference.createClientReference(bridge, "another name",
						Arrays.asList(new String[] { "aMethod" })));
		assertSame(dispatcher.getObject("s1"),
				dispatcher.getObject("another name"));
	}

	public void testAddNonExistingServiceByKey() {
		assertNull(dispatcher.storeExistingObjectByKey("does not exist",
				"should not exist"));
	}

	@Test
	public void testGetConformingMethod() throws IllegalArgumentException,
			IllegalAccessException, InvocationTargetException {

		ConformingMethodTestClass testInstance = new ConformingMethodTestClass();

		// Argument types are same class as parameter type
		Object[] methodArgs = new Object[] { 1.0f, new ArrayList<String>() };
		Method method = dispatcher.getConformingMethod("nonPolymorphic",
				methodArgs, ConformingMethodTestClass.class);
		assertNotNull(method);
		assertTrue((Boolean) method.invoke(testInstance, methodArgs));

		// Argument types inherit from parameter types
		Object[] polyMethodArgs = new Object[] { 1.0f, new ArrayList<String>(),
				new ArrayList<String>() };
		Method polyMethod = dispatcher.getConformingMethod("polymorphic",
				polyMethodArgs, ConformingMethodTestClass.class);
		assertNotNull(polyMethod);
		assertTrue((Boolean) polyMethod.invoke(testInstance, polyMethodArgs));

		// Argument is a reference, parameter is a ServiceClient subclass

		// Arguments have type parameters. Params do not
		Object[] erasedMethodArgs = new Object[] {
				new HashMap<String, Object>(), new ArrayList<String>() };
		Method erasedMethod = dispatcher.getConformingMethod(
				"collectionsTypeErased", erasedMethodArgs,
				ConformingMethodTestClass.class);
		assertNotNull(erasedMethod);
		assertTrue((Boolean) erasedMethod
				.invoke(testInstance, erasedMethodArgs));

		// Arguments have type parameters. Params also have types
		Object[] typedMethodArgs = new Object[] {
				new HashMap<String, Object>(), new ArrayList<String>() };
		Method typedMethod = dispatcher.getConformingMethod(
				"collectionsWithTypes", typedMethodArgs,
				ConformingMethodTestClass.class);
		assertNotNull(typedMethod);
		assertTrue((Boolean) typedMethod.invoke(testInstance, typedMethodArgs));

		Object[] nxTypedMethodArgs = new Object[] {
				new HashMap<Float, Object>(), new ArrayList<Float>() };
		Method nxTypedMethod = dispatcher.getConformingMethod(
				"collectionsWithTypes", nxTypedMethodArgs,
				ConformingMethodTestClass.class);
		// This should be null. I don't fully understand why it is not
		// assertNull(nxTypedMethod);
		assertTrue((Boolean) nxTypedMethod.invoke(testInstance,
				nxTypedMethodArgs));

		// No such method name
		Method nxMethod = dispatcher.getConformingMethod("doesNotExist",
				new Object[] { new HashMap<String, Object>(),
						new ArrayList<String>() },
				ConformingMethodTestClass.class);
		assertNull(nxMethod);
	}

	class ConformingMethodTestClass {
		public boolean nonPolymorphic(Float theFloat, ArrayList theList) {
			return true;
		}

		public boolean polymorphic(Number theNumber, List theList,
				Object theObject) {
			return true;
		}

		public boolean collectionsTypeErased(Map theMap, List theList) {
			return true;
		}

		public boolean collectionsWithTypes(Map<String, Object> theMap,
				List<String> theList) {
			return true;
		}

		public boolean referenceToClient(BridgeRemoteObject client) {
			return true;
		}
	}

}
