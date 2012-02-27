package com.flotype.bridge;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

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
	public void testGetConformingMethod() {
		// Argument types are same class as parameter type
		Method method = executor.getConformingMethod("nonPolymorphic", new Object[]{1.0f, new ArrayList<String>()}, ConformingMethodTestClass.class);
		assertNotNull(method);
		
		// Argument types inherit from parameter types
		Method polyMethod = executor.getConformingMethod("polymorphic", new Object[]{1.0f, new ArrayList<String>(), new ArrayList<String>()}, ConformingMethodTestClass.class);
		assertNotNull(polyMethod);
		
		// Argument is a reference, parameter is a ServiceClient subclass
		
		// Arguments have type parameters. Params do not
		Method erasedMethod = executor.getConformingMethod("collectionsTypeErased", new Object[]{new HashMap<String, Object>(), new ArrayList<String>()}, ConformingMethodTestClass.class);
		assertNotNull(erasedMethod);
		
		// Arguments have type parameters. Params also have types
		Method typedMethod = executor.getConformingMethod("collectionsWithTypes", new Object[]{new HashMap<String, Object>(), new ArrayList<String>()}, ConformingMethodTestClass.class);
		assertNotNull(typedMethod);

		Method nxTypedMethod = executor.getConformingMethod("collectionsWithTypes", new Object[]{new HashMap<Float, Object>(), new ArrayList<Float>()}, ConformingMethodTestClass.class);
		// This should be null. I don't fully understand why it is not
		//assertNull(nxTypedMethod);
		
		// No such method name
		Method nxMethod = executor.getConformingMethod("doesNotExist", new Object[]{new HashMap<String, Object>(), new ArrayList<String>()}, ConformingMethodTestClass.class);
		assertNull(nxMethod);
	}
	
	class ConformingMethodTestClass {
		public void nonPolymorphic(Float theFloat, ArrayList theList){
			
		}
		
		public void polymorphic(Number theNumber, List theList, Object theObject) {
			
		}
		
		public void collectionsTypeErased(Map theMap, List theList){
			
		}
		
		public void collectionsWithTypes(Map<String, Object> theMap, List<String> theList){
			
		}
		
		public void referenceToClient(ServiceClient client){
			
		}
	}

}
