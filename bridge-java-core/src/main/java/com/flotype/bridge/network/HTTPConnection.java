package com.flotype.bridge.network;

import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.jboss.netty.handler.codec.http.DefaultHttpRequest;
import org.jboss.netty.handler.codec.http.HttpChunkAggregator;
import org.jboss.netty.handler.codec.http.HttpClientCodec;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpMethod;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpVersion;
import org.jboss.netty.util.CharsetUtil;

import com.flotype.bridge.Connection;

public class HTTPConnection {


	Connection connection;
	protected ClientBootstrap client;
	private String url;

	public HTTPConnection(Connection connection, String url) {

		this.connection = connection;
		this.url = url;

		client = new ClientBootstrap(
				new NioClientSocketChannelFactory(
						Executors.newCachedThreadPool(),
						Executors.newCachedThreadPool()));

		client.setPipelineFactory(new ChannelPipelineFactory() {

			@Override
			public ChannelPipeline getPipeline() throws Exception {
				ChannelPipeline pipeline = Channels.pipeline();
				pipeline.addLast("codec", new HttpClientCodec());
				pipeline.addLast("aggregator", new HttpChunkAggregator(5242880));
				pipeline.addLast("authHandler", new ClientMessageHandler());
				return pipeline;
			}
		});

	}

	public void connect() throws MalformedURLException {
		URL endpointURL = new URL(url);
		final DefaultHttpRequest request = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, endpointURL.getFile());
		request.addHeader(HttpHeaders.Names.HOST, endpointURL.getHost());

		int port = endpointURL.getPort();
		if (port == -1) {
			port = endpointURL.getDefaultPort();
		}
		
		ChannelFuture connectFuture = client.connect(new InetSocketAddress(endpointURL.getHost(), port));
		connectFuture.addListener(new ChannelFutureListener(){
			@Override
			public void operationComplete(ChannelFuture future)
			throws Exception {
				future.getChannel().write(request);

			}
		});
	}

	class ClientMessageHandler extends SimpleChannelHandler {
		@Override
		public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
			e.getCause().printStackTrace();
		}

		@Override
		public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
			HttpResponse httpResponse = (HttpResponse) e.getMessage();
			String json = httpResponse.getContent().toString(CharsetUtil.UTF_8);
			HTTPConnection.this.connection.onRedirectorResponse(json);
		}
	}

}