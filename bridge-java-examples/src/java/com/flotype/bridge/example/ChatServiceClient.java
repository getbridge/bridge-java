package com.flotype.bridge.example;

import com.flotype.bridge.BridgeObject;
import com.flotype.bridge.BridgeRemoteObject;

public interface ChatServiceClient extends BridgeRemoteObject {
	public void join(String string, BridgeObject handler, BridgeObject callback);
}