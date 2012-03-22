package com.flotype.bridge;
import java.io.IOException;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.SerializerProvider;
import org.codehaus.jackson.map.ser.std.SerializerBase;



public class ServiceSerializer extends SerializerBase<Service> {
	Bridge bridge;

	public ServiceSerializer(Bridge bridge, Class<Service> t) {
		super(t);
		this.bridge = bridge;
	}

	public void serialize(Service value, JsonGenerator jsonGen, SerializerProvider serializerProvider)
	throws IOException, JsonProcessingException {
		Reference ref = bridge.dispatcher.storeRandomObject(value);
		serializerProvider.defaultSerializeValue(ref, jsonGen);
	}
}
