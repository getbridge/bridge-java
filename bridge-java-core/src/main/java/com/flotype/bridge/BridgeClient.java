package com.flotype.bridge;

public class BridgeClient {
	public final String clientId;
	private final Bridge bridge;
	protected BridgeClient(Bridge bridge, String id) {
		this.clientId = id;
		this.bridge = bridge;
	}
	public <T> T getService(String serviceName, Class<T> serviceInterface) {
		Reference result = Reference.createClientReference(bridge, serviceName,
				Utils.getMethods(serviceInterface));
		return Utils.createProxy(result, serviceInterface);
	}
}
