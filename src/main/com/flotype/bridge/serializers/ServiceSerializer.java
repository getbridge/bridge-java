package com.flotype.bridge.serializers;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;
import org.codehaus.jackson.map.ser.std.SerializerBase;

import com.flotype.bridge.Reference;
import com.flotype.bridge.ReferenceFactory;
import com.flotype.bridge.Service;



public class ServiceSerializer extends SerializerBase<Service> {

	public ServiceSerializer(Class<Service> t) {
		super(t);
	}

	public void serialize(Service value, JsonGenerator jsonGen, SerializerProvider serializerProvider)
	throws IOException, JsonProcessingException {
		value.ensureReference();
		
		Reference ref = value.getReference();
		
		Map<String, Object> obj = null;

		if(value != null) {
			obj = new HashMap<String, Object>();
			obj.put("ref", ref.getPathchain());
			obj.put("operations", value.getMethodNames());
		}
		
		serializerProvider.defaultSerializeValue(obj, jsonGen);
		
	}
}
