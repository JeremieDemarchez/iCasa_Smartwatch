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
import org.medical.common.impl.EntityImpl;
import org.medical.device.manager.AvailableDevice;
import org.medical.device.manager.DetailedFault;
import org.medical.device.manager.Device;
import org.medical.device.manager.DeviceProxy;
import org.medical.device.manager.DiscoveryRecord;
import org.medical.device.manager.Fault;
import org.medical.device.manager.KnownDevice;
import org.medical.device.manager.ProvidedDevice;
import org.medical.device.manager.Service;
import org.medical.device.manager.ApplicationDevice;
import org.omg.CORBA._PolicyStub;

/**
 * 
 * 
 * @author Thomas Leveque
 *
 */
public class AvailableDeviceImpl extends SynchronizedDevice implements AvailableDevice {

	private KnownDevice _knownDevice;

	public AvailableDeviceImpl(ProvidedDevice device) {
		super(device);
	}

	@Override
	public List<DetailedFault> getDetailedFaults() {
		return  getDelegateDevice().getDetailedFaults();
	}

	@Override
	public boolean hasExclusiveAccess() {
		return  getDelegateDevice().hasExclusiveAccess();//TODO
	}

	@Override
	public List<DiscoveryRecord> getDiscoveryRecords() {
		return Collections.emptyList(); //TODO
	}

	@Override
	public synchronized List<ApplicationDevice> getVisibleDevices() {
		if (_knownDevice == null)
			return null;
		
		return _knownDevice.getApplicationDevices();
	}

	@Override
	public synchronized Application getApplicationOwner() {
		if (_knownDevice == null)
			return null;
		
		return _knownDevice.getAppOwner();
	}

	@Override
	public List<DeviceProxy> getDeviceProxies() {
		List<DeviceProxy> proxies = new ArrayList<DeviceProxy>();
		Device delegateDev = getDelegateDevice();
		if (delegateDev != null)
			proxies.add(new DeviceProxy(delegateDev, null)); //TODO manage discovery records
		return proxies;
	}

	public synchronized void setKnownDevice(KnownDevice knownDevice) {
		_knownDevice = knownDevice;
	}

	@Override
	public synchronized KnownDevice getKnownDevice() {
		return _knownDevice;
	}

	@Override
	public Object getDeviceProxy(Class... interfaces) {
		//TODO use getDeviceProxies
		Device delegateDev = getDelegateDevice();
		for (Class interf : interfaces) {
			if (!(interf.isInstance(delegateDev)))
				return null;
		}
		
		return delegateDev;
	}
}
