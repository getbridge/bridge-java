package com.getbridge.example.services;

import java.io.IOException;

import com.getbridge.bridge.Bridge;
import com.getbridge.bridge.BridgeObject;
import com.getbridge.bridge.BridgeRemoteObject;

/* Retrieving a Bridge service 

 This can be done from any Bridge client connected to the same 
 Bridge server, regardless of language.
 If multiple clients publish a Bridge service, getService will 
 retrieve from the publisher with the least load. */

class CallbackObject implements BridgeObject {
	public void callback(String response) {
		System.out.println(response);
	}
}

interface RemoteTestService extends BridgeRemoteObject {
	public void ping(CallbackObject cb);
}

public class Client {

	public static void main(String[] args) throws IOException {
		Bridge bridge = new Bridge("mypubkey");
		bridge.connect();

		RemoteTestService test = bridge.getService("testService", RemoteTestService.class);
		test.ping(new CallbackObject());

	}

}
