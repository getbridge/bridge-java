package com.flotype.now;

public class Callback extends Service{
	public Callback() {
		ReferenceFactory.client.joinService(this);
	}
}
