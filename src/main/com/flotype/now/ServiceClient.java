package com.flotype.now;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.Version;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.module.SimpleModule;

import com.flotype.now.serializers.ListSerializer;
import com.flotype.now.serializers.MapSerializer;
import com.flotype.now.serializers.ReferenceSerializer;
import com.flotype.now.serializers.StringSerializer;
import com.rabbitmq.client.Channel;


public class ServiceClient {
	Reference reference;
	
	public ServiceClient(Reference reference){
		this.reference = reference;
	}
	
	protected void invokeRPC(String methodName, Object... args){
		List<Reference> refList = new ArrayList<Reference>();
		ObjectMapper mapper = new ObjectMapper();
		SimpleModule module = new SimpleModule("NowSerializers", new Version(0, 1, 0, "alpha"));
		
		// Ugly trick: pass refList to the serializer to be populated
		module.addSerializer(new ReferenceSerializer(Reference.class, refList))
			.addSerializer(new MapSerializer(Map.class))
			.addSerializer(new ListSerializer(List.class))
			.addSerializer(new StringSerializer(String.class));
		
		mapper.registerModule(module);
		
		try {
			String argsString = mapper.writeValueAsString(args);
			this.reference.invokeRPC(methodName, argsString, refList);
		} catch (JsonGenerationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
