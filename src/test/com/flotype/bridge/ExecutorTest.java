package com.flotype.bridge;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import com.flotype.bridge.Executor;

public class ExecutorTest {

	private Executor executor;
	private Service service;

	public void testExecutor() {
		fail("Not yet implemented");
	}

	@Before
	public void setUp() {
		executor  = new Executor();
		service = new Service(){
			public void aMethod(){

			}
		};

		executor.addService("some service", service);
	}

	@Test
	public void testExecute() {		
		// No existent service

		// Nonexistent method

		// Service + method both exist
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

	public void testGetConformingMethod() {
		fail("Not yet implemented");

		// Argument types are same class as parameter type
		
		// Argument types inherit from parameter types
		
		// Argument types `implement` parameter interface
		
		// Method name exists, arg types have nothing to do with param types
		
		// No such method name
	}

}
