package com.flotype.now;
import java.util.List;
import java.util.Map;


public class ReferenceFactory {
	
	static ReferenceFactory theFactory;
	static Client client;
	
	protected ReferenceFactory(Client client) {
		this.client = client;
	}

	protected static void createFactory(Client client){
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