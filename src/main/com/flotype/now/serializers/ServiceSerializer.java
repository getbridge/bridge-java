package com.flotype.now.serializers;
import java.io.IOException;
import java.util.List;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;
import org.codehaus.jackson.map.ser.std.SerializerBase;

import com.flotype.now.Reference;
import com.flotype.now.Service;



public class ServiceSerializer extends SerializerBase<Service> {
	
	private List<Reference> refList;
	
	public ServiceSerializer(Class<Service> t, List<Reference> refList) {
		super(t);
		this.refList = refList;
	}

	public void serialize(Service value, JsonGenerator jsonGen, SerializerProvider serializerProvider) 
	throws IOException, JsonProcessingException {

		serializerProvider.defaultSerializeValue(value.getReference(), jsonGen);

	}
}
