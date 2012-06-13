package com.flotype.bridge.example.channels;

import com.flotype.bridge.*;

class ChannelsChatObj implements BridgeObject {
	public void message(String sender, String message) {
		System.out.println(sender + ":" + message);
	}
}

interface ChannelsRemoteChat extends BridgeRemoteObject {
	public void message(String sender, String message);
}

class ChannelsChatCallback implements BridgeObject {
	public void callback(ChannelsRemoteChat channel, String channelName) {
		channel.message("steve", "Can write to " + channelName);
	}
}

public class ChannelsServer {

	public static void main(String[] args) throws Exception {
		Bridge bridge = new Bridge("localhost", 8090, "abcdefgh");
		ChannelsChatObj obj = new ChannelsChatObj();
		ChannelsChatCallback callback = new ChannelsChatCallback();
		bridge.joinChannel("+rw", obj, callback);
		bridge.joinChannel("+r", obj, false, callback);
	}
}
