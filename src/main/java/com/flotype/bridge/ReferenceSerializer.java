package com.flotype.bridge;

import java.io.IOException;
import java.util.Map;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.SerializerProvider;
import org.codehaus.jackson.map.ser.std.SerializerBase;

class ReferenceSerializer extends SerializerBase<Reference> {

	public ReferenceSerializer(Bridge bridge, Class<Reference> t) {
		super(t);
	}

	public void serialize(Reference value, JsonGenerator jsonGen,
			SerializerProvider serializerProvider) throws IOException,
			JsonProcessingException {

		Map<String, Object> obj = null;

		if (value != null) {
			obj = value.toDict();
		}

		serializerProvider.defaultSerializeValue(obj, jsonGen);

	}
}
