package com.flotype.bridge.example.chat;

import com.flotype.bridge.*;

class ChatObj implements BridgeObject {
	public void message(String sender, String message) {
		System.out.println(sender + ":" + message);
	}
}

interface RemoteChat extends BridgeRemoteObject {
	public void message(String sender, String message);
}

class ChatCallback implements BridgeObject {
	public void callback(RemoteChat channel, String channelName) {
		channel.message("steve", "Can write to " + channelName);
	}
}

public class ChannelsServer {

	public static void main(String[] args) throws Exception {
		Bridge bridge = new Bridge().setApiKey("abcdefgh").setHost("localhost").setPort(8090);
		bridge.joinChannel("+rw", obj, callback);
		bridge.joinChannel("+r", obj, false, callback);
	}
}