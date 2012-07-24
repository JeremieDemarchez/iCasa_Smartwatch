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
import java.util.Set;

import org.medical.application.Application;
import org.medical.common.StateVariable;
import org.medical.common.StateVariableExtender;
import org.medical.common.StateVariableListener;
import org.medical.device.manager.ApplicationDevice;
import org.medical.device.manager.AvailableDevice;
import org.medical.device.manager.DetailedFault;
import org.medical.device.manager.DeviceDependencies;
import org.medical.device.manager.Fault;
import org.medical.device.manager.KnownDevice;
import org.medical.device.manager.Service;

/**
 * Default implementation of an application device which allows to change access right at runtime. 
 * 
 * @author Thomas Leveque
 *
 */
public class ApplicationDeviceImpl implements ApplicationDevice {

	private static final String STRING = new DeviceDependencies().toString();

	private Application _app;
	
	private KnownDevice _knownDev;
	
	private ApplicationDevice _protectedDevice;
	
	private List<Object> _proxies = new ArrayList<Object>();

	public ApplicationDeviceImpl(KnownDeviceImpl knownDev, Application app) {
		_app = app;
		_knownDev = knownDev;
		knownDev.addApplicationDevice(this);
	}
	
	/*
	 * TODO should manage access rights
	 */

	@Override
	public String getName() {
		return _knownDev.getName();
	}

	@Override
	public String getTypeId() {
		return _knownDev.getTypeId();
	}

	@Override
	public String getVendor() {
		return _knownDev.getVendor();
	}

	@Override
	public boolean hasFault() {
		return _knownDev.hasFault();
	}

	@Override
	public Fault getGlobalFault() {
		return _knownDev.getGlobalFault();
	}

	@Override
	public List<DetailedFault> getDetailedFaults() {
		return _knownDev.getDetailedFaults();
	}

	@Override
	public boolean isAvailable() {
		return _knownDev.isAvailable();
	}

	@Override
	public boolean hasExclusiveAccess() {
		return _knownDev.hasExclusiveAccess();
	}

	@Override
	public List<Service> getServices() {
		return _knownDev.getServices();
	}

	@Override
	public Set<String> getVariableNames() {
		return _knownDev.getVariableNames();
	}

	@Override
	public Object getVariableValue(String propertyName) {
		return _knownDev.getVariableValue(propertyName);
	}

	@Override
	public void setVariableValue(String propertyName, Object value) {
		_knownDev.setVariableValue(propertyName, value);
	}

	@Override
	public List<StateVariable> getStateVariables() {
		return _knownDev.getStateVariables();
	}

	@Override
	public void addVariableExtender(StateVariableExtender extender) {
		_knownDev.addVariableExtender(extender);
	}

	@Override
	public void removeVariableExtender(StateVariableExtender extender) {
		_knownDev.removeVariableExtender(extender);
	}

	@Override
	public List<StateVariableExtender> getVariableExtenders() {
		return _knownDev.getVariableExtenders();
	}

	@Override
	public String getId() {
		return _knownDev.getId();
	}

	@Override
	public AvailableDevice getAvailableDevice() {
		return _knownDev.getAvailableDevice();
	}

	@Override
	public KnownDevice getKnownDevice() {
		return _knownDev;
	}

	@Override
	public Application getApplication() {
		return _app;
	}

	@Override
	public Application getAppOwner() {
		return _knownDev.getAppOwner();
	}
	
	@Override
	public StateVariable getStateVariable(String propertyName) {
		return _knownDev.getStateVariable(propertyName);
	}

	@Override
	public ApplicationDevice getProtectedDevice() {
		if (_protectedDevice == null) {
			_protectedDevice = new ProtectedDeviceImpl(this);
		}
		
		return _protectedDevice;
	}

	@Override
	public boolean hasServiceType(String spec) {
		return _knownDev.hasServiceType(spec);
	}

	@Override
	public Service getService(String servicetype) {
		return _knownDev.getService(servicetype);
	}

	@Override
	public Object getDeviceProxy(Class... interfaces) {
		synchronized (_proxies) {
			for (Object proxy : _proxies) {
				boolean match = true;
				for (Class interf : interfaces) {
					if (!(interf.isInstance(proxy))) {
						match = false;
						break;
					}
				}
				if (match)
					return proxy;
			}
		}
		
		return null;
	}

	@Override
	public List<Object> getDeviceProxies() {
		synchronized (_proxies) {
			List<Object> proxies = new ArrayList<Object>(_proxies);
			return proxies;
		}
	}
	
	public void addProxy(Object proxy) {
		synchronized (_proxies) {
			_proxies.add(proxy);
		}
	}
	
	public void removeProxy(Object proxy) {
		synchronized (_proxies) {
			_proxies.remove(proxy);
		}
	}

	@Override
	public boolean isProtected() {
		return false;
	}

	@Override
	public int hashCode() {
		return getId().hashCode() + getApplication().getId().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof ApplicationDevice))
			return false;
		
		ApplicationDevice other = (ApplicationDevice) obj;
		return (getId().equals(other.getId())) && (getApplication().getId().equals(other.getApplication().getId()));
	}

	@Override
	public void addVariableListener(StateVariableListener listener) {
		_knownDev.addVariableListener(listener);
	}

	@Override
	public void removeVariableListener(StateVariableListener listener) {
		_knownDev.removeVariableListener(listener);
	}
	
	public void setKnownDevice(KnownDevice dev) {
		_knownDev = dev;
	}

	@Override
	public boolean hasStateVariable(String varName) {
		return _knownDev.hasStateVariable(varName);
	}
}
