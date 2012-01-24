package com.flotype.bridge;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.codehaus.jackson.annotate.JsonProperty;

public class Request {

	private List<String> pathchain;

	private Class<?>[] parameterList;
	private Object[] argumentsList;

	public Request(@JsonProperty("pathchain") List<String> pathchain, @JsonProperty("args") List<ArrayList<Object>> regularArguments) {

		this.pathchain = pathchain;

		// null check
		if(null == regularArguments || regularArguments.size() < 1){
			parameterList = null;
			argumentsList = null;
		}

		// Get element 0: regular arguments. Ignore element 1 (keyword arguments)
		//List<ArrayList<Object>> regularArguments = (ArrayList<ArrayList<Object>>) signature.get(0);


		int len = regularArguments.size();
		parameterList = new Class<?>[len];
		argumentsList = new Object[len];

		int pos = 0;

		for(ArrayList<Object> param : regularArguments){
			// First element of the list is type. Second element of the list is value. We want type.
			String type = (String) param.get(0);
			Object value = param.get(1);

			Class<?> theClass = Utils.classFromString(type);
			parameterList[pos] = theClass;

			// Ugly hack. Can only return one thing, so deserialize returns deserializedValue while also populating refList
			Object deserializedValue = Utils.deserialize(type, value);
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
