package com.getbridge.example.channels;

import java.io.IOException;

import com.getbridge.bridge.Bridge;
import com.getbridge.bridge.BridgeObject;
import com.getbridge.bridge.BridgeRemoteObject;

public class Client {

	static class ChatObj implements BridgeObject {
		public void message(String sender, String message) {
			System.out.println(sender + ":" + message);
		}
	}

	static interface RemoteChat extends BridgeRemoteObject {
		public void message(String sender, String message);
	}

	static class ChatCallback implements BridgeObject {
		public void callback(RemoteChat channel, String channelName) {
			// The following RPC call will fail because client was not joined to channel with write permissions
			channel.message("steve", "This should not work.");
		}
	}

	static interface RemoteAuth extends BridgeRemoteObject {
		public void join(String channelName,  BridgeObject obj, BridgeObject callback);
		public void joinWriteable(String channelName, String secretWord, BridgeObject obj, BridgeObject callback);
	}
	
	public static void main(String[] args) throws IOException {
		
		Bridge bridge = new Bridge("myapikey");
		RemoteAuth auth = bridge.getService("auth", RemoteAuth.class);
		auth.join("flotype-lovers", new ChatObj(), new ChatCallback());
		bridge.connect();
	}

}
