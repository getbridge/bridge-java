package com.flotype.now;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConnectionFactory;

public class Client {
	
	
	private Connection connection;
	private Channel channel;
	
	private UUID id;
	
	private Dispatcher dispatcher;
	private Consumer consumer;
	
	private Map<String, String> queuesToTags;
	
	public Client() throws IOException{
		this(null, null);
	}
	
	public Client(String host, Integer port) throws IOException{
		ConnectionFactory factory = new ConnectionFactory();
		
		if(host != null) {factory.setHost(host);}
		if(port != null) { factory.setPort(port); }
		
		connection = factory.newConnection();
		
		id = UUID.randomUUID();
		queuesToTags = new HashMap<String, String>();
		
		dispatcher = new Dispatcher(id);
		consumer = new Consumer(channel, dispatcher);
	}
	
	public boolean connect() throws IOException{
		channel = connection.createChannel();
		ReferenceFactory.createFactory(channel, id);
		this.AMQPSetup();
		return true;
	}
	
	public Reference getDummyReference(){
		return new Reference("adasd", channel, id);
	}
	
	public void addToPool(Reference user, Reference pool){
		
	}
	public void registerService(String name, Service service){
		dispatcher.registerService(name, service);
	}
	
	public void joinWorkerPool(String workerPoolName) throws IOException{
		joinWorkerPool(workerPoolName, Utils.DEFAULT_EXCHANGE_NAME);
	}
	
	public void joinWorkerPool(String workerPoolName, String namespace) throws IOException{
		String queueName = Utils.Prefix.WORKER+workerPoolName;
		
		channel.queueDeclare(queueName, /*durable=*/ false, /*exclusive=*/ false, true, null);
		String consumerTag = channel.basicConsume(queueName, true, consumer);
		queuesToTags.put(queueName, consumerTag);
		
		channel.queueBind(queueName, namespace, Utils.Prefix.NAMESPACED_ROUTING+workerPoolName);
		
	}
	
	public void leaveWorkerPool(String workerPoolName) throws IOException{
		String queueName = Utils.Prefix.WORKER+workerPoolName;
		
		String consumerTag = queuesToTags.get(queueName);
		channel.basicCancel(consumerTag);
	}
	
	
	private void AMQPSetup() throws IOException{
		String queueName = Utils.Prefix.CLIENT +id.toString();
		String exchangeName = Utils.Prefix.TOPIC+id.toString();
		
		// Ensure default exchange exists
		channel.exchangeDeclare(Utils.DEFAULT_EXCHANGE_NAME, "direct");
		
		// Exchange T_UUID
		channel.exchangeDeclare(exchangeName, "direct");
		
		// Queue C_UUID
		channel.queueDeclare(queueName, /*durable=*/ false, /*exclusive=*/ true, true, null);
		
		//Consume C_UUID
		String consumerTag = channel.basicConsume(queueName, /*autoAck=*/ true, consumer);
		queuesToTags.put(queueName, consumerTag);
		
		//Binding T_UUID -> routing_key: N.* -> D_DEFAULT
		channel.exchangeBind(Utils.DEFAULT_EXCHANGE_NAME, exchangeName, Utils.Prefix.NAMESPACED_ROUTING+"*");
	}
	
}
