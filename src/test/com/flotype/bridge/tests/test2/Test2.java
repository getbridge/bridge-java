package com.flotype.bridge.tests.test2;

import org.junit.Test;

import com.flotype.bridge.Bridge;
import com.flotype.bridge.BridgeEventHandler;
import com.flotype.bridge.Reference;

public class Test2 {

    final Bridge bridgeServer = new Bridge().setHost("localhost").setPort(8090);
    final Bridge bridgeClient = new Bridge().setHost("localhost").setPort(8090);

    @Test
    public void runTest1() {
        startService();
        startClient();
    }

    private void startService() {
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

    private void startClient() {
        System.out.print("aaa3");
        bridgeClient.setEventHandler(new BridgeEventHandler() {
            @Override
            public void onReady() {
                System.out.print("aaa4");
                Reference service =
                    bridgeClient.getService("test1_consolelog_java");
                (new ConsoleLogHandler(service)).log("123");
            }
        });

        bridgeClient.connect();
    }
}
