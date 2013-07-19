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
import fr.liglab.adele.icasa.application.Application;
import fr.liglab.adele.icasa.application.ApplicationManager;
import fr.liglab.adele.icasa.dependency.manager2.DeviceDependency;
import fr.liglab.adele.icasa.device.GenericDevice;

@Component(name = "DeviceAccessTrackingInterceptor")
@Provides(properties = { @StaticServiceProperty(name = "target", value = "(objectClass=fr.liglab.adele.icasa.device.GenericDevice)", type = "java.lang.String") })
@Instantiate(name = "DeviceAccessTrackingInterceptor-0")
public class DeviceAccessTrackingInterceptor implements ServiceTrackingInterceptor {

	@Requires
	private AccessManager accessManager;

	@Requires
	private ApplicationManager applicationManager;

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

			String bundleName = context.getBundle().getSymbolicName();

			Application app = applicationManager.getApplicationOfBundle(bundleName);

			// No application associated neither Device Identifier
			if (app == null || !ref.contains(GenericDevice.DEVICE_SERIAL_NUMBER)) {
				System.out.println("%ERROR% Application ref " + app + " --- Contains Serial Number: "
				      + ref.contains(GenericDevice.DEVICE_SERIAL_NUMBER));
				return null;
			}

			String deviceId = (String) ref.get(GenericDevice.DEVICE_SERIAL_NUMBER);

			AccessRight right = accessManager.getAccessRight(app.getId(), deviceId);

			System.out.println("========================================================");
			System.out.println("====> Dependency " + deviceDependency.getId());
			System.out.println("====> Component " + deviceDependency.getComponentInstance().getInstanceName());
			System.out.println("====> Application " + app.getId());
			System.out.println("====> Device ID: " + deviceId);
			System.out.println("====> Access: " + right.hasDeviceAccess());
			System.out.println("========================================================");

			if (!right.hasDeviceAccess()) {
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

}
