package com.getbridge.bridge.tests.test1;

import com.getbridge.bridge.BridgeObject;

public class ConsoleLogService implements BridgeObject {
	public void log(String s) throws Exception {
		System.exit(1);
		// throw new Exception("expected 123");
		// assertTrue(s == "1243");
	}
}
