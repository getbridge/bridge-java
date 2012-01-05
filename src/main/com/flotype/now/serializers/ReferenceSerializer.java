package com.flotype.now.serializers;
import java.io.IOException;
import java.util.List;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;
import org.codehaus.jackson.map.ser.std.SerializerBase;

import com.flotype.now.Reference;



public class ReferenceSerializer extends SerializerBase<Reference> {
	
	private List<Reference> refList;
	
	public ReferenceSerializer(Class<Reference> t) {
		super(t);
		this.refList = refList;
	}

	public void serialize(Reference value, JsonGenerator jsonGen, SerializerProvider serializerProvider) 
	throws IOException, JsonProcessingException {

		if(value == null) {
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
			for(String path : value.getPathchain()) {
				jsonGen.writeString(path);
			}
			jsonGen.writeEndArray();
			jsonGen.writeEndObject();
			jsonGen.writeEndArray();
		}
	
	}
}
