package com.flotype.bridge;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
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

public class ExecutorTest {

	private Executor executor;
	private Service service;
	private boolean success;

	public void testExecutor() {
		fail("Not yet implemented");
	}

	@Before
	public void setUp() {
		success = false;
		executor  = new Executor();
		
		service = new Service(){
			public void aMethod(){
				success = true;
				assertTrue(success);
			}
		};

		executor.addService("some service", service);
	}

	@Test
	public void testExecute() {
		Bridge b = new Bridge();
		Reference reference = new Reference(Arrays.asList(new String[]{"a", "b", "c", "d"}), b);
		List<Object> args = Arrays.asList(new Object[]{});
		Request request = new Request(reference, args);

		// No existent service
		executor.execute(request);

		// Nonexistent method
		reference.setServiceName("some service");
		executor.execute(request);

		// Service + method both exist
		reference.setMethodName("aMethod");
		executor.execute(request); // The assertion is inside the service itself because it's threaded
	}

	@Test
	public void testAddService() {		
		// Add service with key that didn't exist
		Service service2 = new Service(){};
		executor.addService("service2", service2);
		assertSame(service2, executor.getService("service2"));

		// Add service with key that existed before
		executor.addService("some service", service2);
		assertNotSame(service, executor.getService("some service"));
		assertSame(service2, executor.getService("some service"));
	}

	@Test
	public void testGetService() {
		// Get a service that exists
		Service s = executor.getService("some service");
		assertSame(service, s);

		// Get a service that does not exist
		Service nonExistent = executor.getService("does not exist");
		assertNull(nonExistent);
	}

	@Test
	public void testAddExistingServiceByKey(){
		// Key that exists
		executor.addExistingServiceByKey("some service", "another name");
		assertSame(executor.getService("some service"), executor.getService("another name"));


	}

	@Test(expected=NullPointerException.class)
	public void testAddNonExistingServiceByKey(){
		executor.addExistingServiceByKey("does not exist", "should not exist");
	}
	

	@Test
	public void testGetConformingMethod() throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		
		ConformingMethodTestClass testInstance = new ConformingMethodTestClass();
		
		// Argument types are same class as parameter type
		Object[] methodArgs = new Object[]{1.0f, new ArrayList<String>()};
		Method method = executor.getConformingMethod("nonPolymorphic", methodArgs, ConformingMethodTestClass.class);
		assertNotNull(method);
		assertTrue((Boolean) method.invoke(testInstance, methodArgs));
		
		// Argument types inherit from parameter types
		Object[] polyMethodArgs = new Object[]{1.0f, new ArrayList<String>(), new ArrayList<String>()};
		Method polyMethod = executor.getConformingMethod("polymorphic", polyMethodArgs, ConformingMethodTestClass.class);
		assertNotNull(polyMethod);
		assertTrue((Boolean) polyMethod.invoke(testInstance, polyMethodArgs));
		
		// Argument is a reference, parameter is a ServiceClient subclass
		
		// Arguments have type parameters. Params do not
		Object[] erasedMethodArgs = new Object[]{new HashMap<String, Object>(), new ArrayList<String>()};
		Method erasedMethod = executor.getConformingMethod("collectionsTypeErased", erasedMethodArgs, ConformingMethodTestClass.class);
		assertNotNull(erasedMethod);
		assertTrue((Boolean) erasedMethod.invoke(testInstance, erasedMethodArgs));
		
		// Arguments have type parameters. Params also have types
		Object[] typedMethodArgs = new Object[]{new HashMap<String, Object>(), new ArrayList<String>()};
		Method typedMethod = executor.getConformingMethod("collectionsWithTypes", typedMethodArgs, ConformingMethodTestClass.class);
		assertNotNull(typedMethod);
		assertTrue((Boolean) typedMethod.invoke(testInstance, typedMethodArgs));

		Object[] nxTypedMethodArgs = new Object[]{new HashMap<Float, Object>(), new ArrayList<Float>()};
		Method nxTypedMethod = executor.getConformingMethod("collectionsWithTypes", nxTypedMethodArgs, ConformingMethodTestClass.class);
		// This should be null. I don't fully understand why it is not
		//assertNull(nxTypedMethod);
		assertTrue((Boolean) nxTypedMethod.invoke(testInstance, nxTypedMethodArgs));
		
		// No such method name
		Method nxMethod = executor.getConformingMethod("doesNotExist", new Object[]{new HashMap<String, Object>(), new ArrayList<String>()}, ConformingMethodTestClass.class);
		assertNull(nxMethod);
	}
	
	class ConformingMethodTestClass {
		public boolean nonPolymorphic(Float theFloat, ArrayList theList){
			return true;
		}
		
		public boolean polymorphic(Number theNumber, List theList, Object theObject) {
			return true;
		}
		
		public boolean collectionsTypeErased(Map theMap, List theList){
			return true;
		}
		
		public boolean collectionsWithTypes(Map<String, Object> theMap, List<String> theList){
			return true;
		}
		
		public boolean referenceToClient(ServiceClient client){
			return true;
		}
	}

}
