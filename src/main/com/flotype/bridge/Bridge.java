package com.flotype.bridge;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.*;
import java.nio.ByteBuffer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.Version;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.module.SimpleModule;

import com.flotype.bridge.serializers.ReferenceSerializer;
import com.flotype.bridge.serializers.ServiceClientSerializer;
import com.flotype.bridge.serializers.ServiceSerializer;

import net.bobah.nio.TcpClient;

public class Bridge {

    private static Log log = LogFactory.getLog(Bridge.class);

    private TcpClient connection = new Bridge.TCPConnection();;

    private String clientId;

    // Secret used for reconnects
    private String secret;

    // Queue for commands before connects happen
    private Queue<String> commandQueue = new LinkedList<String>();

    // Whether handshake has occurred
    private boolean handshaken = false;

    Executor executor = new Executor();

    // Options
    String host;
    Integer port;
    String apiKey;
    BridgeEventHandler eventHandler = null;
    
    public Bridge() {
        ReferenceFactory.createFactory(this);
        this.setHost(Utils.DEFAULT_HOST);
        this.setPort(Utils.DEFAULT_PORT);
        this.setEventHandler(Utils.DEFAULT_EVENT_HANDLER);
        this.setReconnect(Utils.DEFAULT_RECONNECT);
    }

    public Bridge(String host, Integer port, String apiKey, BridgeEventHandler eventHandler, boolean reconnect) {
        this();
        this.setHost(host);
        this.setPort(port);
        this.setApiKey(apiKey);
        this.setEventHandler(eventHandler);
        this.setReconnect(reconnect);
    }
    
    public boolean connect() {
        // Setup TCP
        connection.setAddress(new InetSocketAddress(host, port));

        try {
            connection.start();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        executor.addService("system", new SystemService(this, executor));

        return true;
    }
  
	public <T extends ServiceClient > T getService(String serviceName, Class<T> serviceClass){
		Reference result = new Reference(null, this);
		result.setRoutingPrefix("named");
		result.setRoutingId(serviceName);
		result.setServiceName(serviceName);

		Constructor ctor;
		try {
			ctor = serviceClass.getConstructor(Reference.class);
			return (T) ctor.newInstance(result);
		} catch (Exception e) {
			// One of the billion reflection things has gone wrong
			e.printStackTrace();
			return null;
		}
	}
    public Reference getChannel(String channelName){
    	Map<String, Object> getChannelBody = new HashMap<String, Object>();
        getChannelBody.put("name", channelName);
        sendCommand("GETCHANNEL", getChannelBody);
    	
    	Reference result = new Reference(null, this);
        result.setRoutingPrefix("channel");
        result.setRoutingId(channelName);
        result.setServiceName("channel:" + channelName);
        return result;
    }
    
    public void publishService(String name, Service service) {
        publishService(name, service, null);
    }

    public void publishService(String name, Service service, Service callback) {

        if(name.equals("system")) {
            log.error("Invalid service name: " + name);
            return;
        }

        executor.addService(name, service);
        service.createReference(name);
        joinWorkerPool(name, callback);
    }

    protected void publishService(Service service){
        String name = Utils.generateId();
        service.createReference(name);
        executor.addService(name, service);
    }
    
    public void joinWorkerPool(String name, Service callback) {

        Map<String, Object> joinWorkerPoolBody = new HashMap<String, Object>();

        joinWorkerPoolBody.put("name", name);

        if(callback != null) {
            joinWorkerPoolBody.put("callback", callback);
        }

        this.sendCommand("JOINWORKERPOOL", joinWorkerPoolBody);
    }
    
    public void joinChannel(String name, Service handler) {
        joinChannel(name, handler, null);
    }

    public void joinChannel(String name, Service handler, Service callback) {
        Map<String, Object> joinChannelBody = new HashMap<String, Object>();

        joinChannelBody.put("name", name);
        joinChannelBody.put("handler", handler);

        if(callback != null) {
            joinChannelBody.put("callback", callback);
        }

        this.sendCommand("JOINCHANNEL", joinChannelBody);
    }


    private void sendCommand(String command, Map<String, Object> data){
        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule("Handler", new Version(0, 1, 0, "alpha"));
        module.addSerializer(new ReferenceSerializer(Reference.class))
		.addSerializer(new ServiceSerializer(Service.class))
		.addSerializer(new ServiceClientSerializer(ServiceClient.class));
        mapper.registerModule(module);

        try {

            // Construct the request body here
            Map<String, Object> commandBody = new HashMap<String, Object>();

            commandBody.put("command", command);
            commandBody.put("data", data);

            String commandString = mapper.writeValueAsString(commandBody);

            this.write(commandString);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    protected void write(String jsonStr) {
        if(!this.connection.isConnected()) {
            commandQueue.add(jsonStr);
        } else {
            try {
                log.info("Sending JSON = " +  jsonStr);
                byte[] jsonBytes = jsonStr.getBytes();

                ByteBuffer data = ByteBuffer.allocate(jsonBytes.length + 4);

                data.put(Utils.intToByteArray(jsonBytes.length));
                data.put(jsonBytes);
                data.flip();

                connection.send(data);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
    
    private void processCommandQueue() {

        // TODO: Fix clientId refs

        for (String str : commandQueue ) {
            this.write(str);
        }
        commandQueue.clear();
    }




    class TCPConnection extends TcpClient {

        @Override protected void onRead(ByteBuffer buf) throws Exception {
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

                log.info("Message = " + new String(body));


                if(!Bridge.this.handshaken) {
                    // Client not handshaken
                    String[] ids = (new String(body)).split("\\|");

                    if(ids.length == 2) {
                        String oldId = Bridge.this.getClientId();

                        Bridge.this.setClientId(ids[0]);
                        Bridge.this.setSecret(ids[1]);

                        if(oldId == null) {
                            // This is a first connection (not a reconnect)
                            Bridge.this.eventHandler.onReady();
                        } else {
                            Bridge.this.eventHandler.onReconnect();
                        }
                        Bridge.this.handshaken = true;
                        Bridge.this.processCommandQueue();
                    } else {
                        log.error("Improper connect response");
                        Request req = Utils.deserialize(body);
                        executor.execute(req);
                    }

                } else {
                    // Parse as normal
                    Request req = Utils.deserialize(body);
                    executor.execute(req);
                }
            }
        }
        @Override protected void onDisconnected() {
            Bridge.this.handshaken = false;

            log.warn("Disconnected from TCP server");
        }
        @Override protected void onConnected() throws Exception {
            log.info("Connected to TCP server");

            // Queue up connect command
            Map<String, Object> connectBody = new HashMap<String, Object>();
            connectBody.put("session", new String[]{Bridge.this.getClientId(), Bridge.this.getSecret()});
            connectBody.put("api_key", Bridge.this.apiKey);

            Bridge.this.sendCommand("CONNECT", connectBody);
        }

    }


    
    public Bridge setApiKey(String apiKey) {
        this.apiKey = apiKey;
        return this;
    }

    private void setSecret(String secret) {
        this.secret = secret;
    }

    private String getSecret() {
        return this.secret;
    }

    
    public Bridge setHost(String host) {
        this.host = host;
        return this;
    }

    public Bridge setPort(Integer port) {
        this.port = port;
        return this;
    }

    public Bridge setEventHandler(BridgeEventHandler eventHandler) {
        this.eventHandler = eventHandler;
        return this;
    }

    public Bridge setReconnect(boolean reconnect) {
        this.connection.setReconnect(reconnect);
        return this;
    }

    protected void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientId () {
        return this.clientId;
    }

}
