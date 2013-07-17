package fr.liglab.adele.icasa.dependency.manager2.interceptor;

import org.apache.felix.ipojo.dependency.interceptors.ServiceTrackingInterceptor;
import org.apache.felix.ipojo.dependency.interceptors.TransformedServiceReference;
import org.apache.felix.ipojo.util.DependencyModel;
import org.osgi.framework.BundleContext;

import fr.liglab.adele.icasa.dependency.manager2.DeviceDependency;

//@Component(name = "DeviceAccessTrackingInterceptor")
//@Provides(properties = { @StaticServiceProperty(name = "target", value = "(objectClass=fr.liglab.adele.icasa.device.GenericDevice)", type = "java.lang.String") })
//@Instantiate
public class DeviceAccessTrackingInterceptor implements ServiceTrackingInterceptor {

	@Override
	public void open(DependencyModel dependency) {
		// TODO Auto-generated method stub

	}

	@Override
	public void close(DependencyModel dependency) {
		// TODO Auto-generated method stub

	}

	@Override
	public <S> TransformedServiceReference<S> accept(DependencyModel dependency, BundleContext context,
	      TransformedServiceReference<S> ref) {
		
		
		
		if (dependency instanceof DeviceDependency) {
	      DeviceDependency deviceDependency = (DeviceDependency) dependency;
	      System.out.println("Device Dependency " + deviceDependency.getSpecification().getName());
      }
		
		return ref;
	}

}
