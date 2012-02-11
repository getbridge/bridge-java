package com.flotype.bridge;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


class Executor {
	
	private static Log log = LogFactory.getLog(Executor.class);

	private Map<Service, Class<?>> serviceToClass;
	private Map<String, Service> services;

	private ExecutorService tp;

	protected Executor(){
		serviceToClass = new HashMap<Service, Class<?>>();
		services = new HashMap<String, Service>();

		tp = Executors.newFixedThreadPool(4);
	}

	// TODO synchronize this. Being invoked from different consumer threads
	protected void execute(final Request req){
		Reference reference = req.getReference();

		String serviceName = reference.getServiceName();
		String methodName = reference.getMethodName();

		log.info(serviceName + ":" + methodName + " called");


		final Service service = services.get(serviceName);
		
		if(service == null){
			Utils.error("No such service: " + serviceName);
			return;
		}

		final Method m = getConformingMethod(methodName, req.getArguments(), serviceToClass.get(service));
		tp.execute(new Runnable(){
			public void run() {
				try {
					m.invoke(service, req.getArguments());
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

	protected void addService(String serviceName, Service service){
		serviceToClass.put(service, service.getClass());
		services.put(serviceName, service);
	}
	
	protected Service getService(String serviceName) {
		return services.get(serviceName);
	}

	public void addExistingServiceByKey(String channelName, String key) {
		// TODO Auto-generated method stub
		Service s = services.get(key);
		addService(channelName, s);
	}

	
	
	protected Method getConformingMethod(String methodName, Object[] arguments, Class<?> cls) {
		Method[] publicMethods = cls.getMethods();
		Method m = null;
		int idxMethod = 0;
		while ((m == null) && (idxMethod < publicMethods.length)) {
			m = publicMethods[idxMethod];
			if (m.getName().equals(methodName)) {
				Class<?>[] formalParameters = m.getParameterTypes();
				if (arguments.length == formalParameters.length) {
					int idxParam = 0;
					while ((m != null) && (idxParam < formalParameters.length)) {
						Class<?> param = formalParameters[idxParam];
						if (!param.isAssignableFrom(arguments[idxParam].getClass())) {
							m = null;
						}
						idxParam++;
					}
				} else {
					m = null;
				}
			} else {
				m = null;
			}
			idxMethod++;
		}
		return m;
	}

}
