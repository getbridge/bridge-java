package com.flotype.bridge;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


class Dispatcher {

	private static Log log = LogFactory.getLog(Dispatcher.class);

	private Map<Object, Class<?>> serviceToClass;
	private Map<String, Object> services;
	private ExecutorService tp;
	private Bridge bridge;

	protected Dispatcher(Bridge bridge){
		serviceToClass = new HashMap<Object, Class<?>>();
		services = new HashMap<String, Object>();
		tp = Executors.newFixedThreadPool(4);
		this.bridge = bridge;
	}

	protected void execute(Reference reference, final List<Object> argList){
		String serviceName = reference.getObjectId();
		String methodName = reference.getMethodName();
		log.info(serviceName + ":" + methodName + " called");
		final Object service = services.get(serviceName);
		if(service == null){
			log.error("No such service: " + serviceName);
			return;
		}

		// Turn List<Object> to Object[] as reflection requires
		final Object[] args = new Object[argList.size()];
		int idx = 0;
		for(Object o : argList){
			args[idx++] = o;
		}

		final Method m = getConformingMethod(methodName, args, serviceToClass.get(service));
		if(m == null){
			log.error("No method found: " + methodName);
			return;
		}
		tp.execute(new Runnable(){
			public void run() {
				try {
					// avoids JVM bug involving member access to anonymous classes
					m.setAccessible(true);
					m.invoke(service, args);
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
			}
		});
	}

	protected Reference storeObject(String objectName, Object object){
		if(object == null){
			return null;
		}
		Class<?> klass = object.getClass();
		serviceToClass.put(object, klass);
		services.put(objectName, object);
		return Reference.createClientReference(bridge, objectName, Utils.getMethods(klass));
	}

	public Reference storeRandomObject(Object object){
		String randomId = Utils.generateRandomId();
		return storeObject(randomId,object);
	}

	protected Object getObject(String key){
		return services.get(key);
	}

	protected Reference storeExistingObjectByKey(String oldKey, String newKey) {
		Object s = services.get(oldKey);
		return storeObject(newKey, s);
	}

	protected Method getConformingMethod(String methodName, Object[] arguments, Class<?> cls) {
		Method[] publicMethods = cls.getMethods();
		Method result = null;

		methodLoop:
			for(int iMethod = 0; iMethod < publicMethods.length; iMethod++){
				Method m = publicMethods[iMethod];
				if(m.getName().equals(methodName)){
					Class<?>[] formalParameters = m.getParameterTypes();
					if(arguments.length == formalParameters.length){
						for(int iParam = 0; iParam < arguments.length; iParam++){
							Class<?> param = formalParameters[iParam];
							if (!param.isAssignableFrom(arguments[iParam].getClass())) {
								if(!(arguments[iParam] instanceof Reference
										&& Utils.contains(param.getInterfaces(), BridgeRemoteObject.class))){
									// Argument is not assignable and is not proxyable. Skip this method
									continue methodLoop;
								} else {
									// Can create a proxy using the Reference object and the interface that is expected
									arguments[iParam] = Utils.createProxy((Reference) arguments[iParam], param);
								}
							}
						}
						result = m;
						break;
					}
				}
			}

		return result;
	}

}
