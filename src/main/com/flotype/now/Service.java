package com.flotype.now;

import java.util.ArrayList;

public class Service {
	private Reference reference = null;
	public void createReference(String connectionId, String name) {
		ArrayList<String> path = new ArrayList<String>();
		path.add(connectionId);
		path.add(name);
		reference = ReferenceFactory.getFactory().generateReference(path);
	}
	
	public Reference getReference(){
		return reference;
	}
}
