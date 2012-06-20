package com.getbridge.bridge.network;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.jboss.netty.handler.codec.frame.LengthFieldBasedFrameDecoder;
import org.jboss.netty.handler.codec.frame.LengthFieldPrepender;

import com.getbridge.bridge.Connection;

public class TCPSocket implements Socket {

	protected ClientBootstrap bootstrap;
	private Connection connection;
	private Channel channel;

	public TCPSocket(Connection connection){
		this.connection = connection;

		ChannelFactory factory = new NioClientSocketChannelFactory(Executors.newCachedThreadPool(), Executors.newCachedThreadPool());
		bootstrap = new ClientBootstrap(factory);

		bootstrap.setPipelineFactory(new ChannelPipelineFactory(){
			public ChannelPipeline getPipeline() throws Exception {
				ChannelPipeline pipeline =  Channels.pipeline();
				pipeline.addLast("decodeLength", new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4));
				pipeline.addLast("handleMessage", new SocketHandler());
				pipeline.addLast("prependLength", new LengthFieldPrepender(4));
				return pipeline;
			}
		});

		bootstrap.setOption("tcpNoDelay", true);
		bootstrap.setOption("keepAlive", true);
		bootstrap.setOption("connectTimeoutMillis", 100);
	}

	@Override
	public void send(String message) {
		byte[] bytes = message.getBytes();
		ChannelBuffer buf = ChannelBuffers.buffer(bytes.length);
		buf.writeBytes(bytes);

		channel.write(buf);
	}

	public void connect(String host, int port) {
		bootstrap.connect(new InetSocketAddress(host, port));
	}
	
	class SocketHandler extends SimpleChannelHandler {
		@Override
		public void messageReceived(ChannelHandlerContext ctx, MessageEvent e){
			ChannelBuffer buf = (ChannelBuffer) e.getMessage();
			connection.onMessage(buf.toString(Charset.forName("UTF-8")));
		}

		@Override
		public void channelConnected(ChannelHandlerContext ctx,
				ChannelStateEvent e) throws Exception
		{
			super.channelConnected(ctx, e);
			connection.onOpen();
		}

		@Override
		public void channelDisconnected(ChannelHandlerContext ctx,
				ChannelStateEvent e) throws Exception
		{
			super.channelDisconnected(ctx, e);
			bootstrap.releaseExternalResources();
			
			connection.onClose();
		}

		public void connectRequested(ChannelHandlerContext ctx,
				ChannelStateEvent e) throws Exception
		{
			super.connectRequested(ctx, e);
			channel = e.getChannel();
		}
		
		public void exceptionCaught(ChannelHandlerContext ctx,
                ExceptionEvent e)
		{
			System.out.println(e.getCause().getMessage());
		}
	}

}
