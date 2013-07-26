/**
 *
 *   Copyright 2011-2012 Universite Joseph Fourier, LIG, ADELE Research
 *   Group Licensed under the Apache License, Version 2.0 (the "License");
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
package fr.liglab.adele.icasa.dependency.manager2.interceptor;

import java.util.ArrayList;
import java.util.List;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.StaticServiceProperty;
import org.apache.felix.ipojo.dependency.interceptors.ServiceTrackingInterceptor;
import org.apache.felix.ipojo.dependency.interceptors.TransformedServiceReference;
import org.apache.felix.ipojo.util.DependencyModel;
import org.osgi.framework.BundleContext;

import fr.liglab.adele.icasa.ContextManager;
import fr.liglab.adele.icasa.access.AccessManager;
import fr.liglab.adele.icasa.access.AccessRight;
import fr.liglab.adele.icasa.dependency.manager2.DeviceDependency;
import fr.liglab.adele.icasa.device.GenericDevice;
import fr.liglab.adele.icasa.location.LocatedDevice;

@Component(name = "DeviceAccessTrackingInterceptor")
@Provides(specifications = { ServiceTrackingInterceptor.class }, properties = { @StaticServiceProperty(name = "target", value = "(objectClass=fr.liglab.adele.icasa.device.GenericDevice)", type = "java.lang.String") })
@Instantiate(name = "DeviceAccessTrackingInterceptor-0")
public class DeviceAccessTrackingInterceptor implements ServiceTrackingInterceptor {

	@Requires
	private AccessManager accessManager;

	//@Requires
	//private ApplicationManager applicationManager;

	// @Requires
	// private ContextManager contextManager;

	/**
	 * The set of managed dependencies. Access must be guarded by the monitor lock.
	 */
	protected final List<DeviceDependency> dependencies = new ArrayList<DeviceDependency>();

	@Override
	public void open(DependencyModel dependency) {
		if (isPlatformComponent(dependency))
			return;
		if (dependency instanceof DeviceDependency) {
			dependencies.add((DeviceDependency) dependency);
		}
	}

	@Override
	public void close(DependencyModel dependency) {
		if (isPlatformComponent(dependency))
			return;
		synchronized (this) {
			dependencies.remove(dependency);
		}
	}

	@Override
	public <S> TransformedServiceReference<S> accept(DependencyModel dependency, BundleContext context,
	      TransformedServiceReference<S> ref) {

		if (dependency instanceof DeviceDependency) {
			DeviceDependency deviceDependency = (DeviceDependency) dependency;

			//String bundleName = context.getBundle().getSymbolicName();

			//Application app = applicationManager.getApplicationOfBundle(bundleName);

			String appId = deviceDependency.getApplicationId();
			String deviceId = (String) ref.get(GenericDevice.DEVICE_SERIAL_NUMBER);

			// No application associated or Device Id not Found
			if (appId == null || deviceId == null) {
				return null;
			}

			AccessRight accessRight = accessManager.getAccessRight(appId, deviceId);
			deviceDependency.addAccessRight(accessRight);

			if (!accessRight.isVisible()) {
				return null;
			}

		} else {
			// Only platform components has access to the device instances using iPOJO
			if (!isPlatformComponent(dependency)) {
				System.out.println("An application trying to get a device using a standard iPOJO Requires: "
				      + dependency.getComponentInstance().getInstanceName());
				return null;
			} else {
				System.out.println("A platform component accesing a device using a standard iPOJO Requires: "
				      + dependency.getComponentInstance().getInstanceName());
			}
		}

		return ref;
	}

	/**
	 * Determines if the component having this dependency is a iCasa Platform component
	 * 
	 * @param dependency
	 * @return
	 */
	private boolean isPlatformComponent(DependencyModel dependency) {
		String[] specs = dependency.getComponentInstance().getFactory().getComponentDescription()
		      .getprovidedServiceSpecification();

		for (String specification : specs) {
			if (specification.equals(ContextManager.class.getName()))
				return true;
		}

		return false;
	}

	
	/*
	 
	private void invalidateMatchingServices(String appId, LocatedDevice locatedDevice) {
		List<DeviceDependency> copyList = new ArrayList<DeviceDependency>();
		synchronized (this) {
			copyList.addAll(dependencies);
		}
		
		for (DeviceDependency dependency : copyList) {
			if (appId.equals(dependency.getApplicationId())) { // Only for dependecies of this application
				Class[] interfaces = locatedDevice.getDeviceObject().getClass().getInterfaces();
				for (Class interfaze : interfaces) {
					if (dependency.getSpecification().equals(interfaze)) { // Only dependencies using this kind of device
						dependency.invalidateMatchingServices();
					}
				}
			}
		}
	}


	@Override
   public void onAccessRightModified(AccessRight accessRight) {
	   String appId = accessRight.getApplicationId();
	   String deviceId = accessRight.getDeviceId();
	   
	   LocatedDevice locatedDevice = contextManager.getDevice(deviceId);
	   
	   invalidateMatchingServices(appId, locatedDevice);
	   
   }

	@Override
   public void onMethodAccessRightModified(AccessRight accessRight, String methodName) {
	   // Nothing to be done
   }
   */

}
