import java.io.IOException;
import java.util.ArrayList;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import java.util.Map;

public class Utils {
	
	 static enum Prefix{
		DIRECT("D_"), TOPIC("T_"), FANOUT("F_"),
		WORKER("W_"), CLIENT("C_"),
		NAMESPACED_ROUTING("N.");
		
		private String value;
		private Prefix(String value){
			this.value = value;
		}
		
		public String toString(){
			return this.value;
		}
	};
	static String DEFAULT_EXCHANGE_NAME = Prefix.DIRECT + "DEFAULT";
	
	protected static Request deserialize(String jsonString) throws JsonParseException, JsonMappingException, IOException{
		ObjectMapper mapper = new ObjectMapper();
		
		Map<String, ArrayList<?>> jsonObj = mapper.readValue(jsonString, new TypeReference<Map<String, ArrayList<Object>>>() { });
		
		ArrayList<String> pathchain = (ArrayList<String>) jsonObj.get("pathchain");
		ArrayList<Object> signature = (ArrayList<Object>) jsonObj.get("serargskwargs");

		return new Request(pathchain, signature);
	}
	
	//This is complete bullshit. But it will work for now
	protected static boolean isUUID(String name){
		 String[] components = name.split("-");
         return components.length == 5;
	}

	public static Class<?> classFromString(String type) {
		Class<?> theClass = java.lang.Object.class;
		if(type.equals("list")){
			theClass = java.util.ArrayList.class;
		} else if (type.equals("dict")) {
			theClass = java.util.HashMap.class;
		} else if (type.equals("str")) {
			theClass = java.lang.String.class;
		} else if (type.equals("float")) {
			theClass = java.lang.Double.class;
		} else if (type.equals("none")){
			theClass = java.lang.Object.class;
		} else if (type.equals("now")) {
			theClass = Reference.class;
		}
		return theClass;
	}

}
