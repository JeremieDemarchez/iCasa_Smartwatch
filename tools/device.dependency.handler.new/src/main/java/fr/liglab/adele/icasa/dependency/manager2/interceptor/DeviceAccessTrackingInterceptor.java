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
