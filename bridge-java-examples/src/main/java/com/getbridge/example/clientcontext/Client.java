package com.getbridge.example.clientcontext;

import java.io.IOException;

import com.getbridge.bridge.Bridge;
import com.getbridge.bridge.BridgeObject;
import com.getbridge.bridge.BridgeRemoteObject;

public class Client {
	
	static interface RemotePing extends BridgeRemoteObject {
		public void ping();
	}
	
	static class PongObject implements BridgeObject {
		
		public void pong() {
			System.out.println("PONG!");
		}
	}
	
	public static void main(String[] args) throws IOException {
		Bridge bridge = new Bridge("myapikey");
		bridge.storeService("pong", new PongObject());
		bridge.connect();
	}

}