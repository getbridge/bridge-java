package com.flotype.bridge.serializers;
import java.io.IOException;
import java.util.List;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;
import org.codehaus.jackson.map.ser.std.SerializerBase;

import com.flotype.bridge.Reference;



public class ReferenceSerializer extends SerializerBase<Reference> {

	public ReferenceSerializer(Class<Reference> t) {
		super(t);
	}

	public void serialize(Reference value, JsonGenerator jsonGen, SerializerProvider serializerProvider)
	throws IOException, JsonProcessingException {

		if(value == Reference.Null) {
			jsonGen.writeStartArray();
			jsonGen.writeString("none");
			jsonGen.writeNull();
			jsonGen.writeEndArray();
		} else {
			jsonGen.writeStartArray();
			jsonGen.writeString("now");
			jsonGen.writeStartObject();
			jsonGen.writeFieldName("ref");
			jsonGen.writeStartArray();
			String prefix = value.getRoutingPrefix();
			if(prefix.length() > 0) {
				jsonGen.writeString(prefix);
			}
			for(String path : value.getPathchain()) {
				jsonGen.writeString(path);
			}
			jsonGen.writeEndArray();
			jsonGen.writeEndObject();
			jsonGen.writeEndArray();
		}

	}
}
