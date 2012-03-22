package com.flotype.bridge;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Bridge {

	private static Log log = LogFactory.getLog(Bridge.class);

	private TcpClient connection = new Bridge.TCPConnection();;

	private String clientId;

	// Secret used for reconnects
	private String secret;

	// Queue for commands before connects happen
	private Queue<String> commandQueue = new LinkedList<String>();

	// Whether handshake has occurred
	private boolean handshaken = false;

	private Executor executor = new Executor();

	// Options
	String host;
	Integer port;
	String apiKey;
	BridgeEventHandler eventHandler = null;

	public Bridge() {
		ReferenceFactory.createFactory(this);
		this.setHost(Utils.DEFAULT_HOST);
		this.setPort(Utils.DEFAULT_PORT);
		this.setEventHandler(Utils.DEFAULT_EVENT_HANDLER);
		this.setReconnect(Utils.DEFAULT_RECONNECT);
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
		this(host, -1, apiKey, eventHandler, reconnect);
	}

	public boolean connect() {
		if(this.port != -1) {
			// Setup TCP
			connection.setAddress(new InetSocketAddress(host, port));

			try {
				connection.start();
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		} else {
			this.sendGetRequest(this.host);
		}

		executor.addService("system", new SystemService(this, executor));

		return true;
	}

	public <T extends ServiceClient > T getService(String serviceName, Class<T> serviceClass){
		Reference result = new Reference(null, this);
		result.setRoutingPrefix("named");
		result.setRoutingId(serviceName);
		result.setServiceName(serviceName);

		Constructor ctor;
		try {
			ctor = serviceClass.getConstructor(Reference.class);
			return (T) ctor.newInstance(result);
		} catch (Exception e) {
			// One of the billion reflection things has gone wrong
			e.printStackTrace();
			return null;
		}
	}
	public <T extends ServiceClient > T getChannel(String channelName, Class<T> serviceClass){
		Map<String, Object> getChannelBody = new HashMap<String, Object>();
		getChannelBody.put("name", channelName);
		sendCommand("GETCHANNEL", getChannelBody);

		Reference result = getChannelReference(channelName);
		
		Constructor ctor;
		try {
			ctor = serviceClass.getConstructor(Reference.class);
			return (T) ctor.newInstance(result);
		} catch (Exception e) {
			// One of the billion reflection things has gone wrong
			e.printStackTrace();
			return null;
		}
	}
	
	protected Reference getChannelReference(String channelName) {
		Reference result = new Reference(null, this);
		result.setRoutingPrefix("channel");
		result.setRoutingId(channelName);
		result.setServiceName("channel:" + channelName);
		return result;
	}

	public void publishService(String name, Service service) {
		publishService(name, service, null);
	}

	public void publishService(String name, Service service, Service callback) {

		if(name.equals("system")) {
			log.error("Invalid service name: " + name);
			return;
		}

		executor.addService(name, service);
		service.createReference(name);
		joinWorkerPool(name, callback);
	}

	protected void publishService(Service service){
		String name = Utils.generateId();
		service.createReference(name);
		executor.addService(name, service);
	}

	public void joinWorkerPool(String name, Service callback) {

		Map<String, Object> joinWorkerPoolBody = new HashMap<String, Object>();

		joinWorkerPoolBody.put("name", name);

		if(callback != null) {
			joinWorkerPoolBody.put("callback", callback);
		}

		this.sendCommand("JOINWORKERPOOL", joinWorkerPoolBody);
	}

	public void joinChannel(String name, Service handler) {
		joinChannel(name, handler, null);
	}

	public void joinChannel(String name, Service handler, Service callback) {
		Map<String, Object> joinChannelBody = new HashMap<String, Object>();

		joinChannelBody.put("name", name);
		joinChannelBody.put("handler", handler);

		if(callback != null) {
			joinChannelBody.put("callback", callback);
		}

		this.sendCommand("JOINCHANNEL", joinChannelBody);
	}
	
	public void leaveChannel(String name, Service handler) {
		leaveChannel(name, handler, null);
	}
	
	public void leaveChannel(String name, Service handler, Service callback) {
		Map<String, Object> leaveChannelBody = new HashMap<String, Object>();

		leaveChannelBody.put("name", name);
		leaveChannelBody.put("handler", handler);
		
		if(callback != null) {
			leaveChannelBody.put("callback", callback);
		}

		this.sendCommand("LEAVECHANNEL", leaveChannelBody);
	}


	private void sendCommand(String command, Map<String, Object> data){
		ObjectMapper mapper = new ObjectMapper();
		SimpleModule module = new SimpleModule("Handler", new Version(0, 1, 0, "alpha"));
		module.addSerializer(new ReferenceSerializer(Reference.class))
		.addSerializer(new ServiceSerializer(Service.class))
		.addSerializer(new ServiceClientSerializer(ServiceClient.class));
		mapper.registerModule(module);

		try {

			// Construct the request body here
			Map<String, Object> commandBody = new HashMap<String, Object>();

			commandBody.put("command", command);
			commandBody.put("data", data);

			String commandString = mapper.writeValueAsString(commandBody);

			if(!this.handshaken && !command.equals("CONNECT")) {
				this.addCommandQueue(commandString);	
			} else {
				this.write(commandString);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	protected Executor getExecutor() {
		return this.executor;
	}

	public Bridge setApiKey(String apiKey) {
		this.apiKey = apiKey;
		return this;
	}

	private void setSecret(String secret) {
		this.secret = secret;
	}

	private String getSecret() {
		return this.secret;
	}


	public Bridge setHost(String host) {
		this.host = host;
		return this;
	}

	public Bridge setPort(Integer port) {
		this.port = port;
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

	protected void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getClientId () {
		return this.clientId;
	}

	public boolean isHandshaken() {
		return this.handshaken;
	}

	}


}
