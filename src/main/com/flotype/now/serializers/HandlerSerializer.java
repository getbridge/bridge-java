package com.flotype.now.serializers;
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
		jsonGen.writeString((String)value.get("name"));
		jsonGen.writeFieldName("handler");
		serializerProvider.defaultSerializeValue(value.get("handler"), jsonGen);
		jsonGen.writeFieldName("callback");
		serializerProvider.defaultSerializeValue(value.get("callback"), jsonGen);
		jsonGen.writeEndObject();
	}
}
