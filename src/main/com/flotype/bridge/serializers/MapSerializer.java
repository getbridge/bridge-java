package com.flotype.bridge.serializers;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;
import org.codehaus.jackson.map.ser.std.SerializerBase;


public class MapSerializer extends SerializerBase<Map> {

	public MapSerializer(Class<Map> class1) {
		super(class1);
		// TODO Auto-generated constructor stub
	}

	public void serialize(Map value, JsonGenerator jsonGen, SerializerProvider serializerProvider)
	throws IOException, JsonProcessingException {

		jsonGen.writeStartArray();
		jsonGen.writeString("dict");
		jsonGen.writeStartObject();
		Set<Map.Entry> s = value.entrySet();
		for(Map.Entry entry : s){
			jsonGen.writeFieldName((String) entry.getKey());
			serializerProvider.defaultSerializeValue(entry.getValue(), jsonGen);
		}
		jsonGen.writeEndObject();
		jsonGen.writeEndArray();
	}
}
