package com.flotype.now;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import java.util.Map;

public class Utils {
	
	public static final int DEFAULT_PORT = 8082;
	public static final String DEFAULT_HOST = "127.0.0.1";
	
	
	protected static Request deserialize(byte[] json) throws JsonParseException, JsonMappingException, IOException{
		
		// Create object mapper
		ObjectMapper mapper = new ObjectMapper();
		
		// Return a request object parsed by mapper
		Request jsonObj = mapper.readValue(json, Request.class);
		return jsonObj;
	}
	
	//This is complete bullshit. But it will work for now
	protected static boolean isUUID(String name){
		 String[] components = name.split("-");
         return components.length == 5;
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
			theClass = java.lang.Double.class;
		} else if (type.equals("none")){
			theClass = java.lang.Object.class;
		} else if (type.equals("now")) {
			theClass = Reference.class;
		}
		return theClass;
	}
	
	// TODO: unit test
	protected static Object deserialize(String type, Object value, List<Reference> refList) {
		Object newValue = value;
		if(type.equals("list")){
			List<Object> theList = new ArrayList<Object>();
			for(List<Object> item : (List<List>) value){
				String type1 = (String) item.get(0);
				Object value1 = item.get(1);
				theList.add(Utils.deserialize(type1 , value1, refList));
			}
			
			newValue = theList;
		} else if (type.equals("dict")) {
			Map<String, Object> theMap = new HashMap<String, Object>();
			for(Map.Entry<String, List<?>> entry: ((Map<String, List<?>>) value).entrySet()){
				String type1 = (String) entry.getValue().get(0);
				Object value1 = entry.getValue().get(1);
				theMap.put(entry.getKey(), Utils.deserialize(type1 , value1, refList));
			}
			
			newValue = theMap;
		} else if (type.equals("now")) {
			Map<String, List<String>> refMap = (Map<String, List<String>>) value;
			List<String> path = refMap.get("ref");
			Reference theReference = ReferenceFactory.getFactory().generateReference(path);
			newValue = theReference;
			refList.add(theReference);
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

}
