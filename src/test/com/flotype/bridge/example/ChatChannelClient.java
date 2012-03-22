package com.flotype.bridge.example;

import com.flotype.bridge.ServiceClient;

public interface ChatChannelClient extends ServiceClient { 
	public void msg(String string, String string2);
}
