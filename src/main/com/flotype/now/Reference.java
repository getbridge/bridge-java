package com.flotype.now;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Reference {
	private String networkAddress;
	List<String> pathchain;
	UUID id;
	
	protected Reference(String address, UUID id){
		this(address, Arrays.asList(new String[]{address}), id);
	}
	
	protected Reference(List<String> pathchain, UUID id){
		this(pathchain.get(0), pathchain, id);
	}
	
	protected Reference(String address, List<String> pathchain, UUID id){
		setAddress(address);
		this.pathchain = pathchain;
		this.id = id;
	}

	protected void setAddress(String address) {
		this.networkAddress = address;
	}

	public String getAddress() {
		return networkAddress;
	}

	public void invokeRPC(String bodyString, List<Reference> refList) throws IOException {
		
		Map<String, Object> linkMap = new HashMap<String, Object>();
		for(int i = 0; i < refList.size(); i++){
			Reference ref = refList.get(i);
			String headerKey = "link_"+i;
			linkMap.put(headerKey, ref.networkAddress);
		}
		
		
		// TODO Use the AMQP BasicProperties builder
		//properties.setHeaders(linkMap);
		
		// basicPublish(java.lang.String exchange, java.lang.String routingKey, AMQP.BasicProperties props, byte[] body)
		//this.channel.basicPublish(Utils.Prefix.TOPIC+id.toString(), Utils.Prefix.NAMESPACED_ROUTING + this.networkAddress, properties, bodyString.getBytes());
	}
	
	
}