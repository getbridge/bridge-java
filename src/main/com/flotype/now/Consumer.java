package com.flotype.now;
import java.io.IOException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;


public class Consumer  {
	/*
	Dispatcher dispatcher;

	protected Consumer(Channel channel, Dispatcher dispatcher) {
		super(channel);		
		this.dispatcher = dispatcher;
	}
	
	public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body){
		try {
			// TODO run this in a seperate thread
			Request req = Utils.deserialize(new String(body));
			dispatcher.dispatch(req);
		} catch (JsonParseException e) {
			System.err.println("JsonParseException: Received malformed message");
		} catch (JsonMappingException e) {
			System.err.println("JsonMappingException: JSON format mismatch");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	*/

}