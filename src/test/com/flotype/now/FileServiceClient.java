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


public class FileServiceClient extends ServiceClient {

	public FileServiceClient(Reference reference) {
		super(reference);
	}
	
	public void get_localpath(Callback z){
		this.invokeRPC("get_localpath", z);
	}

}
