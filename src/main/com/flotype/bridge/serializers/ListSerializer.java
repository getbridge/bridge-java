package com.flotype.bridge.serializers;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;
import org.codehaus.jackson.map.ser.std.SerializerBase;


public class ListSerializer extends SerializerBase<List> {
	
	public ListSerializer(Class<List> class1) {
		super(class1);	
	}

	public void serialize(List value, JsonGenerator jsonGen, SerializerProvider serializerProvider) 
	throws IOException, JsonProcessingException {

		jsonGen.writeStartArray();
		jsonGen.writeString("list");
		jsonGen.writeStartArray();
		for(Object item : value){
			serializerProvider.defaultSerializeValue(item, jsonGen);
		}
		jsonGen.writeEndArray();
		jsonGen.writeEndArray();
	}
}
