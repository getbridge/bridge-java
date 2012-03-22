package com.flotype.bridge.example;

import com.flotype.bridge.Service;

public class ChatService implements Service {
	public void msg(String name, String s){
		System.out.println(name);
	}
}
