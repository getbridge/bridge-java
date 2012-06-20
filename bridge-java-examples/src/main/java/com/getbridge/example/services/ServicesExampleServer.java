package com.getbridge.example.services;

import java.io.IOException;

import com.getbridge.bridge.Bridge;
import com.getbridge.bridge.BridgeObject;
import com.getbridge.bridge.BridgeRemoteObject;

/*Publishing a Bridge service

Any Java object implementing BridgeObject can be published. A published service 
can be retrieved by any Bridge client with the same API key pair.

Responses can be sent to the caller by acceping a callback and calling a method on it.

Only Bridge clients using the prviate API key may publish services. */

interface RemoteCallback extends BridgeRemoteObject {
	public void callback(String response);
}

class TestService implements BridgeObject {
	public void ping(RemoteCallback cb){
		System.out.println("Received ping request!");
		cb.callback("Pong");
	}
}

public class ServicesExampleServer {

	public static void main(String[] args) throws IOException {
		Bridge bridge = new Bridge("myprivkey");
		bridge.connect();

		bridge.publishService("testService", new TestService());

	}

}
