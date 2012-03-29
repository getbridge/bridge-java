package com.flotype.bridge.tests.test2;

import com.flotype.bridge.BridgeObject;

public class ConsoleLogService implements BridgeObject {
    public void log(String s) {
        System.out.print(s);
        //assertTrue(s == "1243");
    }
}
