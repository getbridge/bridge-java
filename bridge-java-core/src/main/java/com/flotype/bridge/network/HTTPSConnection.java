package com.flotype.bridge.network;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.TrustManagerFactory;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.handler.codec.http.HttpChunkAggregator;
import org.jboss.netty.handler.codec.http.HttpClientCodec;
import org.jboss.netty.handler.ssl.SslHandler;

import com.flotype.bridge.Connection;

public class HTTPSConnection extends HTTPConnection {
	private SslHandler sslHandler;
	
	public HTTPSConnection(Connection connection, String url) {
		super(connection, url);
		client.setPipelineFactory(new ChannelPipelineFactory() {

			@Override
			public ChannelPipeline getPipeline() throws Exception {
				ChannelPipeline pipeline = Channels.pipeline();
				
				java.security.KeyStore ts = java.security.KeyStore.getInstance("JKS");
				ts.load(this.getClass().getResourceAsStream("/keystore.ks"), "flotype".toCharArray());
				
				TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
				tmf.init(ts);

				SSLContext sslc = SSLContext.getInstance("TLS");     
				sslc.init(null, tmf.getTrustManagers(), null);


				SSLEngine engine = sslc.createSSLEngine();
				engine.setUseClientMode(true);
				engine.setEnableSessionCreation(true);
				engine.setWantClientAuth(true);
								
				sslHandler = new SslHandler(engine);
				
				pipeline.addLast("ssl", sslHandler);
				
				pipeline.addLast("codec", new HttpClientCodec());
				pipeline.addLast("aggregator", new HttpChunkAggregator(5242880));
				pipeline.addLast("authHandler", new SecureMessageHandler());
				return pipeline;
			}
		});
	}
	
	class SecureMessageHandler extends ClientMessageHandler {
		@Override
		public void channelConnected(ChannelHandlerContext ctx,
				ChannelStateEvent e) throws Exception
		{
			super.channelConnected(ctx, e);
			sslHandler.handshake();
		}
	}

}
