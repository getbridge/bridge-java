package com.flotype.bridge;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import java.lang.reflect.Proxy;

public class Bridge {
	
	// Options
	BridgeEventHandler eventHandler = null;
	private static Log log = LogFactory.getLog(Bridge.class);
	protected Dispatcher dispatcher = new Dispatcher(this);
	boolean ready = false;
	private Connection connection;
	
	public Bridge() {
		connection = new Connection(this);
		
		this.setHost(Utils.DEFAULT_HOST);
		this.setPort(Utils.DEFAULT_PORT);
		this.setEventHandler(Utils.DEFAULT_EVENT_HANDLER);
		this.setReconnect(Utils.DEFAULT_RECONNECT);
		
		dispatcher.storeObject("system", new SystemService(this, dispatcher));
	}

	public Bridge(String host, Integer port, String apiKey, BridgeEventHandler eventHandler, boolean reconnect) {
		this();
		this.setHost(host);
		this.setPort(port);
		this.setApiKey(apiKey);
		this.setEventHandler(eventHandler);
		this.setReconnect(reconnect);
	}

	public Bridge(String host, String apiKey, BridgeEventHandler eventHandler, boolean reconnect) {
		this(host, null, apiKey, eventHandler, reconnect);
	}

	public boolean connect() throws IOException {
		this.connection.connect();
		return true;
	}	
	
	protected void send(Reference destination, Object[] args) {
		String msg = JSONCodec.createSEND(this, destination, args);
		this.connection.send(msg);
	}
	
	public void publishService(String name, BridgeObjectBase bridgeObject) {
		BridgeObject callback = null;
		publishService(name, bridgeObject, callback);
	}
	public void publishService(String name, BridgeObjectBase bridgeObject, BridgeObjectBase callback) {
		if(name.equals("system")) {
			log.error("Invalid service name: " + name);
			return;
		}
		
		if(bridgeObject instanceof BridgeObject) {
			dispatcher.storeObject(name, bridgeObject);
		}
		
		Reference callbackRef = null;
		if(callback instanceof BridgeObject) {
			callbackRef = dispatcher.storeRandomObject(callback);
		} else if (callback instanceof BridgeRemoteObject){
			callbackRef = (Reference) Proxy.getInvocationHandler(callback);
		}
		String msg = JSONCodec.createJWP(this, name, callbackRef);
		this.connection.send(msg);
	}
	
	public <T> T getService(String serviceName, Class<T> serviceInterface){
		Reference result = Reference.createServiceReference(this, serviceName, Utils.getMethods(serviceInterface));
		return Utils.createProxy(result, serviceInterface);
	}

	public <T> T getChannel(String channelName, Class<T> channelInterface){
		String msg = JSONCodec.createGETCHANNEL(this, channelName);
		this.connection.send(msg);
		
		Reference result = Reference.createChannelReference(this, channelName, Utils.getMethods(channelInterface));
		return Utils.createProxy(result, channelInterface);
	}
	
	public void joinChannel(String channelName, BridgeObject handler) {
		joinChannel(channelName, handler, null);
	}
	
	public void joinChannel(String channelName, BridgeObjectBase handler, BridgeObjectBase callback) {
		Reference handlerRef = null;
		if(handler instanceof BridgeObject) {
			handlerRef = dispatcher.storeRandomObject(handler);
		} else if (handler instanceof BridgeRemoteObject) {
			handlerRef = (Reference) Proxy.getInvocationHandler(handler);
		}
		Reference callbackRef = null;
		if(handler instanceof BridgeObject) {
			callbackRef = dispatcher.storeRandomObject(callback);
		} else if (callback instanceof BridgeRemoteObject) {
			callbackRef = (Reference) Proxy.getInvocationHandler(callback);
		}
		String msg = JSONCodec.createJC(this, channelName, handlerRef, callbackRef);
		this.connection.send(msg);
	}

	public void leaveChannel(String name, BridgeObject handler) {
		leaveChannel(name, handler, null);
	}
	
	public void leaveChannel(String name, BridgeObject handler, BridgeObject callback) {
		Reference handlerRef = dispatcher.storeObject(name, handler);
		Reference callbackRef = dispatcher.storeRandomObject(callback);
		String msg = JSONCodec.createLEAVECHANNEL(this, name, handlerRef, callbackRef);
		this.connection.send(msg);
	}

	public Bridge setApiKey(String apiKey) {
		this.connection.setApiKey(apiKey);
		return this;
	}

	public Bridge setHost(String host) {
		this.connection.setHost(host);
		return this;
	}

	public Bridge setPort(Integer port) {
		this.connection.setPort(port);
		return this;
	}

	public Bridge setEventHandler(BridgeEventHandler eventHandler) {
		this.eventHandler = eventHandler;
		return this;
	}

	public Bridge setReconnect(boolean reconnect) {
		this.connection.setReconnect(reconnect);
		return this;
	}
	
	protected void onDisconnect(){
		this.ready = false;
	}
	
	protected void onReady(){
		this.ready = true;
		dispatcher.fixClientIds(this.connection.clientId);
		this.eventHandler.onReady();
	}

	protected void onReconnect() {
		
	}

	protected void onConnected() {
		
	}

	protected String getClientId() {
		return connection.clientId;
	}

}
