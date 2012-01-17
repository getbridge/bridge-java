package com.flotype.bridge;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


class Executor {
	
	// In the future this will be auto generated
	
	
	
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

		String serviceName;
		String methodName;
		
		List<String> pathchain = req.getPathchain();

		if (pathchain.get(0).equals("system")) {
			// Reserved for future
		}
		
		if (pathchain.get(0).equals("channel")) {
			serviceName = "channel:" + pathchain.get(1);
			if(pathchain.size() == 3) {
				// connectionId.channel.channelname.method
				methodName = pathchain.get(2);
			} else {
				// connectionId.channel.channelname
				methodName = "callback";
			}

		} else {
			serviceName = pathchain.get(0);
			if(pathchain.size() == 3) {
				// connectionId.service.method
				methodName = pathchain.get(1);
			} else {
				// connectionId.channel.channelname
				methodName = "callback";
			}
		}
		
		Utils.info(serviceName + ":" + methodName + " called");
		
		
		final Service service = services.get(serviceName);
		
		try {
			final Method m = serviceToClass.get(service).getMethod(methodName, req.getParameterList());
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
		} catch(NoSuchMethodException e){
			e.printStackTrace();
		}
		
	}
	
	protected void addService(String serviceName, Service service){
		serviceToClass.put(service, service.getClass());
		services.put(serviceName, service);
	}

	public void addExistingServiceByKey(String channelName, String key) {
		// TODO Auto-generated method stub
		Service s = services.get(key);
		addService(channelName, s);
	}

}
