package com.getbridge.bridge.network;

import java.security.KeyStore;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.handler.codec.frame.LengthFieldBasedFrameDecoder;
import org.jboss.netty.handler.codec.frame.LengthFieldPrepender;
import org.jboss.netty.handler.ssl.SslHandler;

import com.getbridge.bridge.Connection;

public class SSLSocket extends TCPSocket {
	private SslHandler sslHandler;
	
	public SSLSocket(Connection connection) {
		super(connection);
		bootstrap.setPipelineFactory(new ChannelPipelineFactory(){

			public ChannelPipeline getPipeline() throws Exception {
				ChannelPipeline pipeline =  Channels.pipeline();
				
				TrustManagerFactory tmFactory = TrustManagerFactory.getInstance("SunX509");
				KeyStore tmpKS = null;
				tmFactory.init(tmpKS);
				TrustManager[] tm = tmFactory.getTrustManagers();

				SSLContext sslContext = SSLContext.getInstance("TLS");
				sslContext.init(null, tm, null);
				SSLEngine engine = sslContext.createSSLEngine();

				engine.setUseClientMode(true);
				engine.setEnableSessionCreation(true);
				engine.setWantClientAuth(true);
								
				sslHandler = new SslHandler(engine);
				
				pipeline.addLast("ssl", sslHandler);
				pipeline.addLast("decodeLength", new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4));
				pipeline.addLast("handleMessage", new SecureHandler());
				pipeline.addLast("prependLength", new LengthFieldPrepender(4));
				return pipeline;
			}
		});
	}
	
	class SecureHandler extends SocketHandler {
		
		@Override
		public void channelConnected(ChannelHandlerContext ctx,
				ChannelStateEvent e) throws Exception
		{
			super.channelConnected(ctx, e);
			ChannelFuture f = sslHandler.handshake();
			
			f.addListener(new ChannelFutureListener() {

				@Override
				public void operationComplete(ChannelFuture future)
						throws Exception {
					// TODO Auto-generated method stub
					System.out.println(future.isSuccess());
					
				}
				
			});
		}
	}

}