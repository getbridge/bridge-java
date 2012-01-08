package com.flotype.bridge;
import java.util.List;
import java.util.Map;


public class ReferenceFactory {
	
	static ReferenceFactory theFactory;
	static Bridge client;
	
	protected ReferenceFactory(Bridge client) {
		this.client = client;
	}

	protected static void createFactory(Bridge client){
		theFactory = new ReferenceFactory(client);
	}
	
	protected static ReferenceFactory getFactory(){
		if(theFactory == null){
			throw new Error("ReferenceFactory uninitialized");
		} else {
			return theFactory;
		}
	}
	
	protected Reference generateReference(String value){
		return new Reference(value, client);
	}

	public Reference generateReference(List<String> value) {
		return new Reference(value, client);
	}

}