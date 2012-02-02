package com.flotype.bridge;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.Version;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.module.SimpleModule;

import com.flotype.bridge.serializers.ReferenceSerializer;
import com.flotype.bridge.serializers.ServiceSerializer;

public class Reference {

	private List<String> pathchain ;
	private Bridge client;

	protected Reference(List<String> pathchain, Bridge client){
		this.client = client;

		if(pathchain != null){
			this.pathchain = pathchain;
		} else {
			this.pathchain = Arrays.asList(new String[]{"", "", "", ""});
			
			this.setRoutingPrefix("client");
			this.setRoutingId(client.getConnectionId());
		}
	}
	
	protected Reference(Reference other){
		this(other.getPathchain(), other.client);
	}

	public List<String> getPathchain () {
		return pathchain;
	}

	protected void setRoutingPrefix (String prefix) {
		pathchain.set(0, prefix);
	}
	
	protected void setRoutingId (String id) {
		pathchain.set(1, id);
	}
	
	protected void setServiceName (String serviceName) {
		pathchain.set(2, serviceName);
	}
	
	protected void setMethodName (String methodName) {
		pathchain.set(3, methodName);
	}

	protected String getRoutingPrefix() {
		return pathchain.get(0);
	}
	
	protected String getRoutingId() {
		return pathchain.get(1);
	}
	
	protected String getServiceName() {
		return pathchain.get(2);
	}
	
	protected String getMethodName() {
		return pathchain.get(3);
	}
	public void invokeRPC(String methodName, Object ... args) throws IOException {

		ObjectMapper mapper = new ObjectMapper();
		SimpleModule module = new SimpleModule("NowSerializers", new Version(0, 1, 0, "alpha"));
		module.addSerializer(new ReferenceSerializer(Reference.class))
			.addSerializer(new ServiceSerializer(Service.class));
		mapper.registerModule(module);

		
		// Construct the request body here
		Map<String, Object> sendBody = new HashMap<String, Object>();

		Reference destination = ReferenceFactory.getFactory().generateReference(this);
		destination.setMethodName(methodName);
		
		sendBody.put("destination", destination);
		sendBody.put("method", methodName);
		sendBody.put("args", args);
		// TODO
		sendBody.put("exceptions", null);

	

		// Construct the request body here
		Map<String, Object> commandBody = new HashMap<String, Object>();

		commandBody.put("command", "SEND");
		commandBody.put("data", sendBody);

		String commandString = mapper.writeValueAsString(commandBody);

		client.write(commandString);
	}

}