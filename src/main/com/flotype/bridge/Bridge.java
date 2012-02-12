package com.flotype.bridge;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import java.net.*;
import java.nio.ByteBuffer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.Version;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.module.SimpleModule;

import com.flotype.bridge.serializers.ReferenceSerializer;

import net.bobah.nio.TcpClient;

public class Bridge {

	private static Log log = LogFactory.getLog(Bridge.class);

	private TcpClient connection;
	
	private String clientId;

	// Secret used for reconnects
	private String secret;

	Executor executor = new Executor();
	
	// Options
	String host;
	Integer port;
	String apiKey;
	BridgeEventHandler eventHandler = null;

	public Bridge() {
		ReferenceFactory.createFactory(this);
	}
	
	public Bridge(String host, Integer port, String apiKey, BridgeEventHandler eventHandler) {
		this();
		this.setHost(host);
		this.setPort(port);
		this.setApiKey(apiKey);
		this.setEventHandler(eventHandler);
	}

	public boolean connect() {
		
		// Setup TCP
		connection = new Bridge.TCPConnection();
		connection.setAddress(new InetSocketAddress(host, port));
		
		try {
			connection.start();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}

		executor.addService("system", new SystemService(this, executor));
		return true;
	}

	public Reference getService(String serviceName){
		Reference result = new Reference(null, this);
		result.setRoutingPrefix("named");
		result.setRoutingId(serviceName);
		result.setServiceName(serviceName);
		return result;
	}

	protected Reference getChannel(String channelName){
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
			joinWorkerPoolBody.put("callback", callback.getReference());
		}

		this.sendCommand("JOINWORKERPOOL", joinWorkerPoolBody);
	}

	public void joinChannel(String name, Service handler) {
		joinChannel(name, handler, null);
	}

	public void joinChannel(String name, Service handler, Service callback) {
		Map<String, Object> joinChannelBody = new HashMap<String, Object>();

		joinChannelBody.put("name", name);
		joinChannelBody.put("handler", handler.getReference());

		if(callback != null) {
			joinChannelBody.put("callback", callback.getReference());
		}

		this.sendCommand("JOINCHANNEL", joinChannelBody);
	}

	private void sendCommand(String command, Map<String, Object> data){
		ObjectMapper mapper = new ObjectMapper();
		SimpleModule module = new SimpleModule("Handler", new Version(0, 1, 0, "alpha"));
		module.addSerializer(new ReferenceSerializer(Reference.class));
		mapper.registerModule(module);

		try {

			// Construct the request body here
			Map<String, Object> commandBody = new HashMap<String, Object>();

			commandBody.put("command", command);
			commandBody.put("data", data);

			String commandString = mapper.writeValueAsString(commandBody);

			this.write(commandString);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	protected void write(String jsonStr) {
		try {
			log.info("Sending JSON = " +  jsonStr);
			byte[] jsonBytes = jsonStr.getBytes();

			ByteBuffer data = ByteBuffer.allocate(jsonBytes.length + 4);

			data.put(Utils.intToByteArray(jsonBytes.length));
			data.put(jsonBytes);
			data.flip();

			connection.send(data);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public Bridge setHost(String host) {
		this.host = host;
		return this;
	}
	
	public Bridge setPort(Integer port) {
		this.port = port;
		return this;
	}
	
	public Bridge setApiKey(String apiKey) {
		this.apiKey = apiKey;
		return this;
	}
	
	public Bridge setEventHandler(BridgeEventHandler eventHandler) {
		this.eventHandler = eventHandler;
		return this;
	}

	protected void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getClientId () {
		return this.clientId;
	}

	class TCPConnection extends TcpClient {

		@Override protected void onRead(ByteBuffer buf) throws Exception {
			while(buf.hasRemaining()){
				// Assuming 4 byte little endian ints
				int length = buf.getInt();

				byte[] body = new byte[length];
				buf.get(body);

				if (length != body.length) {
					throw new Exception("Expected message length not equal to buffer size");
				}

				log.info("Message = " + new String(body));


				if(Bridge.this.getClientId() == null) {
					// Client not handshaken
					String[] ids = (new String(body)).split("\\|");
					Bridge.this.setClientId(ids[0]);
					Bridge.this.setSecret(ids[1]);
					Bridge.this.eventHandler.onReady();	

				} else {
					// Parse as normal
					Request req = Utils.deserialize(body);
					executor.execute(req);
				}
			}
		}
		@Override protected void onDisconnected() {
			// No reconnect system yet so new connectionId every connection
			Bridge.this.setClientId(null);
			log.warn("disconnected to tcp server");

		}
		@Override protected void onConnected() throws Exception {
			log.info("connected to tcp server");
			Map<String, Object> connectBody = new HashMap<String, Object>();
			connectBody.put("session", Arrays.asList(new String[]{"0","0"}));
			connectBody.put("api_key", Bridge.this.apiKey);
			
			Bridge.this.sendCommand("CONNECT", connectBody);
		}

	}

	private void setSecret(String secret) {
		this.secret = secret;
	}
	
	private String getSecret() {
		return this.secret;
	}
}
