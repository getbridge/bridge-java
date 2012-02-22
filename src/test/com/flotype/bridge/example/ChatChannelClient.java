package com.flotype.bridge.example;

import com.flotype.bridge.Reference;
import com.flotype.bridge.ServiceClient;

public class ChatChannelClient extends ServiceClient {

	public ChatChannelClient(Reference reference) {
		super(reference);
		// TODO Auto-generated constructor stub
	}

	public void msg(String string, String string2) {
		invokeRPC("msg", string, string2);
	}

}
