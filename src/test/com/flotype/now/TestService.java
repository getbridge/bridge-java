package com.flotype.now;

import com.flotype.now.Reference;
import com.flotype.now.Service;


public class TestService extends Service {

	public void foo(){
		System.out.println("Holy shitballs, this worked");
	}
	
	public void foo(Double i){
		System.out.println("I'm printing numbers, bitch: " + i);
	}
	
	public void foo(Reference r){
		System.out.println("Dat reference: " + r.getAddress());
	}
}
