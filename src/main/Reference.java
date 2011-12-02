import com.rabbitmq.client.Channel;


public class Reference {
	String address;
	Channel channel;
	
	public Reference(String value, Channel channel){
		address = value;
		this.channel = channel;
	}
}