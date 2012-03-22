package com.flotype.bridge.serializers;
import java.io.IOException;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.SerializerProvider;
import org.codehaus.jackson.map.ser.std.SerializerBase;

import com.flotype.bridge.ServiceClient;



public class ServiceClientSerializer extends SerializerBase<ServiceClient> {

	public ServiceClientSerializer(Class<ServiceClient> t) {
		super(t);
	}

	public void serialize(ServiceClient value, JsonGenerator jsonGen, SerializerProvider serializerProvider)
	throws IOException, JsonProcessingException {
		
		serializerProvider.defaultSerializeValue(value.getReference(), jsonGen);

	}
}
