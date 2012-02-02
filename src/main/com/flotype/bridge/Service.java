package com.flotype.bridge;

import java.util.ArrayList;

public class Service {
	private Reference reference = null;

	// Create named reference
	protected void createReference(String name) {
		reference = ReferenceFactory.getFactory().generateReference();
		reference.setServiceName(name);
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
