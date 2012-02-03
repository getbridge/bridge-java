package com.flotype.bridge;

import java.lang.reflect.Method;
import java.util.ArrayList;

public class Service {
	private Reference reference = null;
	private String[] methodNames;

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
	
	public String[] getMethodNames(){
		if(methodNames == null) {
			Class<?> serviceClass = this.getClass();
			
			Method[] methods = serviceClass.getDeclaredMethods();
			methodNames = new String[methods.length];
			
			for(int i = 0; i < methods.length; i++) {
				methodNames[i] = methods[i].getName();
			}
		}
		return methodNames;
	}
}
