package com.flotype.bridge.example;

import com.flotype.bridge.Bridge;
import com.flotype.bridge.BridgeObject;
import com.flotype.bridge.BridgeRemoteObject;
import com.flotype.bridge.Utils;

public class TestMain {

	public static void main(String[] args) throws Exception {

		Bridge bridge = new Bridge()
		.setApiKey("abcdefgh");
		bridge.connect();

		BridgeObject authHandler = new BridgeObject() {
			public void join(String room, String password){
				if(password.equals("secret123")){
					System.out.println("Welcome!");
				} else {
					System.out.println("Sorry!");
				}
			}
		};

		bridge.publishService("auth", authHandler);
		
		System.out.println(Utils.contains(BridgeRemoteObject.class.getInterfaces(), BridgeRemoteObject.class));
	}
}