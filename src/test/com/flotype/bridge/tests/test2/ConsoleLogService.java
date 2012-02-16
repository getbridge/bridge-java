package com.flotype.bridge.tests.test2;

import static junit.framework.Assert.assertTrue;

import com.flotype.bridge.Service;

public class ConsoleLogService extends Service {
    public void log(String s) {
        System.out.print(s);
        //assertTrue(s == "1243");
    }
}
