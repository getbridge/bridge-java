package com.flotype.bridge.example.adder;
import com.flotype.bridge.Bridge;
import com.flotype.bridge.BridgeEventHandler;
import com.flotype.bridge.Reference;


public class AdderTest {

	private final static Bridge bridgeServer = new Bridge().setHost("localhost").setPort(8090);
	private static final Bridge bridgeClient = new Bridge().setHost("localhost").setPort(8090);
	
	public static void main (String[] args) throws Exception {
		startChannel();
		streamChannel();
	}
	
	public static void startChannel() {
		bridgeServer.setEventHandler(new BridgeEventHandler() {
			
			public void onReady() {
                bridgeServer.joinChannel("friendlyPerson", new AdderChannel());
            }
			
		});
		
		bridgeServer.connect();
	}
	
	public static void streamChannel() {
		bridgeClient.setEventHandler(new BridgeEventHandler() {
			
			public void onReady() {
				Reference channel = bridgeClient.getChannel("friendlyPerson");
				(new AdderHandler(channel)).greeting();
			}
			
		});
		
		bridgeClient.connect();
	}
}