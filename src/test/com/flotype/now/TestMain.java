package com.flotype.now;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.flotype.now.Client;


public class TestMain {
	
	public static void main(String[] args) throws Exception{
		/*try {
			Client client = new Client();
			client.connect();
			client.joinWorkerPool("foo");

			client.registerService("foo", new TestService());
			
			TestServiceClient s = new TestServiceClient(client.getDummyReference("webpull"));
			s.fetchUrl("s");
			
		} catch (IOException e) {
			e.printStackTrace();
		}*/
		
		Client client = null;
	
		client = new Client("localhost", 8090);
		
		client.connect();
		

		
		Thread.sleep(1000);
		
		
		client.joinService("foo", new TestService());
		
		TestServiceClient x = new TestServiceClient(client.getDummyReference("webpull"));
		
		Client.Callback s = client.new Callback(){
			void callback(String data) {
				System.out.println(data);
			}
		};
		x.fetchUrl("s", s);
		
	}
	
}
