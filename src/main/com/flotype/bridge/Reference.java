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

	public static Reference Null = new Reference("null", null);

	private String networkAddress;
	private List<String> pathchain;
	private Bridge client;
	private String routingPrefix = "";

	protected Reference(String address, Bridge client){
		this(address, Arrays.asList(address.split("\\.")), client);
	}

	protected Reference(List<String> pathchain, Bridge client){
		this(Utils.join(pathchain, "."), pathchain, client);
	}

	protected Reference(String address, List<String> pathchain, Bridge client){
		setAddress(address);
		this.pathchain = pathchain;
		this.client = client;
	}

	protected void setAddress(String address) {
		this.networkAddress = address;
	}

	public List<String> getPathchain () {
		return pathchain;
	}

	public String getAddress() {
		return networkAddress;
	}

	protected void setRoutingPrefix (String prefix) {
		routingPrefix = prefix;
	}

	public String getRoutingPrefix() {
		return routingPrefix;
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