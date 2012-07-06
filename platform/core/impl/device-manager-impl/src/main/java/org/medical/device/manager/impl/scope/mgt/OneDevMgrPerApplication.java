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

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.felix.ipojo.ComponentInstance;
import org.apache.felix.ipojo.ConfigurationException;
import org.apache.felix.ipojo.IPojoFactory;
import org.apache.felix.ipojo.InstanceManager;
import org.apache.felix.ipojo.MissingHandlerException;
import org.apache.felix.ipojo.UnacceptableConfiguration;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Invalidate;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.Validate;
import org.apache.felix.ipojo.api.Dependency;
import org.apache.felix.ipojo.api.PrimitiveComponentType;
import org.apache.felix.ipojo.api.Property;
import org.apache.felix.ipojo.api.Service;
import org.medical.application.Application;
import org.medical.application.ApplicationManager;
import org.medical.application.ApplicationTracker;
import org.medical.device.manager.DeviceManager;
import org.medical.device.manager.GlobalDeviceManager;
import org.medical.device.manager.impl.DeviceManagerImpl;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceFactory;
import org.osgi.framework.ServiceRegistration;

/**
 * This service factory returns a new component instance for each digital service.
 * 
 * @author Thomas Leveque
 *
 */
@Instantiate(name="one-device-mgr-per-app-singleton")
@Component(name="one-device-mgr-per-app")
public class OneDevMgrPerApplication implements ServiceFactory, ApplicationTracker {
	
	private static final String APP_ID_PROP_NAME = "app.id";
	
	/*
	 * Dependencies
	 */
	@Requires
	private ApplicationManager _appMgr;

	/*
	 * Used to enable the factory only when the device manager is valid.
	 */
	@SuppressWarnings("unused")
	@Requires
	private GlobalDeviceManager _deviceManager;
	
	private PrimitiveComponentType _deviceMgrComp;
	
	/*
	 * Internal state
	 */
	private Map<String /* application id */, DeviceManager> _instancesPerApp = new HashMap<String, DeviceManager>();
	
	private Map<String /* bundle symbolic name */, DeviceManager> _instancesPerBundle = new HashMap<String, DeviceManager>();
	
//	private Map<DeviceManager, Integer> _instanceUseNbs = new HashMap<DeviceManager, Integer>();

	private BundleContext _context;

	private ServiceRegistration _registeredFactoryRef;
	
	private OneDevMgrPerApplication(BundleContext context) {
		_context = context;
	}

	@Override
	public Object getService(Bundle bundle, ServiceRegistration registration) {
		
		final String symbolicName = bundle.getSymbolicName();
		
		synchronized (_instancesPerBundle) {
		
			// create new instance if needed
			DeviceManager devMgr = _instancesPerBundle.get(symbolicName);
			if (devMgr != null)
				return devMgr;
			
			Dictionary configuration = new Hashtable();
			final Application application = getApplication(symbolicName);
			String appId = null;
			if (application != null) {
				appId = application.getId();
			} else {
				appId = DeviceManagerImpl.INTERNAL_MANAGER_APP_ID;
			}
			devMgr = _instancesPerApp.get(appId);
			if (devMgr == null) {
				configuration.put(APP_ID_PROP_NAME, appId);
				configuration.put("instance.name", getInstanceName(appId));
				try {
					InstanceManager compInst = (InstanceManager) _deviceMgrComp
							.createInstance(configuration);
					devMgr = (DeviceManager) compInst.getPojoObject();
				} catch (UnacceptableConfiguration e) {
					e.printStackTrace();
					return null;
				} catch (MissingHandlerException e) {
					e.printStackTrace();
					return null;
				} catch (ConfigurationException e) {
					e.printStackTrace();
					return null;
				}
				_instancesPerApp.put(appId, devMgr);
			}
			_instancesPerBundle.put(symbolicName, devMgr);
			
			//TODO remove usage number monitoring if no more needed
//			// add usage number
//			Integer usageNb = _instanceUseNbs.get(devMgr);
//			if (usageNb == null) {
//				usageNb = 0;
//			}
//			usageNb++;
//			_instanceUseNbs.put(devMgr, usageNb);
			
			return devMgr;
		}
	}

	private String getInstanceName(String appId) {
		return appId + "-device-mgr";
	}

	private Application getApplication(String bundleSymbolicName) {
		return _appMgr.getApplicationOfBundle(bundleSymbolicName);
	}

	@Override
	public void ungetService(Bundle bundle, ServiceRegistration registration,
			Object service) {
		
//		final String symbolicName = bundle.getSymbolicName();
//		
//		synchronized (_instancesPerBundle) {
//			DeviceManager devMgr = _instancesPerBundle.get(symbolicName);
//			
//			// add usage number
//			Integer usageNb = _instanceUseNbs.get(devMgr);
//			if (usageNb == 0) {
//				usageNb = 0;
//			}
//			usageNb--;
//			if (usageNb <= 0) {
//				_instanceUseNbs.remove(devMgr);
//				
//			} else
//				_instanceUseNbs.put(devMgr, usageNb);
//		}
	}

	@Validate
	public void start() {
		_appMgr.addApplicationListener(this);
		
		// create factory of DeviceManager
		_deviceMgrComp = new PrimitiveComponentType()
		.setComponentTypeName("device-manager")
	    .setBundleContext(_context)
	    .setImmediate(true)
	    .setPublic(false)
	    .setClassName(DeviceManagerImpl.class.getName())
	    .addService(new Service().setSpecification(DeviceManager.class.getName()))
	    .addDependency(new Dependency().setField("_devMgrDelegate"))
	    .addDependency(new Dependency().setField("_appMgr"))
	    .setValidateMethod("start")
	    .setInvalidateMethod("stop")
	    .addProperty(new Property().setField("_appId").setMandatory(true).setName("app.id"));
		_deviceMgrComp.start();
		
		_registeredFactoryRef = _context.registerService(DeviceManager.class.getName(), this, new Hashtable());
	}
	
	@Invalidate
	public void stop() {
		if (_registeredFactoryRef != null)
			_registeredFactoryRef.unregister();
		_registeredFactoryRef = null;
		
		synchronized (_instancesPerBundle) {
			Set<String> appIds = new HashSet<String>(_instancesPerApp.keySet());
			for (String appId : appIds) {
				disposeDevMgr(appId);
			}
		}
		
		if (_deviceMgrComp != null) {
			_deviceMgrComp.stop();
			_deviceMgrComp = null;
		}
		
		synchronized (_instancesPerBundle) {
			_instancesPerBundle.clear();
			// _instanceUseNbs.clear();
			_instancesPerApp.clear();
		}
	}

	@Override
	public void addApplication(Application app) {
		// do nothing
	}

	@Override
	public void removeApplication(Application app) {
		if (app.getId().equals(DeviceManagerImpl.INTERNAL_MANAGER_APP_ID))
			return;
		
		String appId = app.getId();
		disposeDevMgr(appId);
	}

	private void disposeDevMgr(String appId) {
		synchronized (_instancesPerBundle) {
			_instancesPerApp.remove(appId);

			List<String> namesToRem = new ArrayList<String>();
			for (String curSymbolicName : _instancesPerBundle.keySet()) {
				DeviceManager curDev = _instancesPerBundle.get(curSymbolicName);
				if (appId.equals(curDev.getApplicationId()))
					namesToRem.add(curSymbolicName);
			}
			for (String curSymbolicName : namesToRem)
				_instancesPerBundle.remove(curSymbolicName);

			if (_deviceMgrComp != null)
				_deviceMgrComp.disposeInstance(getInstanceName(appId));
		}
	}

}
