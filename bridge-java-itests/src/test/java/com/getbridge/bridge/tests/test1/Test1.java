package com.getbridge.bridge.tests.test1;

import java.io.IOException;

import org.junit.Test;

import com.getbridge.bridge.Bridge;
import com.getbridge.bridge.BridgeEventHandler;

public class Test1 {

	private static final Bridge bridgeServer = 	new Bridge("abcdefgh", "localhost", 8090);
	private static final Bridge bridgeClient = new Bridge("qwertyui", "localhost", 8090);

	@Test
	public void runTest() throws Exception {
		try {
			startService();
			startClient();
		} catch (Exception e) {
			System.out.print("aaa");
		}

	}

	private void startService() throws IOException {
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

	private void startClient() throws IOException {
		System.out.println("aaa3");
		bridgeClient.setEventHandler(new BridgeEventHandler() {
			@Override
			public void onReady() {
				System.out.println("aaa4");
				ConsoleLogHandler handler = bridgeClient.getService(
						"test1_consolelog_java", ConsoleLogHandler.class);
				handler.log("123");
			}
		});

		bridgeClient.connect();
	}
}
