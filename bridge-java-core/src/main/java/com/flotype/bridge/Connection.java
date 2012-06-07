package com.flotype.bridge;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.flotype.bridge.network.HTTPConnection;
import com.flotype.bridge.network.Socket;
import com.flotype.bridge.network.SocketBuffer;
import com.flotype.bridge.network.TCPSocket;
import com.flotype.bridge.network.SSLSocket;

public class Connection {
	private static Logger log = LoggerFactory.getLogger(Connection.class);

	String clientId;

	// Secret used for reconnects
	private String secret;

	private boolean handshaken;

	// Options
	private Bridge bridge;
	private String apiKey = null;
	private String host = null;
	private int port = -1;
	private String redirector = Utils.DEFAULT_REDIRECTOR;

	private Socket sock;
	private TCPSocket tcpSock;
	private SocketBuffer sockBuffer;

	private long reconnectInterval;
	private static final ScheduledExecutorService reconnectExecutor = 
		  Executors.newSingleThreadScheduledExecutor();

	private boolean reconnect;

	private boolean secure;

	protected Connection(Bridge bridge) {
		this.bridge = bridge;
		reconnectInterval = 400;
		
		sockBuffer = new SocketBuffer();
		tcpSock = new TCPSocket(this);
		sock = sockBuffer;
	}

	protected void start() throws IOException {
		if (this.host == null || this.port == -1) {
			redirector();
		} else {
			establishConnection();
		}
	}

	private void establishConnection() {
		log.info("Starting TCP connection {} {}", this.host, this.port);
		tcpSock.connect(host, port);
	}

	private void redirector() throws MalformedURLException {
		String endpoint = redirector + "/redirect/" + apiKey;
		HTTPConnection redirectorRequest = new HTTPConnection(this, endpoint);
		redirectorRequest.connect();
	}
	
	public void onRedirectorResponse(String json) throws JsonParseException, JsonMappingException, IOException {
		Map<String, Object> response = JSONCodec.parseRedirector(json);
		Map<String, Object> data = (Map<String, Object>) response.get("data");
		if(data == null) {
			log.error("Unable to parse redirector response");
			return;
		}
		
		if(data.get("bridge_port") == null || data.get("bridge_host") == null) {
			log.error("Could not find host and port in JSON body");
		} else {
			host = (String) data.get("bridge_host");
			port =  Integer.parseInt((String) data.get("bridge_port"));
			establishConnection();
		}
		
	}
	
	private void reconnect() {
		log.info("Attempting to reconnect");
		if(reconnectInterval < 32768) {
			Runnable task = new Runnable(){
				public void run(){
					reconnectInterval *= 2;
					establishConnection();
				}
			};
			
			reconnectExecutor.schedule(task, reconnectInterval, TimeUnit.MILLISECONDS);
		}
	}
	
	public void send(String msg){
		sock.send(msg);
	}

	public void onOpen() {
		log.info("Beginning handshake");
		String connectString = JSONCodec.createCONNECT(bridge, clientId,
				secret, apiKey);
		tcpSock.send(connectString);
		bridge.onConnected();
	}
	public void onClose() {
		log.warn("Connection closed");
		bridge.onDisconnect();
		sock = sockBuffer;
		if(reconnect == true) {
			reconnect();
		}
	}

	public void onMessage(String message) {
		log.info("Received {}", message);

		String[] ids = message.split("\\|");
		if (ids.length == 2) {
			// Got a ID and secret as response
			log.info("clientId receieved {}", ids[0]);
			clientId = ids[0];
			secret = ids[1];
			reconnectInterval = 400;
			sockBuffer.processQueue(tcpSock, clientId);
			sock = tcpSock;

			log.info("Handshake complete");

			if(this.handshaken == false) {
				bridge.onReady();
				this.handshaken = true;
			} else {
				bridge.onReconnect();
			}
		} else {
			processMessage(message);
		}
	}

	@SuppressWarnings("unchecked")
	private void processMessage(String message) {
		Map<String, Object> obj;
		try {
			obj = Utils.deserialize(bridge, message.getBytes());
			if(obj.get("destination") == null) {
				log.warn("No destination in message {}", message);
			} else {
				bridge.dispatcher.execute((Reference) obj.get("destination"),
						(List<Object>) obj.get("args"));
			}
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public void setRedirector(String redirectorUrl) {
		this.redirector = redirectorUrl;
	}

	public void setSecure(boolean secure) {
		this.secure = secure;
		if(this.secure == true) {
			tcpSock = new SSLSocket(this);
		} else {
			tcpSock = new TCPSocket(this);
		}
	}

}
