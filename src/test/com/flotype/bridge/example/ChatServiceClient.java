package com.flotype.bridge.example;

import com.flotype.bridge.Reference;
import com.flotype.bridge.Service;
import com.flotype.bridge.ServiceClient;

public class ChatServiceClient extends ServiceClient {

	public ChatServiceClient(Reference reference) {
		super(reference);
	}
	
	public void msg(String s){
		this.invokeRPC("msg", s);
	}

	public void join(String string, Service handler, Service callback) {
		// TODO Auto-generated method stub
		invokeRPC("join", string, handler, callback);
	}

}
