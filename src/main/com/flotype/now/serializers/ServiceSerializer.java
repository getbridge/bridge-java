package com.flotype.now.serializers;
import java.io.IOException;
import java.util.List;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;
import org.codehaus.jackson.map.ser.std.SerializerBase;

import com.flotype.now.Reference;
import com.flotype.now.ReferenceFactory;
import com.flotype.now.Service;



public class ServiceSerializer extends SerializerBase<Service> {
	
	public ServiceSerializer(Class<Service> t) {
		super(t);
	}

	public void serialize(Service value, JsonGenerator jsonGen, SerializerProvider serializerProvider) 
	throws IOException, JsonProcessingException {
		value.ensureReference();
		serializerProvider.defaultSerializeValue(value.getReference(), jsonGen);
	}
}
