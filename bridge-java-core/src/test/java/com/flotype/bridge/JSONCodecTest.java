package com.flotype.bridge;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.Version;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.module.SimpleModule;
import org.codehaus.jackson.type.TypeReference;
import org.junit.Before;
import org.junit.Test;

public class JSONCodecTest {

	private static Bridge bridge;
	private static ObjectMapper mapper;
	private static Reference serviceRef, channelRef, localRef;

	@Before
	public void setUp() {
		List<String> emptyList = new ArrayList<String>();
		
		bridge = mock(Bridge.class);
		Dispatcher dispatcher = mock(Dispatcher.class);
		bridge.dispatcher = dispatcher;
		
		when(bridge.getClientId()).thenReturn("abcdefgh");

		mapper = new ObjectMapper();
		SimpleModule module = new SimpleModule("Handler", new Version(0, 1, 0,
				"alpha"));
		module.addSerializer(new ReferenceSerializer(bridge, Reference.class))
				.addSerializer(
						new BridgeObjectSerializer(bridge, BridgeObject.class))
				.addSerializer(
						new BridgeRemoteObjectSerializer(bridge,
								BridgeRemoteObject.class));
		mapper.registerModule(module);

		serviceRef = Reference.createServiceReference(bridge, "SERVICE",
				emptyList);
		localRef = Reference.createClientReference(bridge, "RANDOM", emptyList);
		channelRef = Reference.createChannelReference(bridge, "CHANNEL",
				emptyList);
		
		when(dispatcher.storeRandomObject(anyObject())).thenReturn(localRef);
	}

	public void testCreateSEND() {
		fail("Not yet implemented");
	}

	@Test
	public void testCreateJWP() throws JsonParseException,
			JsonMappingException, IOException {
		String genNoCallback = JSONCodec.createJWP(bridge, "FOO", null);
		String specNoCallback = "{\"command\":\"JOINWORKERPOOL\",\"data\":{\"name\":\"FOO\"}}";

		Map<String, Object> specObjNoCallback = mapper.readValue(
				specNoCallback, new TypeReference<Map<String, Object>>() {
				});

		Map<String, Object> genObjNoCallback = mapper.readValue(genNoCallback,
				new TypeReference<Map<String, Object>>() {
				});
		assertEquals(specObjNoCallback, genObjNoCallback);

		String gen = JSONCodec.createJWP(bridge, "FOO", localRef);
		String spec = "{\"command\":\"JOINWORKERPOOL\",\"data\":{\"name\":\"FOO\", \"callback\":{\"ref\":[\"client\", \"abcdefgh\", \"RANDOM\"], \"operations\":[]}}}";

		Map<String, Object> specObj = mapper.readValue(spec,
				new TypeReference<Map<String, Object>>() {
				});

		Map<String, Object> genObj = mapper.readValue(gen,
				new TypeReference<Map<String, Object>>() {
				});
		assertEquals(specObj, genObj);
	}

	@Test
	public void testCreateGETCHANNEL() throws JsonParseException, JsonMappingException, IOException {
		String gen = JSONCodec.createGETCHANNEL(bridge, "CHANNEL");
		String spec= "{\"command\":\"GETCHANNEL\",\"data\":{\"name\":\"CHANNEL\"}}";

		Map<String, Object> specObj = mapper.readValue(spec,
				new TypeReference<Map<String, Object>>() {
				});

		Map<String, Object> genObj = mapper.readValue(gen,
				new TypeReference<Map<String, Object>>() {
				});
		assertEquals(specObj, genObj);
	}

	public void testCreateLEAVECHANNEL() {
		fail("Not yet implemented");
	}

	@Test
	public void testCreateJC() throws JsonParseException, JsonMappingException, IOException {
		String gen = JSONCodec.createJC(bridge, "CHANNEL", localRef, true, localRef);
		String spec = "{\"command\":\"JOINCHANNEL\",\"data\":{\"name\":\"CHANNEL\",\"writeable\":true,\"handler\":{\"ref\":[\"client\",\"abcdefgh\",\"RANDOM\"],\"operations\":[]},\"callback\":{\"ref\":[\"client\",\"abcdefgh\",\"RANDOM\"],\"operations\":[]}}}";
		Map<String, Object> specObj = mapper.readValue(spec,
				new TypeReference<Map<String, Object>>() {
		});

		Map<String, Object> genObj = mapper.readValue(gen,
				new TypeReference<Map<String, Object>>() {
		});
		assertEquals(specObj, genObj);
	}

	@Test
	public void testCreateCONNECT() throws JsonParseException, JsonMappingException, IOException {
		String gen = JSONCodec.createCONNECT(bridge, null, null, "abcdefgh");
		String spec = "{\"command\":\"CONNECT\",\"data\":{\"api_key\":\"abcdefgh\",\"session\":[null,null]}}";
		Map<String, Object> specObj = mapper.readValue(spec,
				new TypeReference<Map<String, Object>>() {
		});

		Map<String, Object> genObj = mapper.readValue(gen,
				new TypeReference<Map<String, Object>>() {
		});
		assertEquals(specObj, genObj);

		gen = JSONCodec.createCONNECT(bridge, "foo", "bar", "abcdefgh");
		spec = "{\"command\":\"CONNECT\",\"data\":{\"api_key\":\"abcdefgh\",\"session\":[\"foo\", \"bar\"]}}";
		specObj = mapper.readValue(spec,
				new TypeReference<Map<String, Object>>() {
		});

		genObj = mapper.readValue(gen,
				new TypeReference<Map<String, Object>>() {
		});
		assertEquals(specObj, genObj);
	}

	public void testParseRedirector() {
		fail("Not yet implemented");
	}

}
