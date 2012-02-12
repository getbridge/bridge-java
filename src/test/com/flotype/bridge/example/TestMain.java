package com.flotype.bridge.example;

import com.flotype.bridge.Bridge;
import com.flotype.bridge.BridgeEventHandler;
import com.flotype.bridge.Reference;
import com.flotype.bridge.Service;


public class TestMain {


	public static void main (String[] args) throws Exception {

		final Bridge bridge = new Bridge()
			.setHost("localhost")
			.setPort(8090);
		bridge.setEventHandler(new BridgeEventHandler() {
			public void onReady() {
				Reference chat = bridge.getService("chatserver");
				(new ChatServiceClient(chat)).join(
						"lobby", 
						new Service(){
							public void msg(String s, String t){
								System.out.println(s + ":" + t);
							}
						},
						new Service(){
							public void callback(Reference l, String name){
								System.out.println("JOINED " + name);
								(new ChatChannelClient(l)).msg("peter piper", "picked a peck of pickled peppers");
							}
						}
				);

			}
		});
		
		bridge.connect();
		
	}
}
