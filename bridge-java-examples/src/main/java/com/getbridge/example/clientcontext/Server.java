package com.getbridge.example.clientcontext;

import java.io.IOException;

import com.getbridge.bridge.Bridge;
import com.getbridge.bridge.BridgeClient;
import com.getbridge.bridge.BridgeObject;
import com.getbridge.bridge.BridgeRemoteObject;

public class Server {
	
	static interface RemotePong extends BridgeRemoteObject {
		public void pong();
	}
	
	static class PingObject implements BridgeObject {
		static Bridge bridge;
		
		public PingObject(Bridge bridge) {
			this.bridge = bridge;
		}
		
		public void ping() {
			System.out.println("PING!");
			BridgeClient client = bridge.getContext();
			client.getService("pong", RemotePong.class).pong();
		}
	}
	
	public static void main(String[] args) throws IOException {
		Bridge bridge = new Bridge("myapikey");
		bridge.publishService("ping", new PingObject(bridge));
		bridge.connect();
	}

}