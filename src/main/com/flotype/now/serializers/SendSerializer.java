package com.flotype.now.serializers;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;
import org.codehaus.jackson.map.ser.std.SerializerBase;

public class SendSerializer extends SerializerBase<Map> {

	public SendSerializer(Class<Map> class1) {
		super(class1);
	}

	public void serialize(Map value, JsonGenerator jsonGen, SerializerProvider serializerProvider) 
	throws IOException, JsonProcessingException {
		jsonGen.writeStartObject();
		jsonGen.writeFieldName("destination");
		serializerProvider.defaultSerializeValue(value.get("destination"), jsonGen);
		jsonGen.writeFieldName("args");
		jsonGen.writeRawValue((String) value.get("args"));
		jsonGen.writeFieldName("exceptions");
		serializerProvider.defaultSerializeValue(value.get("exceptions"), jsonGen);
		jsonGen.writeEndObject();
	}

}
