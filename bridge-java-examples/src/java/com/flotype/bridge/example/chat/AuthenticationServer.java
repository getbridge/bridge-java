package com.flotype.bridge.example.chat;

import com.flotype.bridge.*;

class AuthObj implements BridgeObject {
	private Bridge bridge;
	
	public AuthObj(Bridge bridge){
		this.bridge = bridge;
	}

	public void login(String name, String password, String room, BridgeRemoteObject
			obj, BridgeRemoteObject callback){
		if(password.equals("secret123")){
			System.out.println("Welcome!");
			bridge.joinChannel(room, obj, callback);
		} else {
			System.out.println("Sorry!");
		}
	}
}

public class AuthenticationServer {

	public static void main(String[] args) throws Exception {

		Bridge bridge = new Bridge()
		.setApiKey("abcdefgh");
		bridge.connect();

		bridge.publishService("auth", new AuthObj(bridge));
	}
}