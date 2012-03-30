package com.flotype.bridge.example.adder;
import java.io.IOException;

import com.flotype.bridge.Bridge;
import com.flotype.bridge.BridgeEventHandler;


public class AdderTest {

	private final static Bridge bridgeServer = new Bridge().setHost("localhost").setPort(8090);
	private static final Bridge bridgeClient = new Bridge().setHost("localhost").setPort(8090);
	
	public static void main (String[] args) throws Exception {
		startChannel();
		streamChannel();
	}
	
	public static void startChannel() throws IOException {
		bridgeServer.setEventHandler(new BridgeEventHandler() {
			
			public void onReady() {
                bridgeServer.joinChannel("friendlyPerson", new AdderChannel());
            }
			
		});
		
		bridgeServer.connect();
	}
	
	public static void streamChannel() throws IOException {
		bridgeClient.setEventHandler(new BridgeEventHandler() {
			
			public void onReady() {
				AdderHandler adder = bridgeClient.getChannel("friendlyPerson", AdderHandler.class);
				adder.greeting();
			}
			
		});
		
		bridgeClient.connect();
	}
}