package com.flotype.bridge;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.Version;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.module.SimpleModule;
import org.codehaus.jackson.type.TypeReference;


import java.util.Map;

public class Utils {

	public static final int DEFAULT_PORT = 8082;
	public static final String DEFAULT_HOST = "127.0.0.1";
	public static int logLevel = 5;

	protected static Request deserialize(byte[] json) throws JsonParseException, JsonMappingException, IOException{

		// Create object mapper
		ObjectMapper mapper = new ObjectMapper();

		// Return a request object parsed by mapper
		Map<String, Object> jsonObj = mapper.readValue(json, new TypeReference<Map<String, Object>>(){});
		jsonObj = (Map<String, Object>) constructRefs(jsonObj);

		List<Object> args = (List<Object>) jsonObj.get("args");

		return new Request(((Reference) jsonObj.get("destination")), args);
	}
	
	
	public static Object constructRefs(Map<String, Object> theMap){
		Object pathchain;
		if((pathchain = theMap.get("ref")) != null){
			return ReferenceFactory.getFactory().generateReference((List<String>) pathchain);
		}
		
		for(Map.Entry<String,Object> entry: (theMap).entrySet()){
			
			Object value = entry.getValue();
			
			if(value != null && value.getClass() == java.util.LinkedHashMap.class){
				value = constructRefs((Map<String, Object>) value);
			} else if (value != null && value.getClass() == ArrayList.class) {
				value = constructRefs((List<Object>) value);
			}
			
			theMap.put(entry.getKey(), value);
		}
		
		return theMap;
	}
	
	private static Object constructRefs(List<Object> list) {
		
		int idx = 0;
		for(Object value : (List<Object>) list){
			if(value != null && value.getClass() == java.util.LinkedHashMap.class){
				value = constructRefs((Map<String, Object>) value);
			} else if (value != null && value.getClass() == ArrayList.class) {
				value = constructRefs((List<Object>) value);
			}
			list.set(idx, value);
			idx++;
		}

		return list;
	}


	protected static String generateId() {
		return Long.toHexString(Double.doubleToLongBits(Math.random()));
	}

	protected static Object deserialize(Object value) {
		Class<?> klass = value.getClass();
		if (klass == Double.class || klass == Integer.class){
			// All numbers are floats
			return ((Number) value).floatValue();
		} else {
			return value;
		}
	}

	protected static byte[] intToByteArray(int value) {
        return new byte[] {
                (byte)(value >>> 24),
                (byte)(value >>> 16),
                (byte)(value >>> 8),
                (byte)value};
	}

	protected static String join(Collection<String> s, String delimiter) {
		StringBuffer buffer = new StringBuffer();
        Iterator iter = s.iterator();
        while (iter.hasNext()) {
            buffer.append(iter.next());
            if (iter.hasNext()) {
                buffer.append(delimiter);
            }
        }
        return buffer.toString();
	}

	public static void info(Object obj) {
		if(Utils.logLevel > 2) {
			System.out.println(obj);
		}
	}

	public static void warn(Object obj) {
		if(Utils.logLevel > 1) {
			System.out.println(obj);
		}
	}

	public static void error(Object obj) {
		if(Utils.logLevel > 0) {
			System.out.println(obj);
		}
	}

}
