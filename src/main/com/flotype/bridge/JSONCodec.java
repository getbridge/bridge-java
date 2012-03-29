package com.flotype.bridge;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.Version;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.module.SimpleModule;
import org.codehaus.jackson.type.TypeReference;

class JSONCodec {

	public static String createSEND(Bridge bridge, Reference destination, Object[] args) {
		// Format: {destination: BRIDGEREF , args: [...]}
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("destination", destination);
		data.put("args", args);
		return createCommand(bridge, "SEND", data);
	}

	public static String createJWP(Bridge bridge, String name, Reference callbackRef) {
		// Format: {name: STRING, callback: BRIDGEREF }
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("name", name);
		if(callbackRef != null){
			data.put("callback", callbackRef);
		}
		return createCommand(bridge, "JOINWORKERPOOL", data);
	}

	public static String createGETCHANNEL(Bridge bridge, String channelName) {
		// Format: {name: STRING }
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("name", channelName);
		return createCommand(bridge, "GETCHANNEL", data);
	}

	public static String createLEAVECHANNEL(Bridge bridge, String name, Reference handlerRef,
			Reference callbackRef) {
		// Format: {name: STRING, handler: BRIDGEREF , callback: BRIDGEREF }
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("name", name);
		data.put("handler", handlerRef);
		if(callbackRef != null){
			data.put("callback", callbackRef);
		}
		return createCommand(bridge, "LEAVECHANNEL", data);
	}

	public static String createJC(Bridge bridge, String name, Reference handlerRef,
			Reference callbackRef) {
		// Format: {name: STRING, handler: BRIDGEREF , callback: BRIDGEREF 
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("name", name);
		data.put("handler", handlerRef);
		if(callbackRef != null){
			data.put("callback", callbackRef);
		}
		return createCommand(bridge, "JOINCHANNEL", data);
	}	
	
	public static String createCONNECT(Bridge bridge, String sessionId, String secret, String apiKey) {
		// Format: {session: [SESSIONID, SECRET] || [null ,null], api_key: API_KEY || null}
		Map<String, Object> data = new HashMap<String, Object>();
		List<String> session = Arrays.asList(sessionId, secret);
		data.put("session", session);
		data.put("api_key", apiKey);
		return createCommand(bridge, "CONNECT", data);
	}
	
	private static String createCommand(Bridge bridge, String command, Map<String, Object> data) {
		// Format: {command: STRING, data: {...}}
		ObjectMapper mapper = new ObjectMapper();
		SimpleModule module = new SimpleModule("Handler", new Version(0, 1, 0, "alpha"));
		module.addSerializer(new ReferenceSerializer(bridge, Reference.class))
		.addSerializer(new BridgeObjectSerializer(bridge, BridgeObject.class))
		.addSerializer(new BridgeRemoteObjectSerializer(bridge, BridgeRemoteObject.class));
		mapper.registerModule(module);
		
		
		Map<String, Object> commandObj = new HashMap<String, Object>();
		commandObj.put("command", command);
		commandObj.put("data", data);
		
		try {
			return mapper.writeValueAsString(commandObj);
		} catch (JsonGenerationException e) {
			e.printStackTrace();
			return null;
		} catch (JsonMappingException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static Map<String, Object> parseRedirector(String result) throws JsonParseException, JsonMappingException, IOException {
        ObjectMapper mapper = new ObjectMapper();
		return mapper.readValue(result, new TypeReference<Map<String, Object>>() {});
	}
}
