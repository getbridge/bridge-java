package com.getbridge.bridge;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class Reference implements InvocationHandler {

	private static Logger log = LoggerFactory.getLogger(Reference.class);
	
	private Bridge client;
	private String destinationType;
	private String destinationId;
	private String objectId;
	private String methodName;
	private List<String> operations;

	protected Reference(Bridge client, String dT, String dI, String oI,
			String mN, List<String> operations) {
		this.client = client;
		destinationType = dT;
		destinationId = dI;
		objectId = oI;
		methodName = mN;
		this.operations = operations;
	}

	protected Reference(Reference other) {
		this(other.client, other.destinationType, other.destinationId,
				other.objectId, other.methodName, other.operations);
	}

	public Reference(Bridge bridge, List<String> address,
			List<String> operations) {
		this(bridge, address.get(0), address.get(1), address.get(2), null,
				operations);
		if (address.size() == 4) {
			this.setMethodName(address.get(3));
		}
	}

	public Map<String, Object> toDict() {
		Map<String, Object> dict = new HashMap<String, Object>();
		List<String> address = getAddress();

		dict.put("ref", address);

		if (methodName == null) {
			dict.put("operations", operations);
		}

		return dict;
	}

	private List<String> getAddress() {
		List<String> address = new ArrayList<String>();
		address.add(destinationType);
		address.add(destinationId);
		address.add(objectId);
		if (methodName != null) {
			address.add(methodName);
		}
		return address;
	}

	public Object invoke(Object proxy, Method method, Object[] args) {
		invokeByName(proxy, method.getName(), args);
		if (method.getReturnType() == Void.TYPE) {
			return null;
		} else {
			return Utils.defaultValueForPrimitive(method.getReturnType());
		}
	}

	public Object invokeByName(Object proxy, String methodName, Object[] args) {
		Reference destination = new Reference(this);
		destination.setMethodName(methodName);
		log.info("Calling {}.{}", getAddress(), methodName);
		if (args == null) {
			args = new Object[0];
		}
		client.send(destination, args);
		return null;
	}

	public boolean equals(Object o) {
		if (o instanceof Reference) {
			Reference ref = (Reference) o;
			return this.toDict().equals(ref.toDict());
		}

		return false;
	}

	public int hashCode() {
		return this.toString().hashCode();
	}

	public String toString() {
		return this.toDict().toString();
	}

	// Static helper methods

	static Reference createClientReference(Bridge bridge, String objectId,
			List<String> operations) {
		return new Reference(bridge, "client", bridge.getClientId(), objectId,
				null, operations);
	}
	
	static Reference createRemoteClientReference(Bridge bridge, String clientId, String objectId,
			List<String> operations) {
		return new Reference(bridge, "client", clientId, objectId,
				null, operations);
	}

	static Reference createServiceReference(Bridge bridge, String serviceName,
			List<String> operations) {
		return new Reference(bridge, "named", serviceName, serviceName, null,
				operations);
	}

	static Reference createChannelReference(Bridge bridge, String channelName,
			List<String> operations) {
		return new Reference(bridge, "channel", channelName, "channel:"
				+ channelName, null, operations);
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
