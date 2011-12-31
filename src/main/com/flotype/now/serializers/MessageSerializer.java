package com.flotype.now.serializers;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;
import org.codehaus.jackson.map.ser.std.SerializerBase;

public class MessageSerializer extends SerializerBase<Map> {

	public MessageSerializer(Class<Map> class1) {
		super(class1);
	}

	public void serialize(Map value, JsonGenerator jsonGen, SerializerProvider serializerProvider) 
	throws IOException, JsonProcessingException {

		jsonGen.writeStartObject();
		jsonGen.writeFieldName("message");
		// Double serialize object because connection gateway simply forwards json to Rabbit
		jsonGen.writeString((String) value.get("message"));
		jsonGen.writeFieldName("routingKey");
		jsonGen.writeString((String)value.get("routingKey"));
		jsonGen.writeFieldName("headers");
		jsonGen.writeStartObject();
		Map headers = (Map) value.get("headers");
		Set<Map.Entry> s = headers.entrySet();
		for(Map.Entry entry : s){
			jsonGen.writeFieldName((String) entry.getKey());
			jsonGen.writeString((String) entry.getValue());
		}
		jsonGen.writeEndObject();
		jsonGen.writeEndObject();
	}

}
