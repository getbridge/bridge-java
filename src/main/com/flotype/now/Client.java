package com.flotype.now;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import java.lang.*;
import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;

import net.bobah.nio.TcpClient;

public class Client {
	
	
	private TcpClient connection;
	
	private String connectionId;
	
	private Executor executor = new Executor();
	
	
	public Client() throws IOException{
		this(Utils.DEFAULT_HOST, Utils.DEFAULT_PORT);
	}
	
	public Client(String host) throws IOException{
		this(host, Utils.DEFAULT_PORT);
	}
	
	public Client(String host, Integer port) throws IOException{
	    
			
		// Setup TCP
		connection = new TcpClient() {
		  @Override protected void onRead(ByteBuffer buf) throws Exception {
			  
			  // Assuming 4 byte little endian ints
			  int length = buf.getInt();
			 
			  byte[] body = new byte[buf.remaining()];
			  buf.get(body);
			  
			  if (length != body.length) {
				  throw new Exception("Expected message length not equal to buffer size");
			  }
			  
			  
			  if(connectionId == null) {
				  // Client not handshaken
				  connectionId = new String(body);
				  System.out.println("Client handshaked with ID = " + connectionId);
			  } else {
				  // Parse as normal
				  Request req = Utils.deserialize(body);
				  executor.execute(req);
			  }
			 
			  System.out.println("Message = " + new String(body));
			 
		  }
		  @Override protected void onDisconnected() { 
			  
			  // No reconnect system yet so new connectionId every connection
			  connectionId = null;
		
			  System.out.println("disconnected to tcp server");
			  
		  }
		  @Override protected void onConnected() throws Exception { 
			  
			  System.out.println("connected to tcp server");
			  
		  }
	    };
	    connection.setAddress(new InetSocketAddress(host, port));
		
	}
	
	public boolean connect() throws IOException{

	    try {
	      connection.start();
	    } catch (IOException e) {
	      e.printStackTrace();
          return false;
	    }

		ReferenceFactory.createFactory(this);
				
		return true;
	}
	
	public Reference getService(String actorId){
		Reference result = new Reference(actorId, this);
		result.setRoutingPrefix("N.");
		return result;
	}
	
	public void write(String jsonStr) {
		try {
			System.out.println("Sending JSON = " +  jsonStr);
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
	
	}
	
	public void joinService(String name, Service service) {
		executor.addService(name, service);
		service.createReference(name);
		service.getReference().setRoutingPrefix("N.");
		joinWorkerPool(name);
	}
	
	
	public void joinService(Service service){
		String name = Utils.generateId();
		service.createReference(name);
		executor.addService(name, service);
	}
	
	public void joinWorkerPool(String workerPoolName) {
		// It's faster this way
		write("{\"type\":\"joinWorkerPool\",\"name\":\""+workerPoolName+"\",\"callback\":[\"none\",null]}");
	}
	
}
