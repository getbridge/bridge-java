package com.getbridge.bridge;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.getbridge.bridge.Bridge;
import com.getbridge.bridge.Reference;
import com.getbridge.bridge.Utils;

import static org.mockito.Mockito.*;

public class UtilsTest {

	static Bridge bridge;
	static Map<String, List<String>> refObj;
	private static Reference reference;

	static {
		bridge = mock(Bridge.class);

		refObj = new HashMap<String, List<String>>();
		refObj.put("ref", Arrays.asList("a", "b", "c", "d"));
		reference = new Reference(bridge, "a", "b", "c", "d",
				Arrays.asList(new String[] {}));
	}

	@Test
	public void testConstructRefs() {

		// Map with a ref inside
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("a", 1.0);
		map.put("b", refObj);

		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("a", 1.0);
		resultMap.put("b", reference);

		assertEquals(resultMap, Utils.constructRefs(bridge, map));

		// List with a ref inside
		List<Object> list = Arrays.asList(new Object[] { "a", "b", refObj });
		List<Object> resultList = Arrays.asList(new Object[] { "a", "b",
				reference });
		assertEquals(resultList, Utils.constructRefs(bridge, list));

		// List with null values
		List<Object> nullList = Arrays
				.asList(new Object[] { "a", "b", null, 2 });
		List<Object> resultNullList = Arrays.asList(new Object[] { "a", "b",
				null, 2 });
		assertEquals(resultNullList, nullList);

		// Map with a list inside containing ref
		Map<String, Object> nestMap = new HashMap<String, Object>();
		nestMap.put("a", 1.0);
		nestMap.put("b", list);

		Map<String, Object> resultNestMap = new HashMap<String, Object>();
		resultNestMap.put("a", 1.0);
		resultNestMap.put("b", resultList);

		assertEquals(resultNestMap, Utils.constructRefs(bridge, nestMap));

		// List with a map inside containing ref
		List<Object> nestList = Arrays.asList(new Object[] { "a", "b", map });
		List<Object> resultNestList = Arrays.asList(new Object[] { "a", "b",
				resultMap });
		assertEquals(resultNestList, Utils.constructRefs(bridge, nestList));
	}

	@Test
	public void testNormalizeObject() {
		// Test with string
		String s = "FOO";
		assertEquals(s, Utils.normalizeValue(s));

		// with array
		List<String> list = Arrays.asList("a", "b", "c", "d");
		assertEquals(list, Utils.normalizeValue(list));

		// with map
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("a", 1);
		map.put("c", "FOO");

		assertEquals(map, Utils.normalizeValue(map));

		// with integer
		assertEquals(1.0f, Utils.normalizeValue(1));

		// with float
		assertEquals(1.0f, Utils.normalizeValue(1.0f));

		// with double
		assertEquals(1.0f, Utils.normalizeValue(1.0));
	}

	@Test
	public void testIntToByteArray() {
		// Positive int
		byte[] posBytes = new byte[] { 0x00, 0x00, 0x00, 0x2a };
		assertArrayEquals(posBytes, Utils.intToByteArray(42));

		// Zero
		byte[] zeroBytes = new byte[] { (byte) 0x00, (byte) 0x00, (byte) 0x00,
				(byte) 0x00 };
		assertArrayEquals(zeroBytes, Utils.intToByteArray(0));

		// Negative int
		byte[] negBytes = new byte[] { (byte) 0xff, (byte) 0xff, (byte) 0xff,
				(byte) 0xf4 };
		assertArrayEquals(negBytes, Utils.intToByteArray(-12));
	}

	@Test
	public void testGetMethods() {
		Object a = new Object() {
			public void foo() {
			}

			public void bar() {
			}

			protected void baz() {
			}

			private void qux() {
			}
		};

		List<String> methods = Utils.getMethods(a.getClass());
		assertEquals(Arrays.asList("foo", "bar"), methods);
	}
}
