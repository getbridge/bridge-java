package com.flotype.bridge.tests.test1;

import java.io.IOException;

import com.flotype.bridge.Bridge;
import com.flotype.bridge.BridgeEventHandler;

public class Test1 {

	private static final Bridge bridgeServer = new Bridge()
	.setHost("localhost").setPort(8090);
	private static final Bridge bridgeClient = new Bridge()
	.setHost("localhost").setPort(8090);

	public static void main(String[] args) throws Exception {
		try {
			startService();
			startClient();
		} catch (Exception e) {
			System.out.print("aaa");
		}

	}

	private static void startService() throws IOException {
		System.out.println("aaa1");
		bridgeServer.setEventHandler(new BridgeEventHandler() {
			@Override
			public void onReady() {
				System.out.println("aaa2");
				bridgeServer.publishService("test1_consolelog_java",
						new ConsoleLogService());
			}
		});

		bridgeServer.connect();
	}

	private static void startClient() throws IOException {
		System.out.println("aaa3");
		bridgeClient.setEventHandler(new BridgeEventHandler() {
			@Override
			public void onReady() {
				System.out.println("aaa4");
				ConsoleLogHandler handler = bridgeClient.getService("test1_consolelog_java", ConsoleLogHandler.class);
				handler.log("123");
			}
		});

		bridgeClient.connect();
	}
}
