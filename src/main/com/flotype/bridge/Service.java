package com.flotype.bridge;

import java.util.ArrayList;

public class Service {
	private Reference reference = null;

	// Create named reference
	protected void createReference(String name) {
		ArrayList<String> path = new ArrayList<String>();
		path.add(ReferenceFactory.client.getConnectionId());
		path.add(name);
		reference = ReferenceFactory.getFactory().generateReference(path);
	}

	// Create anonymous reference if there isn't one
	public void ensureReference() {
		if(!hasReference()) {
			ReferenceFactory.client.publishService(this);
		}
	}

	public boolean hasReference () {
		return reference != null;
	}

	public Reference getReference(){
		return reference;
	}
}
