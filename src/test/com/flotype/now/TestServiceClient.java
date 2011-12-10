package com.flotype.now;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.Version;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.module.SimpleModule;


import com.flotype.now.Reference;
import com.flotype.now.ServiceClient;
import com.flotype.now.serializers.ListSerializer;
import com.flotype.now.serializers.MapSerializer;
import com.flotype.now.serializers.ReferenceSerializer;
import com.flotype.now.serializers.StringSerializer;


public class TestServiceClient extends ServiceClient {

	public TestServiceClient(Reference reference) {
		super(reference);
	}
	
	public void fetchUrl(String s){
		this.invokeRPC("fetch_url", s);
	}

}
