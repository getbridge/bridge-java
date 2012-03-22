package com.flotype.bridge;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.URL;
import java.net.URLConnection;
import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import net.bobah.nio.TcpClient;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Connection extends TcpClient{
	private static Log log = LogFactory.getLog(Connection.class);

	String clientId;

	// Secret used for reconnects
	private String secret;

	// Queue for commands before connects happen
	private Queue<String> commandQueue = new LinkedList<String>();
	
	private boolean handshaken;
	
	private Bridge bridge;
	private String apiKey;
	private String host;
	private int port;

	protected Connection(Bridge bridge){
		this.bridge = bridge;
	}

	protected void connect() throws IOException {
		if(this.host == null || this.port == -1){
			redirector();
		} else {
			this.setAddress(new InetSocketAddress(host, port));
			start();
		}
	}

	protected void send(String string) {
		System.out.println(string);
		if(bridge.ready) {
			write(string.getBytes());
		} else {
			addCommandQueue(string);
		}
	}
	
	public void write(byte[] buffer) {			
		ByteBuffer data = ByteBuffer.allocate(buffer.length + 4);
		data.put(Utils.intToByteArray(buffer.length));
		data.put(buffer);
		data.flip();
		try {
			this.send(data);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void redirector()
	{
		String endpoint = this.host;
		String result = null;
		if (endpoint.startsWith("http://"))
		{
			try
			{
				// Send data
				String urlStr = endpoint;
				if(urlStr.charAt(urlStr.length()-1) != '/') {
					urlStr += '/';
				}
				urlStr = urlStr + "redirect/"+ this.apiKey;
				URL url = new URL(urlStr);
				URLConnection conn = url.openConnection ();

				// Get the response
				BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
				StringBuffer sb = new StringBuffer();
				String line;
				while ((line = rd.readLine()) != null)
				{
					sb.append(line);
				}
				rd.close();
				result = sb.toString();

				// Return a request object parsed by mapper
				Map<String, Object> jsonObj = JSONCodec.parseRedirector(result);				
				Map<String, String> data = (Map<String, String>) jsonObj.get("data");
				this.host = data.get("bridge_host");
				this.port = Integer.parseInt(data.get("bridge_port"));
				this.connect();
			} catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	private void processCommandQueue() {
		for (String str : commandQueue ) {
			this.send(str.replace("\"ref\":[\"client\",null,", "\"ref\":[\"client\",\""+this.clientId+"\","));
		}
		commandQueue.clear();
	}

	protected void addCommandQueue(String jsonStr) {
		if(this.handshaken) {
			this.send(jsonStr.replace("\"ref\":[\"client\",null,", "\"ref\":[\"client\",\""+ this.clientId+"\","));
		} else {
			commandQueue.add(jsonStr);
		}
	}

	@Override 
	protected void onRead(ByteBuffer buf) throws Exception {
		while(buf.hasRemaining()){
			// Assuming 4 byte little endian ints
			int length = buf.getInt();
			if(buf.remaining() < length){
				// Header received but not the body. Wait until next time.
				break;
			}
			byte[] body = new byte[length];
			buf.get(body);
			if (length != body.length) {
				throw new Exception("Expected message length not equal to buffer size");
			}

			if(!bridge.ready) {
				// Client not handshaken
				String[] ids = (new String(body)).split("\\|");
				if(ids.length == 2) {
					clientId = ids[0];
					secret = ids[1];
					this.handshaken = true;
					
					processCommandQueue();
					bridge.onReady();
					return;
				}
			} 

			// Parse as normal
			Map<String, Object> message = Utils.deserialize(bridge, body);
			bridge.dispatcher.execute((Reference) message.get("destination"), (List<Object>) message.get("args"));
		}
	}

	@Override 
	protected void onDisconnected() {
		log.warn("Disconnected from TCP server");
		bridge.onDisconnect();
	}

	@Override 
	protected void onConnected() throws Exception {
		log.info("Connected to TCP server");
		String connectString = JSONCodec.createCONNECT(bridge, clientId, secret, apiKey);
		// Use write instead of send for immediate
		this.write(connectString.getBytes());
		bridge.onConnected();
	}


	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public void setPort(int port){
		this.port = port;
	}

}
