package com.flotype.bridge;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.Version;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.module.SimpleModule;

import com.flotype.bridge.serializers.ListSerializer;
import com.flotype.bridge.serializers.MapSerializer;
import com.flotype.bridge.serializers.ReferenceSerializer;
import com.flotype.bridge.serializers.SendSerializer;
import com.flotype.bridge.serializers.ServiceSerializer;
import com.flotype.bridge.serializers.StringSerializer;



public class ServiceClient {
	Reference reference;

	public ServiceClient(Reference reference){
		this.reference = reference;
	}

	protected void invokeRPC(String methodName, Object... args){
		try {
			this.reference.invokeRPC(methodName, args);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}