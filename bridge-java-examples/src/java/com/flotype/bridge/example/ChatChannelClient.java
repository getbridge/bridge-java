package com.flotype.bridge.example;

import com.flotype.bridge.BridgeRemoteObject;

public interface ChatChannelClient extends BridgeRemoteObject {
	public void msg(String string, String string2);
}
