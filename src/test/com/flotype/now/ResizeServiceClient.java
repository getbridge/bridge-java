package com.flotype.now;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


import com.flotype.now.Callback;
import com.flotype.now.Reference;
import com.flotype.now.ServiceClient;
import com.flotype.now.serializers.ListSerializer;
import com.flotype.now.serializers.MapSerializer;
import com.flotype.now.serializers.ReferenceSerializer;
import com.flotype.now.serializers.StringSerializer;


public class ResizeServiceClient extends ServiceClient {

	public ResizeServiceClient(Reference reference) {
		super(reference);
	}
	
	public void resize(Reference file, int x, int y, Callback z){
		this.invokeRPC("resize", file, x, y, z);
	}

}
