import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Request {
	
	private List<String> pathchain;
	
	private Class<?>[] parameterList;
	private Object[] argumentsList;
	
	public Request(List<String> pathchain, List<Object> signature){
		this.pathchain = pathchain;
		
		// null check
		if(null == signature || signature.size() < 1){
			parameterList = null;
			argumentsList = null;
		}
		
		// Get element 0: regular arguments. Ignore element 1 (keyword arguments)
		List<ArrayList<Object>> regularArguments = (ArrayList<ArrayList<Object>>) signature.get(0);
		
		// null check
		if(null == regularArguments){
			parameterList = null;
			argumentsList = null;
		}
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
			
			if(param.get(0).equals("now")){
				value = ReferenceFactory.getFactory().generateReference((String) value);
			}
			argumentsList[pos] = value;
			pos++;
		}
	}
	
	public void normalize(UUID id){
		if(pathchain.get(0) == id.toString()){
			pathchain.remove(0);
		}
	}
	
	public String getServiceName(){
		return pathchain.get(0);
	}
	
	public String getMethodName(){
		return pathchain.get(1);
	}
	
	public Class<?>[] getParameterList(){
		return parameterList;
	}
	
	public Object[] getArguments(){
		return argumentsList;
	}
}
