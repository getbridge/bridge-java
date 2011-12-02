import java.io.IOException;


public class TestMain {
	
	public static void main(String[] args){
		try {
			Client client = new Client();
			client.connect();
			client.joinWorkerPool("foo");
			client.registerService("foo", new TestService());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
