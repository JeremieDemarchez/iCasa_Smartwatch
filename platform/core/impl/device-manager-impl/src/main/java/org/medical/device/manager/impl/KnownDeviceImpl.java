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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.medical.application.Application;
import org.medical.common.Attributable;
import org.medical.common.StateVariable;
import org.medical.common.StateVariableListener;
import org.medical.common.impl.StateVariableProxy;
import org.medical.common.impl.StateVariableImpl;
import org.medical.device.manager.ApplicationDevice;
import org.medical.device.manager.AvailableDevice;
import org.medical.device.manager.KnownDevice;
import org.medical.device.manager.Operation;
import org.medical.device.manager.OperationParameter;
import org.medical.device.manager.Service;
import org.medical.device.manager.VariableLifeCycle;
import org.medical.device.manager.impl.util.AppendFaultIfPossibleStateVar;
import org.medical.device.manager.util.AbstractDevice;

/**
 * Implementation of known device.
 * 
 * @author Thomas Leveque
 *
 */
public class KnownDeviceImpl extends SynchronizedDevice implements KnownDevice {
	
	private Application _ownerApp;

	private Map<Application, ApplicationDevice> _visibleDevices = new HashMap<Application, ApplicationDevice>();
	
	public KnownDeviceImpl(AvailableDeviceImpl device) {
		super(device.getId(), device.getName(), device.getVendor(), device.getTypeId());
		addAvailableDevice(device);
	}
	
	protected void customizeVariables() {
		replaceByDelegateVar(NAME_PROP_NAME);
		replaceByDelegateVar(VENDOR_PROP_NAME);
		replaceByDelegateAppendVar(FAULTS_PROP_NAME);
		replaceByDelegateVar(TYPE_PROP_NAME);
		
		// availability attribute
		StateVariable originalVar = getInternalVariable(AVAILABLE_PROP_NAME);
		changeVariableImplem(new AvailabilityStateVar(originalVar, getAvailableDevice()));
	}
	
	private void replaceByDelegateAppendVar(String varName) {
		StateVariable originalVar = getInternalVariable(varName);
		changeVariableImplem(new AppendFaultIfPossibleStateVar(originalVar, getAvailableDevice()));
	}

	private void replaceByDelegateVar(String varName) {
		StateVariable originalVar = getInternalVariable(varName);
		changeVariableImplem(new DeriveIfPossibleStateVar(originalVar, getAvailableDevice()));
	}

	@Override
	public AvailableDevice getAvailableDevice() {
		return (AvailableDevice) getDelegateDevice();
	}

	@Override
	public List<ApplicationDevice> getApplicationDevices() {
		return new ArrayList<ApplicationDevice>(_visibleDevices.values());
	}

	@Override
	public Application getAppOwner() {
		return _ownerApp;
	}

	@Override
	public boolean hasExclusiveAccess() {
		AvailableDevice device = getAvailableDevice();
		if (device == null)
			return false;
		
		return  device.hasExclusiveAccess();//TODO
	}

	public void addApplicationDevice(ApplicationDevice appDev) {
		synchronized (_visibleDevices) {
			_visibleDevices.put(appDev.getApplication(), appDev);
		}
	}
	
	public ApplicationDevice removeApplicationDevice(Application app) {
		synchronized (_visibleDevices) {
			return _visibleDevices.remove(app);
		}
	}

	@Override
	public ApplicationDevice getApplicationDevice(Application app) {
		if (app == null)
			throw new IllegalArgumentException("application parameter cannot be null.");
		
		synchronized(_visibleDevices) {
			return _visibleDevices.get(app);
		}
	}

	public void removeAvailableDevice() {
		setDelegateDevice(null);
	}

	public void addAvailableDevice(AvailableDeviceImpl device) {
		synchronized (_lockStructChanges) {
			device.setKnownDevice(this);
			setDelegateDevice(device);
		}
	}

	@Override
	public int hashCode() {
		return getId().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (! (obj instanceof KnownDevice))
			return false;
		
		KnownDevice other = (KnownDevice) obj;
		return other.getId().equals(getId());
	}
	
}
