package com.flotype.bridge;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import java.lang.*;
import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.Version;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.module.SimpleModule;

import com.flotype.bridge.serializers.ReferenceSerializer;

import net.bobah.nio.TcpClient;

public class Bridge {


	private TcpClient connection;

	private String connectionId;

	// Secret used for reconnects
	private String secret;

	private Executor executor = new Executor();

	private BridgeEventHandler eventHandler = null;


	public Bridge() throws IOException{
		this(Utils.DEFAULT_HOST, Utils.DEFAULT_PORT);
	}

	public Bridge(String host) throws IOException{
		this(host, Utils.DEFAULT_PORT);
	}

	public Bridge(String host, Integer port) throws IOException{

		Bridge self = this;

		// Setup TCP
		connection = new TcpClient() {
		  @Override protected void onRead(ByteBuffer buf) throws Exception {
			  while(buf.hasRemaining()){
				  // Assuming 4 byte little endian ints
				  int length = buf.getInt();

				  byte[] body = new byte[length];
				  buf.get(body);

				  if (length != body.length) {
					  throw new Exception("Expected message length not equal to buffer size");
				  }

				  Utils.info("Message = " + new String(body));


				  if(connectionId == null) {
					  // Client not handshaken
					  String[] ids = (new String(body)).split("\\|");
					  connectionId = ids[0];
					  secret = ids[1];

					  Utils.info("Client handshaked with ID = " + connectionId + ", secret = " + secret);

					  if (Bridge.this.eventHandler != null) {
						  Bridge.this.eventHandler.onReady();
					  }

				  } else {
					  // Parse as normal
					  Request req = Utils.deserialize(body);
					  executor.execute(req);
				  }
			  }
		  }
		  @Override protected void onDisconnected() {

			  // No reconnect system yet so new connectionId every connection
			  connectionId = null;

			  Utils.warn("disconnected to tcp server");

		  }
		  @Override protected void onConnected() throws Exception {

			  Utils.info("connected to tcp server");

		  }
	    };
	    connection.setAddress(new InetSocketAddress(host, port));
		this.connect();
	}

	private boolean connect() throws IOException{

	    try {
	      connection.start();
	    } catch (IOException e) {
	      e.printStackTrace();
          return false;
	    }

		ReferenceFactory.createFactory(this);
		executor.addService("system", new SystemService(this, executor));

		return true;
	}

	public Reference getService(String actorId){
		Reference result = new Reference(actorId, this);
		result.setRoutingPrefix("named");
		return result;
	}

	protected Reference getChannel(String channelId){
		Reference result = new Reference(channelId, this);
		result.setRoutingPrefix("channel");
		return result;
	}

	protected void write(String jsonStr) {
		try {
			Utils.info("Sending JSON = " +  jsonStr);
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

	public String getConnectionId () {
		return connectionId;
	}

	public void joinChannel(String name, Service handler) {
		joinChannel(name, handler, null);
	}

	public void joinChannel(String name, Service handler, Service callback) {
		Map<String, Object> joinChannelBody = new HashMap<String, Object>();

		joinChannelBody.put("name", name);
		joinChannelBody.put("handler", handler.getReference());

		if(callback == null) {
			joinChannelBody.put("callback", Reference.Null);
		} else {
			joinChannelBody.put("callback", callback.getReference());
		}

		ObjectMapper handlerMapper = new ObjectMapper();
		SimpleModule handlerModule = new SimpleModule("Handler", new Version(0, 1, 0, "alpha"));
		handlerModule.addSerializer(new ReferenceSerializer(Reference.class));
		handlerModule.addSerializer(new HandlerSerializer(Map.class));
		handlerMapper.registerModule(handlerModule);

		try {
			String joinChannelString = handlerMapper.writeValueAsString(joinChannelBody);

			// Construct the request body here
			Map<String, Object> commandBody = new HashMap<String, Object>();

			commandBody.put("command", "JOINCHANNEL");
			commandBody.put("data", joinChannelString);


			ObjectMapper commandMapper = new ObjectMapper();
			SimpleModule commandModule = new SimpleModule("Command", new Version(0, 1, 0, "alpha"));
			commandModule.addSerializer(new CommandSerializer(Map.class));
			commandMapper.registerModule(commandModule);

			String commandString = commandMapper.writeValueAsString(commandBody);

			this.write(commandString);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void publishService(String name, Service service) {
		publishService(name, service, null);
	}

	public void publishService(String name, Service service, Service callback) {
		executor.addService(name, service);
		service.createReference(name);
		joinWorkerPool(name, service, callback);
	}

	public void publishService(Service service){
		String name = Utils.generateId();
		service.createReference(name);
		executor.addService(name, service);
	}

	public void joinWorkerPool(String name, Service handler, Service callback) {
		Map<String, Object> joinWorkerPoolBody = new HashMap<String, Object>();

		joinWorkerPoolBody.put("name", name);
		joinWorkerPoolBody.put("handler", handler.getReference());

		if(callback == null) {
			joinWorkerPoolBody.put("callback", Reference.Null);
		} else {
			joinWorkerPoolBody.put("callback", callback.getReference());
		}

		ObjectMapper handlerMapper = new ObjectMapper();
		SimpleModule handlerModule = new SimpleModule("Handler", new Version(0, 1, 0, "alpha"));
		handlerModule.addSerializer(new ReferenceSerializer(Reference.class));
		handlerModule.addSerializer(new HandlerSerializer(Map.class));
		handlerMapper.registerModule(handlerModule);

		try {
			String joinWorkerPoolString = handlerMapper.writeValueAsString(joinWorkerPoolBody);

			// Construct the request body here
			Map<String, Object> commandBody = new HashMap<String, Object>();

			commandBody.put("command", "JOINWORKERPOOL");
			commandBody.put("data", joinWorkerPoolString);


			ObjectMapper commandMapper = new ObjectMapper();
			SimpleModule commandModule = new SimpleModule("Command", new Version(0, 1, 0, "alpha"));
			commandModule.addSerializer(new CommandSerializer(Map.class));
			commandMapper.registerModule(commandModule);

			String commandString = commandMapper.writeValueAsString(commandBody);

			this.write(commandString);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void onReady(BridgeEventHandler eventHandler) {
		this.eventHandler = eventHandler;
		if(connection.isConnected()) {
			eventHandler.onReady();
		}
	}

}
