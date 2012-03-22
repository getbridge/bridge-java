package com.flotype.bridge;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class Reference implements InvocationHandler{

	private Bridge client;
	private String destinationType;
	private String destinationId;
	private String objectId;
	private String methodName;
	private List<String> operations;
	

	
	protected Reference(Bridge client, String dT, String dI, String oI, String mN, List<String> operations) {
		this.client = client;
		destinationType = dT;
		destinationId = dI;
		objectId = oI;
		methodName = mN;
		this.operations = operations;
	}
	
	protected Reference(Reference other){
		this(other.client, other.destinationType, other.destinationId, other.objectId, other.methodName, other.operations);
	}
	
	public Reference(Bridge bridge, List<String> address, List<String> operations) {
		this(bridge, address.get(0), address.get(1), address.get(2), null, operations);
		if(address.size() == 4){
			this.setMethodName(address.get(3));
		}
	}

	public Map<String, Object> toDict(){
		Map<String, Object> dict = new HashMap<String, Object>();
		List<String> address = new ArrayList<String>();
		address.add(destinationType);
		address.add(destinationId);
		address.add(objectId);
		if(methodName != null){
			address.add(methodName);
		} 
		
		dict.put("ref", address);
		
		if(methodName == null){
			dict.put("operations", operations);
		}
		
		return dict;
	}

	public Object invoke(Object proxy, Method method, Object[] args) {
		return invokeByName(proxy, method.getName(), args);
	}
	
	public Object invokeByName(Object proxy, String methodName, Object[] args) {
		Reference destination = new Reference(this);
		destination.setMethodName(methodName);
		client.send(destination, args);
		return null;
	}	
	
	// Static helper methods
	
	static Reference createClientReference(Bridge bridge, String objectId, List<String> operations){
		return new Reference(bridge, "client", bridge.getClientId(), objectId, null, operations);
	}
	
	static Reference createServiceReference(Bridge bridge, String serviceName, List<String> operations){
		return new Reference(bridge, "named", serviceName, serviceName, null, operations);
	}
	
	static Reference createChannelReference(Bridge bridge, String channelName, List<String> operations){
		return new Reference(bridge, "channel", channelName, "channel:"+channelName, null, operations);
	}
	
	// Setters and getters
	
	public String getDestinationType() {
		return destinationType;
	}

	public void setDestinationType(String destinationType) {
		this.destinationType = destinationType;
	}

	public String getDestinationId() {
		return destinationId;
	}

	public void setDestinationId(String destinationId) {
		this.destinationId = destinationId;
	}

	public String getObjectId() {
		return objectId;
	}

	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public List<String> getOperations() {
		return operations;
	}

}
