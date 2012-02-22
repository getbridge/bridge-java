package com.flotype.bridge.example;


import com.flotype.bridge.Callback;
import com.flotype.bridge.Reference;
import com.flotype.bridge.ServiceClient;


public class TestServiceClient extends ServiceClient {

	public TestServiceClient(Reference reference) {
		super(reference);
	}

	public void fetchUrl(String s, Callback x){
		this.invokeRPC("fetch_url", s, x);
	}

}
