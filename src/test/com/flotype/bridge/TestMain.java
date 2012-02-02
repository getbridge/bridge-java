package com.flotype.bridge;

import com.flotype.bridge.Callback;
import com.flotype.bridge.Bridge;
import com.flotype.bridge.Reference;


public class TestMain {


	public static void main (String[] args) throws Exception {

		final Bridge bridge = new Bridge("localhost", 8090);

		bridge.onReady(new BridgeEventHandler() {
			public void onReady() {
				Reference chat = bridge.getService("chat");
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
	}
}
