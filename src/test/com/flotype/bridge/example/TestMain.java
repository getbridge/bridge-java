package com.flotype.bridge.example;

import java.io.IOException;

import com.flotype.bridge.Bridge;
import com.flotype.bridge.BridgeEventHandler;
import com.flotype.bridge.Reference;
import com.flotype.bridge.Service;


public class TestMain {


	public static void main (String[] args) throws Exception {

		final Bridge bridge = new Bridge()
			.setApiKey("abcdefgh");
		bridge.connect();
		
		ChatServiceClient chat = bridge.getService("chatserver", ChatServiceClient.class);
		chat.join(
				"lobby", 
				new Service(){
					public void msg(String s, String t){
						System.out.println(s + ":" + t);
					}
				},
				new Service(){
					public void callback(ChatChannelClient l, String name){
						System.out.println("JOINED " + name);
						l.msg("peter piper", "picked a peck of pickled peppers");
					}
				}
		);
		
		final ChatChannelClient c = new ChatChannelClient(bridge.getChannel("lobby"));
		
		bridge.publishService("javachat", new Service(){
			public void send(String x){
				c.msg("java", x);
			}
			
		});
		
	}
}
