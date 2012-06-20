package com.flotype.bridge.example.channels;

import com.flotype.bridge.*;

class AuthObj implements BridgeObject {
	private Bridge bridge;
	
	public AuthObj(Bridge bridge){
		this.bridge = bridge;
	}
	
	public void join(String channelName,  BridgeRemoteObject
			obj, BridgeRemoteObject callback){
			bridge.joinChannel(channelName, obj, false, callback);
	}
	public void joinWriteable(String channelName, String secretWord, BridgeRemoteObject
			obj, BridgeRemoteObject callback){
		if(secretWord.equals("secret123")){
			bridge.joinChannel(channelName, obj, true, callback);
		}
	}
}

public class ChannelsServer {

	public static void main(String[] args) throws Exception {
		Bridge bridge = new Bridge("myprivkey");
		bridge.publishService("auth", new AuthObj(bridge));
		bridge.connect();
	}
}
