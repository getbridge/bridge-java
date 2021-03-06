package com.getbridge.example.simplechannels;

import java.io.IOException;

import com.getbridge.bridge.Bridge;
import com.getbridge.bridge.BridgeRemoteObject;


 /* Getting and calling a Bridge channel

 This can be done from any Bridge client connected to the same 
 Bridge server, regardless of language.
 When a function call is made to a channel object, the requested
 function will be executed on everyone in the channel
 
 Only Bridge clients using the private API key may call the join command. */

	
interface RemoteTestChannel extends BridgeRemoteObject {
	public void log(String message);
}

public class Client {

	public static void main(String[] args) throws IOException {
		Bridge bridge = new Bridge("myprivkey");
		bridge.connect();

		RemoteTestChannel testChannel = bridge.getChannel("testChannel", RemoteTestChannel.class);
		testChannel.log("Hello world!");
	}

}
