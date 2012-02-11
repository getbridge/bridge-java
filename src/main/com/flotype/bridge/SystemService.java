package com.flotype.bridge;

import java.io.IOException;

public class SystemService extends Service {
	Executor executor;
	private Bridge bridge;

	public SystemService(Bridge bridge, Executor executor) {
		this.executor = executor;
		this.bridge = bridge;
	}
	public void hook_channel_handler(String channel, Reference handler, Reference callback){
		String channelName = "channel:"+channel;
		String key = handler.getServiceName();
		executor.addExistingServiceByKey(channelName, key);
		(new ServiceClient(callback)).invokeRPC("callback", bridge.getChannel(channel), channel);
	}
	
	public void getservice(String name, Reference callback) throws IOException {
		Service service = executor.getService(name);
		if(service != null) {
			callback.invokeRPC("callback", service);
		} else {
			callback.invokeRPC("callback", null, "Cannot find service " + name);
		}
		
	}
	
	public void remoteError(String error) {
		System.err.println(error);
		bridge.eventHandler.onRemoteError(error);
	}

}
