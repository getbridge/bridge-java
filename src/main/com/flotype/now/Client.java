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
	
	private UUID id;
	
	private Dispatcher dispatcher;
	private Consumer consumer;
	
	private Map<String, String> queuesToTags;
	
	public Client() throws IOException{
		this(null, null);
	}
	
	public Client(String host) throws IOException{
		this(host, null);
	}
	
	public Client(String host, Integer port) throws IOException{
		
		// Setup TCP
		
	}
	
	int i = 0;
	
	public boolean connect() throws IOException{
		
	    	
		connection = new TcpClient() {
	      @Override protected void onRead(ByteBuffer buf) throws Exception {
	    	  byte[] bytearr = new byte[buf.remaining()];
	    	  buf.get(bytearr);
	    	  String s = new String(bytearr);
	    	  System.out.println(s);
	    	  //write("asdf");
    	  	buf.position(buf.limit()); 
	  	  }
	      @Override protected void onDisconnected() { 
	    	  

	    	  System.out.println("disconnected to tcp server");
	    	  
	      }
	      @Override protected void onConnected() throws Exception { 
	    	  
	    	  System.out.println("connected to tcp server");
	    	  
	      }
	    };

	    connection.setAddress(new InetSocketAddress("127.0.0.1", 8082));
	    try {
	      connection.start();
	    } catch (IOException e) {
	      e.printStackTrace();
	    }
		
		
		ReferenceFactory.createFactory(id);
		
		
		
		return true;
	}
	
	public Reference getDummyReference(String actorId){
		return new Reference(actorId, id);
	}
	
	public void write(String jsonStr) {
		try {
			
			byte[] jsonBytes = jsonStr.getBytes();
			
			ByteBuffer data = ByteBuffer.allocate(jsonBytes.length + 4);
			
			data.put(Utils.intToByteArray(jsonBytes.length));
			data.put(jsonBytes);
			
			connection.send(data);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void addToPool(Reference user, Reference pool){
		
	}
	public void registerService(String name, Service service){
		dispatcher.registerService(name, service);
	}
	
	public void joinWorkerPool(String workerPoolName) throws IOException{
		//joinWorkerPool(workerPoolName, Utils.DEFAULT_EXCHANGE_NAME);
	}
	
	public void joinWorkerPool(String workerPoolName, String namespace) throws IOException{
		//String queueName = Utils.Prefix.WORKER+workerPoolName;
		
		//channel.queueDeclare(queueName, /*durable=*/ false, /*exclusive=*/ false, true, null);
	//	String consumerTag = channel.basicConsume(queueName, true, consumer);
	//	queuesToTags.put(queueName, consumerTag);
		
		//channel.queueBind(queueName, namespace, Utils.Prefix.NAMESPACED_ROUTING+workerPoolName);
	}
	
	public void leaveWorkerPool(String workerPoolName) throws IOException{
		//String queueName = Utils.Prefix.WORKER+workerPoolName;
		
		//String consumerTag = queuesToTags.get(queueName);
		//channel.basicCancel(consumerTag);
	}
	

}
