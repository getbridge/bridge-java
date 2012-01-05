package com.flotype.now.serializers;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;
import org.codehaus.jackson.map.ser.std.SerializerBase;

public class JoinChannelSerializer extends SerializerBase<Map> {
	
	public JoinChannelSerializer(Class<Map> t) {
		super(t);
	}

	public void serialize(Map value, JsonGenerator jsonGen, SerializerProvider serializerProvider) 
	throws IOException, JsonProcessingException {

		jsonGen.writeStartObject();
		jsonGen.writeFieldName("type");
		jsonGen.writeString("joinChannel");
		jsonGen.writeFieldName("name");
		jsonGen.writeString((String)value.get("name"));
		jsonGen.writeFieldName("handler");
		serializerProvider.defaultSerializeValue(value.get("handler"), jsonGen);
		jsonGen.writeFieldName("callback");
		Object callback = value.get("callback");
		if(callback != null) {
			serializerProvider.defaultSerializeValue(callback, jsonGen);
		} else {
			jsonGen.writeStartArray();
			jsonGen.writeString("none");
			jsonGen.writeNull();
			jsonGen.writeEndArray();
		}
		jsonGen.writeEndObject();
	}
}
