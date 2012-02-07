package com.flotype.bridge;
import java.util.List;


public class ReferenceFactory {

	static ReferenceFactory theFactory;
	static Bridge client;

	protected ReferenceFactory(Bridge client) {
		ReferenceFactory.client = client;
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

	public Reference generateReference(List<String> value) {
		return new Reference(value, client);
	}

	public Reference generateReference(Reference reference) {
		return new Reference(reference);
	}
	
	public Reference generateReference(){
		return new Reference(null, client);
	}

}