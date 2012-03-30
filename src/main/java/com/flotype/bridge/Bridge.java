package com.flotype.bridge;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.lang.reflect.Proxy;

/**
 * Bridge class is the interface to the Bridge server. A Bridge object
 * encapsulates a network connection and state of stored objects. The network
 * connection is a bidirectional TCP socket open between this client and the
 * Bridge server. Incoming RPC calls are handled by the {@link Dispatcher}.
 * Messages are formatted for the wire by {@link JSONCodec}
 * 
 * @author sridatta
 */
public class Bridge {
	BridgeEventHandler eventHandler = null;
	private static Logger log = LoggerFactory.getLogger(Bridge.class);
	protected Dispatcher dispatcher = new Dispatcher(this);
	boolean ready = false;
	private Connection connection;

	/**
	 * Default Bridge constructor which uses all default settings. This
	 * constructor provides all basic setup instructions to connect to the
	 * Bridge server operated by Flotype. Instead of establishing a direct
	 * connection, the client will first make an HTTP request to a 'redirector'
	 * to determine which Bridge server to connect to. The default settings are:
	 * <ul>
	 * <li>host: http://redirector.flotype.com
	 * <li>port: -1 (signifies HTTP request instead of TCP socket)
	 * <li>reconnect: true
	 * </ul>
	 */
	public Bridge() {
		connection = new Connection(this);
		this.setRedirector(Utils.DEFAULT_REDIRECTOR);
		this.setEventHandler(Utils.DEFAULT_EVENT_HANDLER);
		this.setReconnect(Utils.DEFAULT_RECONNECT);

		dispatcher.storeObject("system", new SystemService(this, dispatcher));
	}

	/**
	 * Bridge constructor which allows all options to be specified manually This
	 * constructor calls the default constructor and then calls setter methods
	 * to change from the defaults.
	 * 
	 * @param host
	 *            Either a hostname for direct connection
	 * @param port
	 *            Positive integer value if hostname specified. Else, null
	 * @param apiKey
	 *            An API key issued and recognized by the Bridge server
	 * @param eventHandler
	 *            An instance of {@link BridgeEventHandler} to receive event
	 *            notifications
	 * @param reconnect
	 *            Boolean specifying whether or not to reconnect if connection
	 *            is disrupted
	 */
	public Bridge(String host, Integer port, String apiKey,
			BridgeEventHandler eventHandler, boolean reconnect) {
		this();
		this.setHost(host);
		this.setPort(port);
		this.setApiKey(apiKey);
		this.setEventHandler(eventHandler);
		this.setReconnect(reconnect);
	}

	/**
	 * Bridge constructor to be used for a redirected connection.
	 * 
	 * @param redirectorUrl
	 *            Must be a valid HTTP URL
	 * @param apiKey
	 *            An API key issued and recognized by the Bridge server
	 * @param eventHandler
	 *            An instance of {@link BridgeEventHandler} to receive event
	 *            notifications
	 * @param reconnect
	 *            Boolean specifying whether or not to reconnect if connection
	 *            is disrupted
	 */
	public Bridge(String redirectorUrl, String apiKey,
			BridgeEventHandler eventHandler, boolean reconnect) {
		this();
		this.setRedirector(redirectorUrl);
		this.setApiKey(apiKey);
		this.setEventHandler(eventHandler);
		this.setReconnect(reconnect);
	}

	/**
	 * Establishes the connection and handshake process. If a redirected
	 * connection is specified, the client will make a synchronous HTTP request
	 * to the redirector server and then start a TCP socket with the host and
	 * port specified in the response. If a direct connection is specified, the
	 * TCP socket will be started immediately.
	 * 
	 * @return
	 * @throws IOException
	 */
	public boolean connect() throws IOException {
		this.connection.connect();
		return true;
	}

	protected void send(Reference destination, Object[] args) {
		String msg = JSONCodec.createSEND(this, destination, args);
		this.connection.send(msg);
	}

	/**
	 * Makes an instance of local or remote Bridge object available to remote
	 * Bridge clients with the given name. If the object is a locally created
	 * object, it will be stored in this Bridge instance's object table and this
	 * client will receive RPC calls. If the object is a reference to a remote
	 * object, the client that owns the remote object will receive RPC calls and
	 * this client will NOT proxy any calls.
	 * 
	 * @param name
	 *            The name of the service to be exposed to other Bridge clients
	 * @param bridgeObject
	 */
	public void publishService(String name, BridgeObjectBase bridgeObject) {
		BridgeObject callback = null;
		publishService(name, bridgeObject, callback);
	}

	/**
	 * Makes an instance of local or remote Bridge object available to remote
	 * Bridge clients with the given name. Behaves identically to
	 * {@link #publishService(String, BridgeObjectBase)} but takes a local
	 * Service object as a callback. Upon successful receipt of the publish
	 * command, the Bridge server will invoke this callback object's `callback`
	 * method.
	 * 
	 * @param name
	 * @param bridgeObject
	 * @param callback
	 *            A local object that has a method named callback
	 */
	public void publishService(String name, BridgeObjectBase bridgeObject,
			BridgeObjectBase callback) {
		if (name.equals("system")) {
			log.error("Invalid service name {}", name);
			return;
		}

		if (bridgeObject instanceof BridgeObject) {
			dispatcher.storeObject(name, bridgeObject);
		}

		Reference callbackRef = null;
		if (callback instanceof BridgeObject) {
			callbackRef = dispatcher.storeRandomObject(callback);
		} else if (callback instanceof BridgeRemoteObject) {
			callbackRef = (Reference) Proxy.getInvocationHandler(callback);
		}
		String msg = JSONCodec.createJWP(this, name, callbackRef);
		this.connection.send(msg);
	}

	/**
	 * Creates a proxy object to a service published by a remote Bridge client.
	 * 
	 * @param serviceName
	 * @param serviceInterface
	 *            An interface that proxy object should conform to
	 * @return A proxy object that points to `serviceName` and has methods
	 *         defined in the interface `serviceInterface`
	 */
	public <T> T getService(String serviceName, Class<T> serviceInterface) {
		Reference result = Reference.createServiceReference(this, serviceName,
				Utils.getMethods(serviceInterface));
		return Utils.createProxy(result, serviceInterface);
	}

	/**
	 * Creates a proxy object to a channel of remote Bridge clients.
	 * 
	 * @param channelName
	 * @param channelInterface
	 *            An interface that proxy object should conform to
	 * @return A proxy object that points to `channelName` and has methods
	 *         defined in the interface `channelInterface`
	 */
	public <T> T getChannel(String channelName, Class<T> channelInterface) {
		String msg = JSONCodec.createGETCHANNEL(this, channelName);
		this.connection.send(msg);

		Reference result = Reference.createChannelReference(this, channelName,
				Utils.getMethods(channelInterface));
		return Utils.createProxy(result, channelInterface);
	}

	/**
	 * Joins this Bridge client to a channel whose messages will be handled by
	 * the given handler.
	 * 
	 * @param channelName
	 * @param handler
	 */
	public void joinChannel(String channelName, BridgeObject handler) {
		joinChannel(channelName, handler, null);
	}

	/**
	 * Joins this Bridge client to a channel whose messages will be handled by
	 * the given handler.
	 * 
	 * @param channelName
	 * @param handler
	 * @param callback
	 *            A callback that will be called upon joining the channel
	 */
	public void joinChannel(String channelName, BridgeObjectBase handler,
			BridgeObjectBase callback) {
		Reference handlerRef = null;
		if (handler instanceof BridgeObject) {
			handlerRef = dispatcher.storeRandomObject(handler);
		} else if (handler instanceof BridgeRemoteObject) {
			handlerRef = (Reference) Proxy.getInvocationHandler(handler);
		}
		Reference callbackRef = null;
		if (handler instanceof BridgeObject) {
			callbackRef = dispatcher.storeRandomObject(callback);
		} else if (callback instanceof BridgeRemoteObject) {
			callbackRef = (Reference) Proxy.getInvocationHandler(callback);
		}
		String msg = JSONCodec.createJC(this, channelName, handlerRef,
				callbackRef);
		this.connection.send(msg);
	}

	/**
	 * Leaves the channel and removes the handler
	 * 
	 * @param name
	 * @param handler
	 */
	public void leaveChannel(String name, BridgeObject handler) {
		leaveChannel(name, handler, null);
	}

	/**
	 * Leaves the channel and removes the handler
	 * 
	 * @param name
	 * @param handler
	 */
	public void leaveChannel(String name, BridgeObject handler,
			BridgeObject callback) {
		Reference handlerRef = dispatcher.storeObject(name, handler);
		Reference callbackRef = dispatcher.storeRandomObject(callback);
		String msg = JSONCodec.createLEAVECHANNEL(this, name, handlerRef,
				callbackRef);
		this.connection.send(msg);
	}

	public Bridge setApiKey(String apiKey) {
		this.connection.setApiKey(apiKey);
		return this;
	}

	public void setRedirector(String redirectorUrl) {
		this.connection.setRedirector(redirectorUrl);
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

	protected void onDisconnect() {
		this.ready = false;
	}

	protected void onReady() {
		this.ready = true;
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
