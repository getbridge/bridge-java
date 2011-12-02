import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


class Dispatcher {
	
	// In the future this will be auto generated
	
	
	private UUID id;
	private Map<Service, Class<?>> serviceToClass;
	private Map<String, Service> services;

	public Dispatcher(UUID id){
		this.id = id;
		
		serviceToClass = new HashMap<Service, Class<?>>();
		services = new HashMap<String, Service>();
	}
	
	protected void dispatch(final Request req){
		req.normalize(id);
		
		String serviceName = req.getServiceName();
		String methodName = req.getMethodName();
		
		final Service service = services.get(serviceName);
		
		try {
			final Method m = serviceToClass.get(service).getMethod(methodName, req.getParameterList());
			Thread t = new Thread(new Runnable(){
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
			t.run();
		} catch(NoSuchMethodException e){
			e.printStackTrace();
		}

			
		
	}
	
	protected void registerService(String serviceName, Service service){
		serviceToClass.put(service, service.getClass());
		services.put(serviceName, service);
	}

}
