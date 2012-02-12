package com.flotype.bridge;
import java.util.List;
import org.codehaus.jackson.annotate.JsonProperty;

public class Request {

	private Reference reference;

	private Class<?>[] parameterList;
	private Object[] argumentsList;

	public Request(@JsonProperty("destination") Reference reference, @JsonProperty("args") List<Object> args) {

		this.reference = reference;

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

			Object normalizedValue = Utils.normalizeValue(arg);
			argumentsList[pos] = normalizedValue;

			pos++;
		}


	}

	public Reference getReference() {
		return reference;
	}

	public Class<?>[] getParameterList(){
		return parameterList;
	}

	public Object[] getArguments(){
		return argumentsList;
	}

}
