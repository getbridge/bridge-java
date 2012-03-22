package com.flotype.bridge;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

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
	
	public void publishService(String name, Service service) {
		publishService(name, service, null);
	}

	public void publishService(String name, Service service, Service callback) {

		if(name.equals("system")) {
			log.error("Invalid service name: " + name);
			return;
		}

		dispatcher.storeObject(name, service);
		Reference callbackRef = dispatcher.storeRandomObject(callback);
		String msg = JSONCodec.createJWP(this, name, callbackRef);
		this.connection.send(msg);
	}
	
	public <T> T getService(String serviceName, Class<T> serviceClass){
		Reference result = Reference.createServiceReference(this, serviceName, Utils.getMethods(serviceClass));
		return Utils.createProxy(result, serviceClass);
	}

	public <T> T getChannel(String channelName, Class<T> serviceClass){
		String msg = JSONCodec.createGETCHANNEL(this, channelName);
		this.connection.send(msg);
		
		Reference result = Reference.createChannelReference(this, channelName, Utils.getMethods(serviceClass));
		return Utils.createProxy(result, serviceClass);
	}
	
	public void joinChannel(String name, Service handler) {
		joinChannel(name, handler, null);
	}
	
	public void joinChannel(String name, Service handler, Service callback) {
		Reference handlerRef = dispatcher.storeRandomObject(handler);
		Reference callbackRef = dispatcher.storeRandomObject(callback);
		String msg = JSONCodec.createJC(this, name, handlerRef, callbackRef);
		this.connection.send(msg);
	}

	public void leaveChannel(String name, Service handler) {
		leaveChannel(name, handler, null);
	}
	
	public void leaveChannel(String name, Service handler, Service callback) {
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
