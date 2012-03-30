package com.flotype.bridge;

import java.io.IOException;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.SerializerProvider;
import org.codehaus.jackson.map.ser.std.SerializerBase;

class BridgeObjectSerializer extends SerializerBase<BridgeObject> {
	Bridge bridge;

	public BridgeObjectSerializer(Bridge bridge, Class<BridgeObject> t) {
		super(t);
		this.bridge = bridge;
	}

	public void serialize(BridgeObject value, JsonGenerator jsonGen,
			SerializerProvider serializerProvider) throws IOException,
			JsonProcessingException {
		Reference ref = bridge.dispatcher.storeRandomObject(value);
		serializerProvider.defaultSerializeValue(ref, jsonGen);
	}
}
