package com.flotype.bridge.tests.test2;

import java.io.IOException;

import org.junit.Test;

import com.flotype.bridge.Bridge;
import com.flotype.bridge.BridgeEventHandler;

public class Test2 {

    final Bridge bridgeServer = new Bridge().setHost("localhost").setPort(8090);
    final Bridge bridgeClient = new Bridge().setHost("localhost").setPort(8090);

    @Test
    public void runTest1() throws IOException {
        startService();
        startClient();
    }

    private void startService() throws IOException {
        System.out.print("aaa1");
        bridgeServer.setEventHandler(new BridgeEventHandler() {
            @Override
            public void onReady() {
                System.out.print("aaa2");
                bridgeServer.publishService("test1_consolelog_java",
                    new ConsoleLogService());
            }
        });

        bridgeServer.connect();
    }

    private void startClient() throws IOException {
        System.out.print("aaa3");
        bridgeClient.setEventHandler(new BridgeEventHandler() {
            @Override
            public void onReady() {
                System.out.print("aaa4");
                ConsoleLogHandler handler = bridgeClient.getService("test1_consolelog_java", ConsoleLogHandler.class);
                handler.log("123");
            }
        });

        bridgeClient.connect();
    }
}
