package com.flotype.bridge.serializers;

import java.io.IOException;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;
import org.codehaus.jackson.map.ser.std.SerializerBase;

public class BooleanSerializer extends SerializerBase<Boolean> {

	public BooleanSerializer(Class<Boolean> class1) {
		super(class1);
	}

	public void serialize(Boolean value, JsonGenerator jsonGen, SerializerProvider serializerProvider)
	throws IOException, JsonProcessingException {

		jsonGen.writeStartArray();
		jsonGen.writeString("bool");
		jsonGen.writeBoolean(value);
		jsonGen.writeEndArray();
	}
}