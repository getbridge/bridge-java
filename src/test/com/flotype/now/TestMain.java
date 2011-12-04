package com.flotype.now;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.flotype.now.Client;


public class TestMain {
	
	public static void main(String[] args){
		try {
			Client client = new Client();
			client.connect();
			client.joinWorkerPool("foo");
			client.registerService("foo", new TestService());
			
			TestServiceClient s = new TestServiceClient(client.getDummyReference());
			Map<String, Object> m = new HashMap<String, Object>();
			m.put("foo", "bar");
			m.put("qux", Arrays.asList(new String[]{"I", "am", "crazy"}));
			m.put("baz", client.getDummyReference());
			
			s.foo("s");
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
