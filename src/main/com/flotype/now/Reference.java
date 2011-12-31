package com.flotype.now;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.codehaus.jackson.Version;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.module.SimpleModule;

import com.flotype.now.serializers.MessageSerializer;
import com.flotype.now.serializers.OuterSerializer;

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

	public void invokeRPC(String methodName, String bodyString, List<Reference> refList) throws IOException {
		
		Map<String, Object> linkMap = new HashMap<String, Object>();
		for(int i = 0; i < refList.size(); i++){
			Reference ref = refList.get(i);
			String headerKey = "link_"+i;
			linkMap.put(headerKey, ref.networkAddress);
		}
		String routingKey = this.routingPrefix + this.networkAddress + "." + methodName ;
		
		
		// Add message wrapper
		
		// Construct the request body here
		Map<String, Object> messageBody = new HashMap<String, Object>();
		

		messageBody.put("message", bodyString);
		messageBody.put("routingKey", routingKey);
		messageBody.put("headers", linkMap);
		

		ObjectMapper messageMapper = new ObjectMapper();
		SimpleModule messageModule = new SimpleModule("Outer", new Version(0, 1, 0, "alpha"));
		messageModule.addSerializer(new MessageSerializer(Map.class));
		messageMapper.registerModule(messageModule);
		
		String messageString = messageMapper.writeValueAsString(messageBody);
	
		client.write(messageString);
		
		// TODO Use the AMQP BasicProperties builder
		//properties.setHeaders(linkMap);
		
		// basicPublish(java.lang.String exchange, java.lang.String routingKey, AMQP.BasicProperties props, byte[] body)
		//this.channel.basicPublish(Utils.Prefix.TOPIC+id.toString(), Utils.Prefix.NAMESPACED_ROUTING + this.networkAddress, properties, bodyString.getBytes());
	}
	
}