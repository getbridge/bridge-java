package com.getbridge.bridge.tests.test2;

import com.getbridge.bridge.BridgeObject;

public class ConsoleLogService implements BridgeObject {
	public void log(String s) {
		System.out.print(s);
		// assertTrue(s == "1243");
	}
}
