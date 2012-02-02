package com.flotype.bridge;

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

}
