package com.flotype.bridge;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.codehaus.jackson.annotate.JsonProperty;

public class Request {

	private List<String> pathchain;

	private Class<?>[] parameterList;
	private Object[] argumentsList;

	public Request(@JsonProperty("pathchain") List<String> pathchain, @JsonProperty("args") List<Object> args) {

		this.pathchain = pathchain;

		// null check
		if(null == args || args.size() < 1){
			parameterList = null;
			argumentsList = null;
		}

		// Get element 0: regular arguments. Ignore element 1 (keyword arguments)
		//List<ArrayList<Object>> regularArguments = (ArrayList<ArrayList<Object>>) signature.get(0);


		int len = args.size();
		parameterList = new Class<?>[len];
		argumentsList = new Object[len];

		int pos = 0;

		for(Object arg : args){
			Class<?> theClass = arg.getClass();
			parameterList[pos] = theClass;

			Object deserializedValue = Utils.deserialize(arg);
			argumentsList[pos] = deserializedValue;

			pos++;
		}


	}

	public List<String> getPathchain() {
		return pathchain;
	}

	public Class<?>[] getParameterList(){
		return parameterList;
	}

	public Object[] getArguments(){
		return argumentsList;
	}

}