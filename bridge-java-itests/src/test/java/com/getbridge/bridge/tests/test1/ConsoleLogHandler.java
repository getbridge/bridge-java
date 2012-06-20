package com.getbridge.bridge.tests.test1;

import com.getbridge.bridge.BridgeRemoteObject;

public interface ConsoleLogHandler extends BridgeRemoteObject {

	public void log(String s);
}
