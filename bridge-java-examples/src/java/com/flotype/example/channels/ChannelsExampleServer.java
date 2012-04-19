package com.flotype.example.channels;

import java.io.IOException;

import com.flotype.bridge.Bridge;
import com.flotype.bridge.BridgeObject;

/* Joining a Bridge channel

 In order to join a Bridge channel, clients must provide the name 
 of the channel to join and a handler object on which RPC calls 
 in the channel will act on. Note that the client that is joined 
 to the channel is whoever created the handler, not necessarily the 
 client executing the join command. This means clients can join other
 clients to channels by having a reference to an object of theirs.

 Only Bridge clients using the private API key may call the join command. 
 However, those clients may join other clients using the public API key on their behalf. */
	
class TestChannel implements BridgeObject {
	public void log(String message){
		System.out.println("Got message:  " + message);
	}
}

public class ChannelsExampleServer {

	public static void main(String[] args) throws IOException {
		Bridge bridge = new Bridge()
		.setApiKey("myprivkey");
		bridge.connect();

		bridge.joinChannel("testChannel", new TestChannel());

	}

}
