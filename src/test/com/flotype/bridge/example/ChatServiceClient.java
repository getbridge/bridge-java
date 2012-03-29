package com.flotype.bridge.example;

import com.flotype.bridge.BridgeObject;
import com.flotype.bridge.BridgeRemoteObject;

public interface ChatServiceClient extends BridgeRemoteObject {
	public void msg(String s);
	public void join(String string, BridgeObject handler, BridgeObject callback);
	public void foo(Object a, Object b);
}