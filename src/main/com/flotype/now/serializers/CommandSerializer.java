package com.flotype.now.serializers;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;
import org.codehaus.jackson.map.ser.std.SerializerBase;

public class CommandSerializer extends SerializerBase<Map> {

	public CommandSerializer(Class<Map> class1) {
		super(class1);
	}

	public void serialize(Map value, JsonGenerator jsonGen, SerializerProvider serializerProvider) 
	throws IOException, JsonProcessingException {

		jsonGen.writeStartObject();
		jsonGen.writeFieldName("command");
		// Double serialize object because connection gateway simply forwards json to Rabbit
		jsonGen.writeString((String) value.get("command"));
		jsonGen.writeFieldName("data");
		jsonGen.writeRaw((String) value.get("data"));
		jsonGen.writeEndObject();
	}

}
