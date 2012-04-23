package com.flotype.bridge.tests.test2;

import com.flotype.bridge.BridgeRemoteObject;

public interface ConsoleLogHandler extends BridgeRemoteObject {
	public void log(String s);
}
