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
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.medical.application.Application;
import org.medical.device.manager.ApplicationDevice;
import org.medical.device.manager.DependRegistration;
import org.medical.device.manager.Device;
import org.medical.device.manager.DeviceDependencies;
import org.medical.device.manager.KnownDevice;

public class DependRegistrationImpl implements DependRegistration, DevicetrackerCustomizer {

	private String _appId;
	
	private GlobalDeviceManagerImpl _devMgr;
	
	private DeviceDependencies _deps;
	
	private boolean _registered = false;
	
	private boolean _resolved = false;

	private String _id;

	private DeviceTracker _devTracker;
	
	private final List<ApplicationDevice> _resolvedDevices = new ArrayList<ApplicationDevice>();

	public DependRegistrationImpl(DeviceDependencies deps, Application app,
			GlobalDeviceManagerImpl devMgr) {
		_devMgr = devMgr;
		_deps = deps;
		_appId = app.getId();
		_id = UUID.randomUUID().toString();
	}

	@Override
	public void unregister() {
		_registered = false;
		_devMgr.removeDependencies(this);
	}

	public Application getApplication() {
		return _devMgr.getApplication(_appId);
	}

	public DeviceDependencies getDependencies() {
		return _deps;
	}

	@Override
	public boolean isRegistered() {
		return _registered;
	}

	@Override
	public String getId() {
		return _id;
	}

	@Override
	public void updates() {
		_devMgr.removeDependencies(this);
		_id = UUID.randomUUID().toString();
		_devMgr.updatesDependencies(this);
	}

	@Override
	public boolean isResolved() {
		return _resolved;
	}

	@Override
	public List<ApplicationDevice> getResolvedDevices() {
		return _resolvedDevices ;
	}
	
	private void addResolvedDevice(ApplicationDevice device) {
		synchronized(_resolvedDevices) {
			_resolvedDevices.add(device);
		}
	}
	
	private void removeResolvedDevice(ApplicationDevice device) {
		synchronized(_resolvedDevices) {
			_resolvedDevices.remove(device);
		}
		_devMgr.notifyNoMoreUse(this, device);
	}

	public void start() {
		_devTracker = new DeviceTracker(_devMgr, getDependencies(), this);
		_devTracker.open();
		_registered = true;
	}
	
	public void stop() {
		if (_devTracker != null) {
			_devTracker.close();
			_devTracker = null;
		}
		_registered = false;
	}

	@Override
	public void addDevice(DeviceEvent event) {
		synchronized(_resolvedDevices) {
			if (_deps.isRequiresOne() && isResolved())
				return;
			
			// create application devices if needed
			boolean createAppDev = !_deps.isCreatedOnDemand() && 
					((_deps.isRequiresOne() && !isResolved())
					|| _deps.isRequiresAll());
			if (createAppDev) {
				KnownDevice knownDev = (KnownDevice) event.getDevice();
				ApplicationDevice appDev = createApplicationDevice(knownDev);
				addResolvedDevice(appDev);
			}
			
			setResolved(computeResolveStatus());
		}
	}

	private ApplicationDevice createApplicationDevice(KnownDevice knownDev) {
		
		final List<Class> exportedInterfaces = _deps.getExportedInterfaces();
		
		return _devMgr.createApplicationDevice(getApplication(), knownDev, exportedInterfaces);
	}

	private boolean computeResolveStatus() {
		final List<ApplicationDevice> resolvedDevices = getResolvedDevices();
		
		// check that proxies have been created
		final List<Class> exportedInterfaces = _deps.getExportedInterfaces();
		if (!exportedInterfaces.isEmpty()) {
			for (ApplicationDevice appDev : resolvedDevices) {
				Object devProxy = appDev.getDeviceProxy(exportedInterfaces.toArray(new Class[exportedInterfaces.size()]));
				if (devProxy == null) 
					return false;
			}
		}
		
		if (_deps.isOptional()) {
			return true;
		}
		
		if (!resolvedDevices.isEmpty()) {
			return true;
		}
		
		//TODO check that access rights are ok
		
		return false;
	}

	private void setResolved(boolean resolved) {
		//TODO notify resolve status change
		_resolved = resolved;
	}

	@Override
	public void updateDevice(DeviceEvent event) {
		// do nothing
	}

	@Override
	public void removeDevice(DeviceEvent event) {
		synchronized(_resolvedDevices) {
			KnownDevice knownDev = (KnownDevice) event.getDevice();
			ApplicationDevice appDev = knownDev.getApplicationDevice(getApplication());
			if (appDev != null)
				removeResolvedDevice(appDev);
			
			boolean resolved = computeResolveStatus();
			if (resolved)
				return;
			
			// try to find another device which match
			final Set<KnownDevice> matchingDevices = _devTracker.getMatchingKnownDevices();
			Application app = getApplication();
			for (KnownDevice dev : matchingDevices) {
				ApplicationDevice curAppDev = dev.getApplicationDevice(app);
				if (curAppDev != null) {
					addResolvedDevice(curAppDev);
					break;
				}
			}
			resolved = computeResolveStatus();
			if (!resolved) {
				// try to create a new application device
				for (KnownDevice dev : matchingDevices) {
					ApplicationDevice curAppDev = dev.getApplicationDevice(app);
					if (curAppDev == null) {
						curAppDev = createApplicationDevice(knownDev);
						addResolvedDevice(curAppDev);
						break;
					}
				}
			}
			
			setResolved(computeResolveStatus());
		}
	}
	
	public boolean uses(Device device) {
		for (ApplicationDevice appDev : getResolvedDevices())
			if (appDev.getId().equals(device.getId()))
				return true;
		
		return false;
	}

}
