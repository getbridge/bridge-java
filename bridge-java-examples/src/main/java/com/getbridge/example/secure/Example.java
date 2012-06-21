package com.getbridge.example.secure;

import java.io.IOException;

import com.getbridge.bridge.Bridge;

public class Example {


	public static void main(String[] args) throws IOException {
		
		// Passing true as the second argument enables SSL connection
		Bridge bridge = new Bridge("myapikey", true);
		bridge.connect();
	}

}
