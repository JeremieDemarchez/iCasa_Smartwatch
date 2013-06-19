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

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.StaticServiceProperty;
import org.apache.felix.ipojo.dependency.interceptors.ServiceTrackingInterceptor;
import org.apache.felix.ipojo.dependency.interceptors.TransformedServiceReference;
import org.apache.felix.ipojo.util.DependencyModel;
import org.osgi.framework.BundleContext;

@Component(name = "ICasaTrackingInterceptor")
@Provides(properties = { @StaticServiceProperty(name = "target", value = "(objectClass=fr.liglab.adele.icasa.device.GenericDevice)", type = "java.lang.String") })
@Instantiate
public class ICasaTrackingInterceptor implements ServiceTrackingInterceptor {

	@Override
	public void close(DependencyModel model) {
		System.out.println("Closing Interceptor :: " + getInstanceName(model));
	}

	@Override
	public void open(DependencyModel model) {
		System.out.println("Opening Interceptor :: " + getInstanceName(model));
	}

	@Override
	public <S> TransformedServiceReference<S> accept(DependencyModel model, BundleContext context,
	      TransformedServiceReference<S> serviceReference) {
		System.out.println("-----------------------------------------------------------------------");
		System.out.println("Tracking Interceptor - Component Instance :: " + getInstanceName(model));
		System.out.println("Tracking Interceptor - Dependency Filter :: " + model.getFilter());
		System.out.println("Tracking Interceptor - Service Property :: " + serviceReference.getProperty("location"));		
		if (getInstanceName(model).equals("ConsumerFakeDevice-2")) {
			System.out.println("Tracking Interceptor - Adding Location Property ");
			serviceReference.addProperty("location", "kitchen");
			return null;
		}
		System.out.println("-----------------------------------------------------------------------");
		return serviceReference;
	}

	private String getInstanceName(DependencyModel model) {
		return model.getComponentInstance().getInstanceName();
	}

}
