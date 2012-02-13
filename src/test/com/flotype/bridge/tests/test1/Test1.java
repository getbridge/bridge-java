package com.flotype.bridge.tests.test1;

import com.flotype.bridge.Bridge;
import com.flotype.bridge.BridgeEventHandler;
import com.flotype.bridge.Reference;

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

    private static void startService() {
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

    private static void startClient() {
        System.out.println("aaa3");
        bridgeClient.setEventHandler(new BridgeEventHandler() {
            @Override
            public void onReady() {
                System.out.println("aaa4");
                Reference service =
                    bridgeClient.getService("test1_consolelog_java");
                (new ConsoleLogHandler(service)).log("123");
            }
        });

        bridgeClient.connect();
    }
}
