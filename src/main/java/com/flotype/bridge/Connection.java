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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Connection extends TcpClient {
	private static Logger log = LoggerFactory.getLogger(Connection.class);

	String clientId;

	// Secret used for reconnects
	private String secret;

	// Queue for commands before connects happen
	private Queue<String> commandQueue = new LinkedList<String>();

	private boolean handshaken;

	private Bridge bridge;
	private String apiKey = null;
	private String host = null;
	private int port = -1;
	private String redirector;

	protected Connection(Bridge bridge) {
		this.bridge = bridge;
	}

	protected void connect() throws IOException {
		if (this.host == null || this.port == -1) {
			redirector();
		} else {
			this.setAddress(new InetSocketAddress(host, port));
			log.info("Starting TCP connection {} {}", this.host, this.port);
			start();
		}
	}
	
	protected void send(String string) {
		if (bridge.ready) {
			write(string.getBytes());
		} else {
			// Buffer messages until reconnection happens
			commandQueue.add(string);
		}
	}

	public void write(byte[] buffer) {
		log.info("Sending {}", new String(buffer));
		ByteBuffer data = ByteBuffer.allocate(buffer.length + 4);
		data.put(Utils.intToByteArray(buffer.length)); // Put the length header
		data.put(buffer); // Put the data
		data.flip();
		try {
			this.send(data);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void redirector() {
		String result = null;
		if (redirector.startsWith("http://")) {
			try {
				// Send data
				String urlStr = redirector;
				if (urlStr.charAt(urlStr.length() - 1) != '/') {
					urlStr += '/';
				}
				urlStr = urlStr + "redirect/" + this.apiKey;
				URL url = new URL(urlStr);
				URLConnection conn = url.openConnection();

				// Get the response
				BufferedReader rd = new BufferedReader(new InputStreamReader(
						conn.getInputStream()));
				StringBuffer sb = new StringBuffer();
				String line;
				while ((line = rd.readLine()) != null) {
					sb.append(line);
				}
				rd.close();
				result = sb.toString();

				// Parse response JSON, set fields on self, and connect
				Map<String, Object> jsonObj = JSONCodec.parseRedirector(result);
				Map<String, String> data = (Map<String, String>) jsonObj
						.get("data");
				
				if(data.get("bridge_host") == null || data.get("bridge_port") == null) {
					log.error("Could not find host and port in JSON body");
					return;
				}
				
				this.host = data.get("bridge_host");
				this.port = Integer.parseInt(data.get("bridge_port"));
				this.connect();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void processCommandQueue() {
		for (String str : commandQueue) {
			// Replace the 'null' in the JSON reference address with the
			// now-known client ID
			this.send(str.replace("\"ref\":[\"client\",null,",
					"\"ref\":[\"client\",\"" + this.clientId + "\","));
		}
		commandQueue.clear();
	}

	@Override
	protected void onRead(ByteBuffer buf) throws Exception {
		while (buf.hasRemaining()) {
			if(buf.remaining() < 4) {
				// Full header not received yet. Wait until next time
				break;
			}
			int length = buf.getInt();
			if (buf.remaining() < length) {
				// Header received but not the body. Wait until next time.
				break;
			}
			byte[] body = new byte[length];
			buf.get(body);
			String bodyString = new String(body);
			if (length != body.length) {
				throw new Exception(
						"Expected message length not equal to buffer size");
			}
			
			log.info("Received {}", body);

			if (!bridge.ready) {
				// Client not handshaken
				String[] ids = (bodyString).split("\\|");
				if (ids.length == 2) {
					// Got a ID and secret as response
					log.info("clientId receieved {}", ids[0]);
					clientId = ids[0];
					secret = ids[1];
					this.handshaken = true;

					bridge.onReady();
					processCommandQueue();
					log.info("Handshake complete");
					return;
				}
			}

			// Parse as normal
			Map<String, Object> message = Utils.deserialize(bridge, body);
			if(message.get("destination") == null) {
				log.warn("No destination in message {}", bodyString);
			} else {
			bridge.dispatcher.execute((Reference) message.get("destination"),
					(List<Object>) message.get("args"));
			}
		}
	}

	@Override
	protected void onDisconnected() {
		log.warn("Connection closed");
		bridge.onDisconnect();
	}

	@Override
	protected void onConnected() throws Exception {
		log.info("Beginning handshake");
		String connectString = JSONCodec.createCONNECT(bridge, clientId,
				secret, apiKey);
		// Use write instead of send for unbuffered sending
		this.write(connectString.getBytes());
		bridge.onConnected();
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

}
