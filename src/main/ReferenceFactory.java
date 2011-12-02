import com.rabbitmq.client.Channel;


public class ReferenceFactory {
	
	static ReferenceFactory theFactory;
	
	private Channel channel;
	
	public ReferenceFactory(Channel channel) {
		this.channel = channel;
	}

	public static void createFactory(Channel channel){
		theFactory = new ReferenceFactory(channel);
	}
	
	public static ReferenceFactory getFactory(){
		if(theFactory == null){
			throw new Error("ReferenceFactory uninitialized");
		} else {
			return theFactory;
		}
	}
	
	public Reference generateReference(String value){
		return new Reference(value, channel);
	}

}
