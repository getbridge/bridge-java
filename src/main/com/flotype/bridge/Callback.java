package com.flotype.bridge;

public class Callback extends Service{
	public Callback() {
		ReferenceFactory.client.publishService(this);
	}
}
