package com.flotype.bridge;
import java.io.IOException;

public class ServiceClient {
	Reference reference;

	public ServiceClient(Reference reference){
		this.reference = reference;
	}

	protected void invokeRPC(String methodName, Object... args){
		try {
			this.reference.invokeRPC(methodName, args);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}