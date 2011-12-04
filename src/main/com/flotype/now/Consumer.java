package com.flotype.now;
import java.io.IOException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

public class Consumer extends DefaultConsumer {
	
	Dispatcher dispatcher;

	protected Consumer(Channel channel, Dispatcher dispatcher) {
		super(channel);		
		this.dispatcher = dispatcher;
	}
	
	public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body){
		try {
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
	

}