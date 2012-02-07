package com.flotype.bridge.serializers;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.SerializerProvider;
import org.codehaus.jackson.map.ser.std.SerializerBase;

import com.flotype.bridge.Reference;



public class ReferenceSerializer extends SerializerBase<Reference> {

	public ReferenceSerializer(Class<Reference> t) {
		super(t);
	}

	public void serialize(Reference value, JsonGenerator jsonGen, SerializerProvider serializerProvider)
	throws IOException, JsonProcessingException {
		
		Map<String, Object> obj = null;

		if(value != null) {
			obj = new HashMap<String, Object>();
			obj.put("ref", value.getPathchain());
		}
		
		serializerProvider.defaultSerializeValue(obj, jsonGen);

	}
}
