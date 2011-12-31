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
