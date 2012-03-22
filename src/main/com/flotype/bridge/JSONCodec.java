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

public class JSONCodec {

	public static String createSEND(Bridge bridge, Reference destination, Object[] args) {
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("destination", destination);
		data.put("args", args);
		return createCommand(bridge, "SEND", data);
	}

	public static String createJWP(Bridge bridge, String name, Reference serviceRef,
			Reference callbackRef) {
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("name", name);
		data.put("handler", serviceRef);
		if(callbackRef != null){
			data.put("callback", callbackRef);
		}
		return createCommand(bridge, "JOINWORKERPOOL", data);
	}

	public static String createGETCHANNEL(Bridge bridge, String channelName) {
		// TODO Auto-generated method stub
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("name", channelName);
		return createCommand(bridge, "GETCHANNEL", data);
	}

	public static String createLEAVECHANNEL(Bridge bridge, String name, Reference handlerRef,
			Reference callbackRef) {
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
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("name", name);
		data.put("handler", handlerRef);
		if(callbackRef != null){
			data.put("callback", callbackRef);
		}
		return createCommand(bridge, "JOINCHANNEL", data);
	}	
	
	public static String createCONNECT(Bridge bridge, String sessionId, String secret, String apiKey) {
		Map<String, Object> data = new HashMap<String, Object>();
		List<String> session = Arrays.asList(sessionId, secret);
		data.put("session", session);
		data.put("api_key", apiKey);
		return createCommand(bridge, "CONNECT", data);
	}
	
	private static String createCommand(Bridge bridge, String command, Map<String, Object> data) {
		ObjectMapper mapper = new ObjectMapper();
		SimpleModule module = new SimpleModule("Handler", new Version(0, 1, 0, "alpha"));
		module.addSerializer(new ReferenceSerializer(bridge, Reference.class))
		.addSerializer(new ServiceSerializer(bridge, Service.class))
		.addSerializer(new ServiceClientSerializer(bridge, ServiceClient.class));
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
