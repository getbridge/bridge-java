package com.flotype.bridge.tests.test2;

import com.flotype.bridge.Service;

public class ConsoleLogService implements Service {
    public void log(String s) {
        System.out.print(s);
        //assertTrue(s == "1243");
    }
}
