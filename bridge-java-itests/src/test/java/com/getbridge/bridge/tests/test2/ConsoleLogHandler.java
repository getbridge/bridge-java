package com.getbridge.bridge.tests.test2;

import com.getbridge.bridge.BridgeRemoteObject;

public interface ConsoleLogHandler extends BridgeRemoteObject {
	public void log(String s);
}
