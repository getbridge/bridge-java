package com.flotype.bridge.example.channels;

import java.io.IOException;

import com.flotype.bridge.Bridge;
import com.flotype.bridge.BridgeObject;
import com.flotype.bridge.BridgeRemoteObject;

public class ClientWriteable {

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
			// The following RPC call will succeed because client was joined to channel with write permissions
			channel.message("steve", "Can write to channel:" + channelName);
		}
	}

	static interface RemoteAuth extends BridgeRemoteObject {
		public void join(String channelName,  BridgeObject obj, BridgeObject callback);
		public void joinWriteable(String channelName, String secretWord, BridgeObject obj, BridgeObject callback);
	}
	
	public static void main(String[] args) throws IOException {
		
		Bridge bridge = new Bridge("myapikey");
		RemoteAuth auth = bridge.getService("auth", RemoteAuth.class);
		auth.joinWriteable("flotype-lovers", "secret123", new ChatObj(), new ChatCallback());
		bridge.connect();
	}

}
