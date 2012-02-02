package com.flotype.bridge;

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
