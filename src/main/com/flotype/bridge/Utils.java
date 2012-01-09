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
	public static int logLevel = 0;
	
	protected static Request deserialize(byte[] json) throws JsonParseException, JsonMappingException, IOException{
		
		// Create object mapper
		ObjectMapper mapper = new ObjectMapper();
		
		// Return a request object parsed by mapper
		ArrayList<Object> jsonObj = mapper.readValue(json, new TypeReference<ArrayList<Object>>(){});
		ArrayList<Object> deserArgs = new ArrayList<Object>();
		
		Map<String, Object> data = (Map<String, Object>) jsonObj.get(1);
		ArrayList<ArrayList<Object>> args = ((ArrayList<ArrayList<ArrayList<Object>>>) data.get("args")).get(1);
		
		List<String> pathchain = (ArrayList<String>)((Map<String, Object>)((ArrayList<Object>) data.get("destination")).get(1)).get("ref");		
		return new Request(pathchain, args);
	}
	
	protected static String generateId() {
		return Long.toHexString(Double.doubleToLongBits(Math.random()));
	}

	protected static Class<?> classFromString(String type) {
		Class<?> theClass = java.lang.Object.class;
		if(type.equals("list")){
			theClass = java.util.ArrayList.class;
		} else if (type.equals("dict")) {
			theClass = java.util.HashMap.class;
		} else if (type.equals("str")) {
			theClass = java.lang.String.class;
		} else if (type.equals("float")) {
			// All numbers are floats now
			theClass = java.lang.Float.class;
		} else if (type.equals("none")){
			theClass = java.lang.Object.class;
		} else if (type.equals("now")) {
			theClass = Reference.class;
		}
		return theClass;
	}
	
	// TODO: unit test
	protected static Object deserialize(String type, Object value) {
		Object newValue = value;
		if(type.equals("list")){
			List<Object> theList = new ArrayList<Object>();
			for(List<Object> item : (List<List>) value){
				String type1 = (String) item.get(0);
				Object value1 = item.get(1);
				theList.add(Utils.deserialize(type1 , value1));
			}
			
			newValue = theList;
		} else if (type.equals("dict")) {
			Map<String, Object> theMap = new HashMap<String, Object>();
			for(Map.Entry<String, List<?>> entry: ((Map<String, List<?>>) value).entrySet()){
				String type1 = (String) entry.getValue().get(0);
				Object value1 = entry.getValue().get(1);
				theMap.put(entry.getKey(), Utils.deserialize(type1 , value1));
			}
			
			newValue = theMap;
		} else if (type.equals("now")) {
			Map<String, List<String>> refMap = (Map<String, List<String>>) value;
			List<String> path = refMap.get("ref");
			Reference theReference = ReferenceFactory.getFactory().generateReference(path);
			newValue = theReference;
		} else if (type.equals("float")){
			newValue = ((Number) value).floatValue();
		}
		return newValue;
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
