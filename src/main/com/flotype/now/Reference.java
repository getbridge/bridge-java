package com.flotype.now;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.AMQP.BasicProperties;


public class Reference {
	private String address;
	Channel channel;
	UUID id;
	
	protected Reference(String value, Channel channel, UUID id){
		setAddress(value);
		this.channel = channel;
		this.id = id;
	}

	protected void establishLink() {
		// Only supporting direct links for now. It's unclear how named links would work
		try {
			this.channel.queueBind(Utils.Prefix.CLIENT + this.getAddress(), Utils.Prefix.TOPIC + id.toString(), this.getAddress());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	protected void setAddress(String address) {
		this.address = address;
	}

	public String getAddress() {
		return address;
	}

	public void invokeRPC(String methodName, String argsString,
			List<Reference> refList) throws IOException {
		
		Map<String, Object> linkMap = new HashMap<String, Object>();
		for(int i = 0; i < refList.size(); i++){
			Reference ref = refList.get(i);
			String headerKey = "link_"+i;
			linkMap.put(headerKey, ref.address);
		}
		
		BasicProperties properties = new BasicProperties();
		
		// Deprecated method but the alternative is to fill out a gigantic constructor with 10 parameters. Fuck that.
		properties.setHeaders(linkMap);
		
		// basicPublish(java.lang.String exchange, java.lang.String routingKey, AMQP.BasicProperties props, byte[] body)
		this.channel.basicPublish(Utils.Prefix.TOPIC+id.toString(), this.address, properties, argsString.getBytes());
	}
	
	
}