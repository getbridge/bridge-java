package com.flotype.now;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.rabbitmq.client.Channel;


public class ReferenceFactory {
	
	static ReferenceFactory theFactory;
	static UUID id;
	
	private Channel channel;
	
	protected ReferenceFactory(Channel channel, UUID id) {
		this.channel = channel;
		this.id = id;
	}

	protected static void createFactory(Channel channel, UUID id){
		theFactory = new ReferenceFactory(channel, id);
	}
	
	protected static ReferenceFactory getFactory(){
		if(theFactory == null){
			throw new Error("ReferenceFactory uninitialized");
		} else {
			return theFactory;
		}
	}
	
	protected Reference generateReference(String value){
		return new Reference(value, channel, id);
	}

	public Reference generateReference(List<String> value) {
		return new Reference(value, channel, id);
	}

}