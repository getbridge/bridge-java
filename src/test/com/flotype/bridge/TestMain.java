package com.flotype.bridge;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.flotype.bridge.Callback;
import com.flotype.bridge.Bridge;
import com.flotype.bridge.Reference;


public class TestMain {
 
	
	public static void main (String[] args) throws Exception {
	
		final Bridge bridge = new Bridge("localhost", 8090);
		
		bridge.onReady(new BridgeEventHandler() {
			public void onReady() {
				bridge.publishService("foo", new TestService());

				TestServiceClient x = new TestServiceClient(bridge.getService("webpull"));
				final ResizeServiceClient y = new ResizeServiceClient(bridge.getService("resize"));

				Callback s = new Callback(){
					public void callback(Reference x) {
						y.resize(x, 2000, 2300, new Callback(){
							public void callback(Reference file) {
								FileServiceClient z = new FileServiceClient(file);
								z.get_localpath(new Callback(){
									public void callback(String result) {
										System.out.println("RESULT::: " + result);
									}
								});
							}
						});
					}
				};
				x.fetchUrl("http://ericzhang.com/images/kb.jpg", s);

			}	
		});
	}	   
}
