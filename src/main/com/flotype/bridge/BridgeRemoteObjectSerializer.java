package com.flotype.bridge;
import java.io.IOException;
import java.lang.reflect.Proxy;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.SerializerProvider;
import org.codehaus.jackson.map.ser.std.SerializerBase;

import com.flotype.bridge.Bridge;
import com.flotype.bridge.BridgeRemoteObject;



class BridgeRemoteObjectSerializer extends SerializerBase<BridgeRemoteObject> {
	Bridge bridge;

	public BridgeRemoteObjectSerializer(Bridge bridge, Class<BridgeRemoteObject> t) {
		super(t);
		this.bridge = bridge;
	}

	public void serialize(BridgeRemoteObject value, JsonGenerator jsonGen, SerializerProvider serializerProvider)
	throws IOException, JsonProcessingException {
		serializerProvider.defaultSerializeValue(Proxy.getInvocationHandler(value), jsonGen);
	}
}