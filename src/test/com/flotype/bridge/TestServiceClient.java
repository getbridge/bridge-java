package com.flotype.bridge;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


import com.flotype.bridge.Callback;
import com.flotype.bridge.Reference;
import com.flotype.bridge.ServiceClient;
import com.flotype.bridge.serializers.ListSerializer;
import com.flotype.bridge.serializers.MapSerializer;
import com.flotype.bridge.serializers.ReferenceSerializer;
import com.flotype.bridge.serializers.StringSerializer;


public class TestServiceClient extends ServiceClient {

	public TestServiceClient(Reference reference) {
		super(reference);
	}
	
	public void fetchUrl(String s, Callback x){
		this.invokeRPC("fetch_url", s, x);
	}

}
