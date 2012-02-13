package com.flotype.bridge.tests.test2;

import com.flotype.bridge.Reference;
import com.flotype.bridge.ServiceClient;

public class ConsoleLogHandler extends ServiceClient {

    public ConsoleLogHandler(Reference reference) {
        super(reference);
    }

    public void log(String s) {
        this.invokeRPC("log", s);
    }
}
