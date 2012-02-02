package com.flotype.bridge;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


import com.flotype.bridge.Callback;
import com.flotype.bridge.Reference;
import com.flotype.bridge.ServiceClient;


public class ResizeServiceClient extends ServiceClient {

	public ResizeServiceClient(Reference reference) {
		super(reference);
	}

	public void resize(Reference file, int x, int y, Callback z){
		this.invokeRPC("resize", file, x, y, z);
	}

}
