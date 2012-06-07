package com.flotype.bridge.network;

import java.util.LinkedList;
import java.util.Queue;

public class SocketBuffer implements Socket {
	
	private Queue<String> queue;
	
	public SocketBuffer() {
		queue = new LinkedList<String>();
	}

	@Override
	public void send(String message) {
		queue.add(message);
	}

	public void processQueue(Socket destination, String clientId) {
		for (String str : queue) {
			// Replace the 'null' in the JSON reference address with the
			// now-known client ID
			destination.send(str.replace("\"ref\":[\"client\",null,",
					"\"ref\":[\"client\",\"" + clientId + "\","));
		}
		queue.clear();
	}

}