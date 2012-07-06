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
package org.medical.device.manager.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.medical.application.Application;
import org.medical.application.ApplicationManager;
import org.medical.application.ApplicationState;
import org.medical.application.ApplicationTracker;
import org.medical.device.manager.impl.app.ApplicationCategoryImpl;
import org.medical.device.manager.ApplicationDevice;
import org.medical.device.manager.DependRegistration;
import org.medical.device.manager.Device;
import org.medical.device.manager.DeviceDependencies;
import org.medical.device.manager.DeviceManager;
import org.medical.device.manager.DeviceRequest;
import org.medical.device.manager.GlobalDeviceManager;
import org.medical.device.manager.impl.app.ApplicationImpl;
import org.osgi.framework.BundleContext;

/**
 * An instance of this component is created for each digital service by the OneDevMgrPerDigitalService component.
 * This component is created programmatically by OneDevMgrPerApplication class.
 * Commented annotations represent configuration defined by it.
 * 
 * @author Thomas Leveque
 *
 */
//@Component(name="device-manager", architecture=true, publicFactory=false)
//@Provides(specifications=DeviceManager.class)
public class DeviceManagerImpl implements DeviceManager, ApplicationTracker {
	
	public final static String INTERNAL_MANAGER_APP_ID = "##InternalFramework##"; 
	
	//@Property(name="app.id", mandatory=true)
	private String _appId;
	
	private Application _app;
	
	//@Requires
	private GlobalDeviceManager _devMgrDelegate;
	
	//@Requires
	private ApplicationManager _appMgr;
	
	private BundleContext _context;
	
	public DeviceManagerImpl(BundleContext context) {
		_context = context;
	}

	@Override
	public synchronized void addApplication(Application app) {
		if (_appId.equals(app.getId())) {
			_app = app;
		}
	}

	@Override
	public synchronized void removeApplication(Application app) {
		if (_appId.equals(app.getId())) {
			_app = null;
		}
	}

	public Application getApplication() {
		return _app;
	}
	
	public String getApplicationId() {
		if (_app == null)
			return _appId;
		else
			return _app.getId();
	}
	
	//@Validate
	private void start() {
		if (_appId.equals(INTERNAL_MANAGER_APP_ID))
			_app = new ApplicationImpl(INTERNAL_MANAGER_APP_ID, "LIG/ADELE", new ApplicationCategoryImpl("Core Platform"), ApplicationState.STARTED, _context);
		else
			_appMgr.addApplicationListener(this);
	}
	
	//@Invalidate
	private void stop() {
		_appMgr.removeApplicationListener(this);
		if (_app != null)
			_app = null;
	}

	@Override
	public List<ApplicationDevice> getDevices(DeviceRequest request) {
		return _devMgrDelegate.getDeviceDescriptions(request, _app);
	}

	@Override
	public DependRegistration addDependencies(DeviceDependencies dependencies) {
		return _devMgrDelegate.addDependencies(dependencies, _app);
	}

}
