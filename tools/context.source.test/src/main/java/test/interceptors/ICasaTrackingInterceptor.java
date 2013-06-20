/**
 *
 *   Copyright 2011-2012 Universite Joseph Fourier, LIG, ADELE team
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package test.interceptors;

import java.util.ArrayList;
import java.util.List;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Invalidate;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.StaticServiceProperty;
import org.apache.felix.ipojo.annotations.Validate;
import org.apache.felix.ipojo.dependency.interceptors.DefaultDependencyInterceptor;
import org.apache.felix.ipojo.dependency.interceptors.ServiceTrackingInterceptor;
import org.apache.felix.ipojo.dependency.interceptors.TransformedServiceReference;
import org.apache.felix.ipojo.util.DependencyModel;
import org.osgi.framework.BundleContext;

import fr.liglab.adele.icasa.ContextManager;
import fr.liglab.adele.icasa.device.GenericDevice;
import fr.liglab.adele.icasa.location.LocatedDevice;
import fr.liglab.adele.icasa.location.LocatedDeviceListener;
import fr.liglab.adele.icasa.location.Position;
import fr.liglab.adele.icasa.location.Zone;

@Component(name = "ICasaTrackingInterceptor")
@Provides(properties = { @StaticServiceProperty(name = "target", value = "(objectClass=fr.liglab.adele.icasa.device.GenericDevice)", type = "java.lang.String") })
@Instantiate
public class ICasaTrackingInterceptor extends DefaultDependencyInterceptor implements ServiceTrackingInterceptor,
      LocatedDeviceListener {

	/**
	 * The set of managed dependencies. Access must be guarded by the monitor lock.
	 */
	//protected final List<DependencyModel> dependencies = new ArrayList<DependencyModel>();

	@Requires
	private ContextManager contextManager;

	@Override
	public void close(DependencyModel dependency) {
		System.out.println("Closing Tracking Interceptor - Component Instance :: " + getInstanceName(dependency));
		if (isPlatformComponent(dependency))
			return;
		super.close(dependency);
	}

	@Override
	public void open(DependencyModel dependency) {
		System.out.println("Opening Tracking Interceptor - Component Instance :: " + getInstanceName(dependency));
		if (isPlatformComponent(dependency))
			return;
		super.open(dependency);
	}

	@Override
	public <S> TransformedServiceReference<S> accept(DependencyModel dependency, BundleContext context,
	      TransformedServiceReference<S> serviceReference) {
		System.out.println("-----------------------------------------------------------------------");
		System.out.println("Tracking Interceptor - Component Instance :: " + getInstanceName(dependency));
		System.out.println("Tracking Interceptor - Dependency Filter :: " + dependency.getFilter());
		System.out.println("Tracking Interceptor - Device ID :: "
		      + serviceReference.getProperty(GenericDevice.DEVICE_SERIAL_NUMBER));
		System.out.println("Tracking Interceptor - Service Property Original :: "
		      + serviceReference.getProperty("location"));

		if (isPlatformComponent(dependency))
			return serviceReference;

		String deviceId = (String) serviceReference.getProperty(GenericDevice.DEVICE_SERIAL_NUMBER);
		Position position = contextManager.getDevicePosition(deviceId);

		if (position == null) {
			System.out.println("Tracking Interceptor - Not context fro service reference :: ");
			return null;
		}

		serviceReference.addProperty("location", getLocationFromPosition(position));

		System.out.println("Tracking Interceptor - Service Property :: " + serviceReference.getProperty("location"));
		System.out.println("-----------------------------------------------------------------------");

		return serviceReference;
	}

	private String getInstanceName(DependencyModel model) {
		return model.getComponentInstance().getInstanceName();
	}

	private String getLocationFromPosition(Position position) {
		Zone zone = contextManager.getZoneFromPosition(position);
		if (zone == null)
			return "unknown";
		return zone.getId();
	}

	private boolean isPlatformComponent(DependencyModel dependency) {
		String instanceName = getInstanceName(dependency);

		if (instanceName.equals("ContextManager-1") || instanceName.equals("SimulationManager-1"))
			return true;
		return false;
	}

	@Validate
	protected void start() {
		contextManager.addListener(this);
	}

	@Invalidate
	protected void stop() {
		contextManager.removeListener(this);
	}
	
	private void invalidateMatchingServices(LocatedDevice ldevice) {
		List<DependencyModel> list = new ArrayList<DependencyModel>();
		synchronized (this) {
			list.addAll(dependencies);
		}
		for (DependencyModel dep : list) {
			Class[] classes = ldevice.getDeviceObject().getClass().getInterfaces();
			for (Class clazz : classes) {
				if (dep.getSpecification().equals(clazz)) {
					dep.invalidateMatchingServices();
					break;
				}				
	      }
		}
	}

	@Override
	public void deviceAdded(LocatedDevice ldevice) {
		invalidateMatchingServices(ldevice);		
	}
	
	

	@Override
	public void deviceRemoved(LocatedDevice ldevice) {
		// TODO Auto-generated method stub
	}

	@Override
	public void deviceMoved(LocatedDevice ldevice, Position oldPosition, Position newPosition) {
		if (getLocationFromPosition(oldPosition).equals(getLocationFromPosition(newPosition)))
			return;
		invalidateMatchingServices(ldevice);
	}

	@Override
	public void deviceAttached(LocatedDevice arg0, LocatedDevice arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void deviceDetached(LocatedDevice arg0, LocatedDevice arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void deviceEvent(LocatedDevice arg0, Object arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void devicePropertyAdded(LocatedDevice arg0, String arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void devicePropertyModified(LocatedDevice arg0, String arg1, Object arg2, Object arg3) {
		// TODO Auto-generated method stub

	}

	@Override
	public void devicePropertyRemoved(LocatedDevice arg0, String arg1) {
		// TODO Auto-generated method stub

	}

}
