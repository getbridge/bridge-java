package com.flotype.now;
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

import com.flotype.now.serializers.CommandSerializer;
import com.flotype.now.serializers.ListSerializer;
import com.flotype.now.serializers.MapSerializer;
import com.flotype.now.serializers.ReferenceSerializer;
import com.flotype.now.serializers.SendSerializer;
import com.flotype.now.serializers.ServiceSerializer;
import com.flotype.now.serializers.StringSerializer;

public class Reference {
	private String networkAddress;
	private List<String> pathchain;
	private Client client;
	private String routingPrefix = "";
	
	protected Reference(String address, Client client){
		this(address, Arrays.asList(address.split("\\.")), client);
	}
	
	protected Reference(List<String> pathchain, Client client){
		this(Utils.join(pathchain, "."), pathchain, client);
	}
	
	protected Reference(String address, List<String> pathchain, Client client){
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

	public void invokeRPC(String methodName, Object ... args) throws IOException {
		
		// Ugly trick: pass refList to the serializer to be populated
		ObjectMapper argsMapper = new ObjectMapper();
		SimpleModule module = new SimpleModule("NowSerializers", new Version(0, 1, 0, "alpha"));
		module.addSerializer(new ReferenceSerializer(Reference.class))
			.addSerializer(new ServiceSerializer(Service.class))
			.addSerializer(new MapSerializer(Map.class))
			.addSerializer(new ListSerializer(List.class))
			.addSerializer(new StringSerializer(String.class));
		argsMapper.registerModule(module);
		
		String argsString = argsMapper.writeValueAsString(args);
		
		// Construct the request body here
		Map<String, Object> sendBody = new HashMap<String, Object>();
		
		sendBody.put("destination", this);
		sendBody.put("args", argsString);
		// TODO
		sendBody.put("exceptions", null);
		
		ObjectMapper sendMapper = new ObjectMapper();
		SimpleModule sendModule = new SimpleModule("Send", new Version(0, 1, 0, "alpha"));
		sendModule.addSerializer(new SendSerializer(Map.class));
		sendMapper.registerModule(sendModule);
		
		String sendString = sendMapper.writeValueAsString(sendBody);
		
			

		
		// Construct the request body here
		Map<String, Object> commandBody = new HashMap<String, Object>();
	
		commandBody.put("command", "SEND");
		commandBody.put("data", sendString);
		

		ObjectMapper commandMapper = new ObjectMapper();
		SimpleModule commandModule = new SimpleModule("Command", new Version(0, 1, 0, "alpha"));
		commandModule.addSerializer(new CommandSerializer(Map.class));
		commandMapper.registerModule(commandModule);
		
		String commandString = commandMapper.writeValueAsString(commandBody);
	
		client.write(commandString);
		
		// TODO Use the AMQP BasicProperties builder
		//properties.setHeaders(linkMap);
		
		// basicPublish(java.lang.String exchange, java.lang.String routingKey, AMQP.BasicProperties props, byte[] body)
		//this.channel.basicPublish(Utils.Prefix.TOPIC+id.toString(), Utils.Prefix.NAMESPACED_ROUTING + this.networkAddress, properties, bodyString.getBytes());
	}
	
}