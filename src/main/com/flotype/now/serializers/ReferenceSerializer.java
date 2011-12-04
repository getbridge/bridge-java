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
	
	public ReferenceSerializer(Class<Reference> t, List<Reference> refList) {
		super(t);
		this.refList = refList;
	}

	public void serialize(Reference value, JsonGenerator jsonGen, SerializerProvider serializerProvider) 
	throws IOException, JsonProcessingException {

		jsonGen.writeStartArray();
		jsonGen.writeString("now");
		jsonGen.writeString(value.getAddress());
		jsonGen.writeEndArray();
		
		refList.add(value);
	}
}
