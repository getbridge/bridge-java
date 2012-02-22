package com.flotype.bridge;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class SystemService extends Service {
	Executor executor;
	private Bridge bridge;
	
	private static Log log = LogFactory.getLog(SystemService.class);

	public SystemService(Bridge bridge, Executor executor) {
		this.executor = executor;
		this.bridge = bridge;
	}
	public void hook_channel_handler(String channel, Reference handler, Reference callback){
		hook_channel(channel, handler);
		(new ServiceClient(callback)).invokeRPC("callback", bridge.getChannel(channel), channel);
	}
	
	public void hook_channel_handler(String channel, Reference handler){
		hook_channel(channel, handler);
	}
	
	public void getservice(String name, Reference callback) throws IOException {
		Service service = executor.getService(name);
		if(service != null) {
			callback.invokeRPC("callback", service);
		} else {
			callback.invokeRPC("callback", null, "Cannot find service " + name);
		}
		
	}
	
	private void hook_channel (String channel, Reference handler) {
		String channelName = "channel:"+channel;
		String key = handler.getServiceName();
		executor.addExistingServiceByKey(key, channelName);
	}
	
	public void remoteError(String error) {
		log.warn(error);
		bridge.eventHandler.onRemoteError(error);
	}

}
