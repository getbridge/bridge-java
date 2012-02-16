package com.flotype.bridge;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class RequestTest {

	private Bridge b;
	private Reference reference;
	private List<Object> args;
	private List<Object> transformedArgs;
	private Request request;

	@Before
	public void setUp(){
		b = new Bridge();
		reference = new Reference(Arrays.asList(new String[]{"a", "b", "c", "d"}), b);

		// String, integer, float, double, boolean, map, list
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("thing", "thingy");

		args = Arrays.asList("a", 1, 3.14f, 6.18, true, map, Arrays.asList("foo", "bar", "baz"));

		transformedArgs = new ArrayList<Object>();
		for(Object arg : args) {
			transformedArgs.add(Utils.normalizeValue(arg));
		}

		request = new Request(reference, args);

	}

	@Test(expected=NullPointerException.class)
	public void testCtorWithNullList(){
		Request request = new Request(reference, null);
	}

	@Test
	public void testGetReference() {
		Request request = new Request(reference, args);

		Reference r2 = new Reference(Arrays.asList(new String[]{"a", "b", "c", "d"}), b);
		assertEquals(r2, request.getReference());
	}

	@Test
	public void testGetParameterList() {
		List<Class<?>> params = new ArrayList<Class<?>>();
		for(Object arg : args) {
			params.add(arg.getClass());
		}
		assertEquals(params, Arrays.asList(request.getParameterList()));
	}

	@Test
	public void testGetArguments() {
		assertEquals(transformedArgs, Arrays.asList(request.getArguments()));
	}

}
