package com.flotype.bridge.example;

import com.flotype.bridge.BridgeObject;

public class ChatService implements BridgeObject {
	public void msg(String name, String s) {
		System.out.println(name);
	}
}
