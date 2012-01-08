package com.flotype.now;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.flotype.now.Callback;
import com.flotype.now.Client;
import com.flotype.now.Reference;


public class TestMain {
 
	
	public static void main (String[] args) {
	
		Client client = null;
		
		try {
			client = new Client("localhost", 8090);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		try {
			client.connect();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		/*
		client.joinChannel("chat", new Callback(){
			
			
			
		});
		*/
		
		client.publishService("foo", new TestService());
		
		TestServiceClient x = new TestServiceClient(client.getService("webpull"));
		final ResizeServiceClient y = new ResizeServiceClient(client.getService("resize"));
		
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
}
