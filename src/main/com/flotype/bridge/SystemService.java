package com.flotype.bridge;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class SystemService implements Service {
	Dispatcher dispatcher;
	private Bridge bridge;
	
	private static Log log = LogFactory.getLog(SystemService.class);

	public SystemService(Bridge bridge, Dispatcher dispatcher) {
		this.dispatcher = dispatcher;
		this.bridge = bridge;
	}
	public void hookChannelHandler(String channel, Reference handler, Reference callback){
		hookChannel(channel, handler);
		callback.invokeByName(null, "callback", new Object[]{Reference.createChannelReference(bridge, channel, handler.getOperations()), channel});
	}
	
	public void hookChannelHandler(String channel, Reference handler){
		hookChannel(channel, handler);
	}	
	
	private void hookChannel (String channel, Reference handler) {
		String channelName = "channel:"+channel;
		String key = handler.getObjectId();
		dispatcher.storeExistingObjectByKey(key, channelName);
	}
	
	public void getService(String name, Reference callback) throws IOException {
		Object service = dispatcher.getObject(name);
		if(service != null) {
			callback.invokeByName(null, "callback", new Object[]{service});
		} else {
			callback.invokeByName(null, "callback", new Object[]{null});
		}
		
	}
	
	public void remoteError(String error) {
		log.warn(error);
		bridge.eventHandler.onRemoteError(error);
	}

}
