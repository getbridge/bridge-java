package com.flotype.bridge;

import com.flotype.bridge.Callback;
import com.flotype.bridge.Bridge;
import com.flotype.bridge.Reference;


public class TestMain {


	public static void main (String[] args) throws Exception {

		final Bridge bridge = new Bridge("localhost", 8090);

		bridge.onReady(new BridgeEventHandler() {
			public void onReady() {
				Reference chatServiceRef = bridge.getService("chatserver");
				
				(new ChatServiceClient(chatServiceRef)).join("lobby", new Callback() {
					public void msg(String message, String person){
						System.out.println(message);
					}
				}, new Callback(){
					public void callback(Reference roomRef, String roomName){
						System.out.println("Joined " + roomName);
					}
				});
				
				
			}
		});
	}
}
