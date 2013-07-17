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
package fr.liglab.adele.icasa.dependency.manager2;

import java.util.Comparator;


import org.apache.felix.ipojo.ComponentInstance;
import org.apache.felix.ipojo.ConfigurationException;
import org.apache.felix.ipojo.PrimitiveHandler;
import org.apache.felix.ipojo.handlers.dependency.Dependency;
import org.apache.felix.ipojo.handlers.dependency.DependencyCallback;
import org.apache.felix.ipojo.handlers.dependency.DependencyHandler;
import org.apache.felix.ipojo.util.Logger;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;
import org.osgi.framework.ServiceReference;

public class DeviceDependency extends Dependency {
	
	private ComponentInstance instance;
	
	private PrimitiveHandler m_handler;

	public DeviceDependency(DependencyHandler handler, String field, Class spec, Filter filter, boolean isOptional,
	      boolean isAggregate, boolean nullable, boolean isProxy, String identity, BundleContext context, int policy,
	      Comparator cmp, String defaultImplem) {
		super(handler, field, spec, filter, isOptional, isAggregate, nullable, isProxy, identity, context, policy, cmp,
		      defaultImplem);
		instance = handler.getInstanceManager();
		m_handler = handler; 
		
		System.out.println("------------------------> Instance Name: " + instance.getInstanceName() + " Dependency: " + field);
	}
	
   public DependencyCallback[] getCallbacks() {
      return super.getCallbacks();
  }
   
   public void setType(int type) {
   	super.setType(type);
   }

   
   public void addConstructorInjection(int index) throws ConfigurationException {
   	super.addConstructorInjection(index);
   }
   
   public void addDependencyCallback(DependencyCallback callback) {
   	super.addDependencyCallback(callback);
   }
   
   public void onObjectCreation(Object pojo) {
   	super.onObjectCreation(pojo);
   }
   
   @Override
   public boolean match(ServiceReference ref) {
   	// Determines if service reference match the "filters"
   	// here we have to call the AccessManager Service
   	
   	System.out.println("--------------------------------------> " + instance.getInstanceName() +  "Device Number: " + ref);
   	
   	//String value = (String) ref.getProperty("device.serialNumber");   	
   	   	
      return super.match(ref);
   }
}
