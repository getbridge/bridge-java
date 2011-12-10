package com.flotype.now.serializers;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;
import org.codehaus.jackson.map.ser.std.SerializerBase;

public class OuterSerializer extends SerializerBase<Map> {

	public OuterSerializer(Class<Map> class1) {
		super(class1);
	}

	public void serialize(Map value, JsonGenerator jsonGen, SerializerProvider serializerProvider) 
	throws IOException, JsonProcessingException {

		jsonGen.writeStartObject();
		jsonGen.writeFieldName("pathchain");
		serializerProvider.defaultSerializeValue(value.get("pathchain"), jsonGen);
		jsonGen.writeFieldName("serargskwargs");
		jsonGen.writeStartArray();
		jsonGen.writeRawValue((String) value.get("serargskwargs"));
		jsonGen.writeEndArray();
		jsonGen.writeEndObject();
	}

}
