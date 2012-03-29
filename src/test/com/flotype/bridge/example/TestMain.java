package com.flotype.bridge.example;

import com.flotype.bridge.Bridge;
import com.flotype.bridge.BridgeEventHandler;
import com.flotype.bridge.BridgeObject;


public class TestMain {


	public static void main (String[] args) throws Exception {

		final Bridge bridge = new Bridge()
			.setHost("localhost")
			.setPort(8090)
			.setApiKey("abcdefgh");
		
		ChatServiceClient chat = bridge.getService("chatserver", ChatServiceClient.class);
		chat.join(
				"lobby", 
				new BridgeObject(){
					public void msg(String s, String t){
						System.out.println(s + ":" + t);
					}
				},
				new BridgeObject(){
					public void callback(ChatChannelClient l, String name){
						System.out.println("JOINED " + name);
						l.msg("peter piper", "picked a peck of pickled peppers");
					}
				}
		);
		
		bridge.connect();
	}
}
