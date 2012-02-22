package com.flotype.bridge.example.adder;
import com.flotype.bridge.Reference;
import com.flotype.bridge.ServiceClient;


public class AdderHandler extends ServiceClient {

    public AdderHandler(Reference reference) {
        super(reference);
    }

    public void greeting() {
        this.invokeRPC("greeting");
    }

}
