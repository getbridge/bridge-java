package com.flotype.bridge.example;

import com.flotype.bridge.Service;
import com.flotype.bridge.ServiceClient;

public interface ChatServiceClient extends ServiceClient {
	public void msg(String s);
	public void join(String string, Service handler, Service callback);
	public void foo(Object a, Object b);
}