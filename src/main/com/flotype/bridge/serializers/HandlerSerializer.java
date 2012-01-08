package com.flotype.bridge.serializers;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;
import org.codehaus.jackson.map.ser.std.SerializerBase;

public class HandlerSerializer extends SerializerBase<Map> {
	
	public HandlerSerializer(Class<Map> t) {
		super(t);
	}

	public void serialize(Map value, JsonGenerator jsonGen, SerializerProvider serializerProvider) 
	throws IOException, JsonProcessingException {
		jsonGen.writeStartObject();
		jsonGen.writeFieldName("name");
		jsonGen.writeStartArray();
		jsonGen.writeString("str");
		jsonGen.writeString((String)value.get("name"));
		jsonGen.writeEndArray();
		jsonGen.writeFieldName("handler");
		serializerProvider.defaultSerializeValue(value.get("handler"), jsonGen);
		jsonGen.writeFieldName("callback");
		serializerProvider.defaultSerializeValue(value.get("callback"), jsonGen);
		jsonGen.writeEndObject();
	}
}
