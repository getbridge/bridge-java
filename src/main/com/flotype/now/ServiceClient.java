package com.flotype.now;
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

import com.flotype.now.serializers.ListSerializer;
import com.flotype.now.serializers.MapSerializer;
import com.flotype.now.serializers.OuterSerializer;
import com.flotype.now.serializers.ReferenceSerializer;
import com.flotype.now.serializers.StringSerializer;



public class ServiceClient {
	Reference reference;
	
	public ServiceClient(Reference reference){
		this.reference = reference;
	}
	
	
	/* This method is basically a big clusterfuck. 
	 * Primary use: comedic value and for inducing depression.
	 * Secondary use: creating JSON objects
	 */
	protected void invokeRPC(String methodName, Object... args){
		List<Reference> refList = new ArrayList<Reference>();
		
		
		// Ugly trick: pass refList to the serializer to be populated
		ObjectMapper argsMapper = new ObjectMapper();
		SimpleModule module = new SimpleModule("NowSerializers", new Version(0, 1, 0, "alpha"));
		module.addSerializer(new ReferenceSerializer(Reference.class, refList))
			.addSerializer(new MapSerializer(Map.class))
			.addSerializer(new ListSerializer(List.class))
			.addSerializer(new StringSerializer(String.class));
		argsMapper.registerModule(module);
		
		try {
			String argsString = argsMapper.writeValueAsString(args);
			
			// Construct the request body here
			Map<String, Object> requestBody = new HashMap<String, Object>();
			
			List<String> pathchainWithMethod = new ArrayList<String>(this.reference.pathchain);
			pathchainWithMethod.add(methodName);
			
			requestBody.put("pathchain", pathchainWithMethod);
			requestBody.put("args", argsString);
	
			ObjectMapper outerMapper = new ObjectMapper();
			SimpleModule outerModule = new SimpleModule("Outer", new Version(0, 1, 0, "alpha"));
			outerModule.addSerializer(new OuterSerializer(Map.class));
			outerMapper.registerModule(outerModule);
			
			String bodyString = outerMapper.writeValueAsString(requestBody);
			
			System.out.println(bodyString);
			
			this.reference.invokeRPC(bodyString, refList);
			
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