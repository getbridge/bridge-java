package com.flotype.now.serializers;
import java.io.IOException;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;
import org.codehaus.jackson.map.ser.std.SerializerBase;


public class FloatSerializer extends SerializerBase<Float> {
	
	public FloatSerializer(Class<Float> t) {
		super(t);
		// TODO Auto-generated constructor stub
	}

	public void serialize(Float value, JsonGenerator jsonGen, SerializerProvider serializerProvider) 
	throws IOException, JsonProcessingException {

		jsonGen.writeStartArray();
		jsonGen.writeString("float");
		jsonGen.writeNumber(value);
		jsonGen.writeEndArray();
	}
}
