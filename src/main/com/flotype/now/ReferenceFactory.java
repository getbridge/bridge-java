package com.flotype.now;
import java.util.List;
import java.util.Map;
import java.util.UUID;


public class ReferenceFactory {
	
	static ReferenceFactory theFactory;
	static UUID id;
	
	protected ReferenceFactory(UUID id) {
		this.id = id;
	}

	protected static void createFactory(UUID id){
		theFactory = new ReferenceFactory(id);
	}
	
	protected static ReferenceFactory getFactory(){
		if(theFactory == null){
			throw new Error("ReferenceFactory uninitialized");
		} else {
			return theFactory;
		}
	}
	
	protected Reference generateReference(String value){
		return new Reference(value, id);
	}

	public Reference generateReference(List<String> value) {
		return new Reference(value, id);
	}

}