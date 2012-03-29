package com.flotype.bridge.kitchensink;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.flotype.bridge.*;

public class Main {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		final Bridge bridge = new Bridge()
		.setHost("localhost")
		.setPort(8090)
		.setApiKey("abcdefgh");
		
		BridgeObject handler = new BridgeObject(){
			public void someFn(){}
		};

		BridgeObject callback = new BridgeObject(){
			public void callback(){
				
			}
		};
		
		bridge.publishService("myService", handler, callback); // TEST
		
		bridge.publishService("myService", handler); //TEST
		
		SomeServiceClient service = bridge.getService("someService", SomeServiceClient.class);
		List<String> list = Arrays.asList("foo", "bar");
		Map<String, String> map = new HashMap<String, String>();
		map.put("foo", "bar");
		service.someFn(1, 1.0f, "foo", true, null, list, map); // TEST

		SomeServiceClient channel = bridge.getChannel("someChannel", SomeServiceClient.class);
		channel.someFn(1, 1.0f, "foo", true, null, list, map);

		bridge.joinChannel("myChannel", handler, callback); // TEST
		bridge.joinChannel("myChannel", handler); // TEST
		
		bridge.leaveChannel("myChannel", handler, callback); // TEST
		bridge.leaveChannel("myChannel", handler); // TEST
		
		service.someFn(handler, callback); // TEST
		
		Map<String, BridgeObject> serviceMap = new HashMap<String, BridgeObject>();
		serviceMap.put("foo", callback);
		List<Object> someList = new ArrayList<Object>();
		someList.add(callback);
		service.someFn(someList, serviceMap); //TEST
		
		
		bridge.connect();

	}

}
