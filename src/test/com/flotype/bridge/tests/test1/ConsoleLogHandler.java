package com.flotype.bridge.tests.test1;

import com.flotype.bridge.ServiceClient;

public interface ConsoleLogHandler extends ServiceClient {

    public void log(String s);
}
