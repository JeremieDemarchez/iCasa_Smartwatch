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
package org.medical.device.manager.impl.scope.mgt;

import java.util.Collection;
import java.util.Iterator;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;
import org.medical.application.Application;
import org.medical.application.ApplicationManager;
import org.medical.device.manager.ApplicationDevice;
import org.medical.device.manager.AvailableDevice;
import org.medical.device.manager.DeviceManager;
import org.medical.device.manager.FilterDeviceContrib;
import org.medical.device.manager.GlobalDeviceManager;
import org.medical.device.manager.KnownDevice;
import org.medical.device.manager.ProvidedDevice;
import org.medical.device.manager.impl.DeviceManagerImpl;
import org.medical.device.manager.impl.PolicyManager;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.hooks.service.EventHook;
import org.osgi.framework.hooks.service.FindHook;

@Instantiate(name = "application-isolation-impl-singleton")
@Component(name = "application-isolation-impl")
@Provides
public class FromApplicationFindHook implements FindHook, EventHook {

	private final BundleContext _context;

	@Requires
	private ApplicationManager _appMgr;

	@Requires
	private GlobalDeviceManager _devMgr;
	
	@Requires
	private PolicyManager _policyMgr;
	
	@Requires(optional=true)
	private FilterDeviceContrib _filterDevContribs[];

	public FromApplicationFindHook(BundleContext context) {
		_context = context;
	}

	public void find(BundleContext context, String name, String filter,
			boolean allServices, Collection references) {

		try {
			// only application bundles get filtered services
			final Bundle bundle = context.getBundle();
			if (_context.equals(context) || bundle.getBundleId() == 0) {
				return;
			}

			Application app = null;
			if (_appMgr.isApplicationBundle(bundle)) {
				app = _appMgr.getApplicationOfBundle(bundle
					.getSymbolicName());
			}

			Iterator<ServiceReference> iterator = references.iterator();
			while (iterator.hasNext()) {
				ServiceReference sr = iterator.next();
				String[] interfaces = (String[]) sr
						.getProperty(Constants.OBJECTCLASS);
				if (filterServices(app, context, sr, interfaces))
					iterator.remove();
			}

		} catch (Throwable ex) {
			ex.printStackTrace();
		}
	}

	private boolean filterServices(Application app, BundleContext context, ServiceReference sr, String[] interfaces) {
		boolean isFromApp = (app != null);
		
		if (_filterDevContribs != null) {
			for (FilterDeviceContrib devFilter : _filterDevContribs) {
				try {
					if (devFilter.hideDevice(app, sr, interfaces))
						return true;
				} catch (Exception e) {
					e.printStackTrace();
					return false;
				}
			}
		}
		
		for (String spec : interfaces) {
			// manage isolation of private core services
			if (GlobalDeviceManager.class.getName().equals(spec)
					|| KnownDevice.class.getName().equals(spec)
					|| ProvidedDevice.class.getName().equals(spec)
					|| AvailableDevice.class.getName().equals(spec)) {
				return isFromApp;
			}
			
			// manage access to application management
			if (ApplicationManager.class.getName().equals(spec)) {
				return isFromApp && !(_policyMgr.allowAccess(app, sr));
			}

			if (ApplicationDevice.class.getName().equals(spec)) {
				if (!isFromApp)
					return true;
				
				ApplicationDevice appDev = (ApplicationDevice) context.getService(sr);
				context.ungetService(sr);
				String appId = app.getId();
				if (!appDev.getApplication().getId().equals(appId))
					return true;
			}

			if (DeviceManager.class.getName().equals(spec)) {
				// remove device manager of other applications
				DeviceManager devMgr = (DeviceManager) context.getService(sr);
				context.ungetService(sr);
				
				if (!isFromApp && devMgr.getApplicationId().equals(DeviceManagerImpl.INTERNAL_MANAGER_APP_ID)) {
					return false;
				}
				
				if (!isFromApp && !devMgr.getApplicationId().equals(DeviceManagerImpl.INTERNAL_MANAGER_APP_ID)) {
					return true;
				}
					
				if (!devMgr.getApplicationId().equals(app.getId()))
					return true;
			}
		}
		
		return false;
	}

	private void filterContextsByApp(final ServiceReference sr,
			Collection<BundleContext> contexts) {
		String[] interfaces = (String[]) sr.getProperty(Constants.OBJECTCLASS);

		Iterator<BundleContext> iterator = contexts.iterator();
		while (iterator.hasNext()) {
			BundleContext context = iterator.next();
			Bundle bundle = context.getBundle();
			
			if (_context.equals(context) || bundle.getBundleId() == 0) {
				continue;
			}

			Application app = null;
			if (_appMgr.isApplicationBundle(bundle)) {
				app = _appMgr.getApplicationOfBundle(bundle
					.getSymbolicName());
			}
			
			if (filterServices(app, context, sr, interfaces))
				contexts.remove(context);
		}
	}

	@SuppressWarnings("unchecked")
	public void event(ServiceEvent event,
			Collection contexts) {
		
		final ServiceReference serviceReference = event.getServiceReference();
		
		try {
			switch (event.getType()) {

			case ServiceEvent.REGISTERED: {

				filterContextsByApp(serviceReference, contexts);

				break;
			}

			case ServiceEvent.UNREGISTERING: {

				filterContextsByApp(serviceReference, contexts);

				break;
			}

			case ServiceEvent.MODIFIED:
			case ServiceEvent.MODIFIED_ENDMATCH: {

				filterContextsByApp(serviceReference, contexts);

				break;
			}
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

}
