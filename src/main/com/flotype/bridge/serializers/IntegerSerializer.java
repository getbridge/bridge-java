package com.flotype.bridge.serializers;
import java.io.IOException;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;
import org.codehaus.jackson.map.ser.std.SerializerBase;


public class IntegerSerializer extends SerializerBase<Integer> {
	
	public IntegerSerializer(Class<Integer> t) {
		super(t);
		// TODO Auto-generated constructor stub
	}

	public void serialize(Integer value, JsonGenerator jsonGen, SerializerProvider serializerProvider) 
	throws IOException, JsonProcessingException {

		jsonGen.writeStartArray();
		jsonGen.writeString("float");
		jsonGen.writeNumber(value);
		jsonGen.writeEndArray();
	}
}
