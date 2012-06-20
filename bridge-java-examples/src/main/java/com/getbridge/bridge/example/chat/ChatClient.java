package com.getbridge.bridge.example.chat;
import com.getbridge.bridge.*;

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
		channel.message("steve", "Flotype Bridge is nifty");
	}
}

interface RemoteAuth extends BridgeRemoteObject {
	public void login(String name, String password, String room, ChatObj
			obj, ChatCallback cb);
}

public class ChatClient {

	public static void main(String[] args) throws Exception {

		Bridge bridge = new Bridge("myapikey");

		RemoteAuth auth = bridge.getService("auth", RemoteAuth.class);
		auth.login("steve", "secret123", "flotype-lovers", new ChatObj(), new ChatCallback());

		bridge.connect();
	}
}
