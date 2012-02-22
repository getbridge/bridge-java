package com.flotype.bridge.example;

import com.flotype.bridge.Callback;
import com.flotype.bridge.Reference;
import com.flotype.bridge.ServiceClient;


public class FileServiceClient extends ServiceClient {

	public FileServiceClient(Reference reference) {
		super(reference);
	}

	public void get_localpath(Callback z){
		this.invokeRPC("get_localpath", z);
	}

}
