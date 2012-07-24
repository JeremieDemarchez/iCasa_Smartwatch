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

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.medical.application.Application;
import org.medical.common.StateVariable;
import org.medical.common.StateVariableExtender;
import org.medical.common.StateVariableListener;
import org.medical.device.manager.AvailableDevice;
import org.medical.device.manager.DetailedFault;
import org.medical.device.manager.DeviceProxy;
import org.medical.device.manager.DiscoveryRecord;
import org.medical.device.manager.Fault;
import org.medical.device.manager.KnownDevice;
import org.medical.device.manager.ProvidedDevice;
import org.medical.device.manager.Service;
import org.medical.device.manager.ApplicationDevice;
import org.omg.CORBA._PolicyStub;

public class AvailableDeviceImpl implements AvailableDevice {

	private ProvidedDevice _device;
	private KnownDevice _knownDevice;

	public AvailableDeviceImpl(ProvidedDevice device) {
		_device = device;
	}

	@Override
	public String getName() {
		return _device.getName();
	}

	@Override
	public String getVendor() {
		return _device.getVendor();
	}

	@Override
	public boolean hasFault() {
		return _device.hasFault();
	}

	@Override
	public Fault getGlobalFault() {
		return _device.getGlobalFault();
	}

	@Override
	public List<DetailedFault> getDetailedFaults() {
		return  _device.getDetailedFaults();
	}

	@Override
	public boolean isAvailable() {
		return  _device.isAvailable();
	}

	@Override
	public boolean hasExclusiveAccess() {
		return  _device.hasExclusiveAccess();//TODO
	}

	@Override
	public List<Service> getServices() {
		return  _device.getServices();
	}

	@Override
	public Set<String> getVariableNames() {
		return  _device.getVariableNames();
	}

	@Override
	public Object getVariableValue(String propertyName) {
		return  _device.getVariableValue(propertyName);
	}

	@Override
	public void setVariableValue(String propertyName, Object value) {
		_device.setVariableValue(propertyName, value);
	}

	@Override
	public List<StateVariable> getStateVariables() {
		return _device.getStateVariables();
	}

	@Override
	public void addVariableExtender(StateVariableExtender extender) {
		_device.addVariableExtender(extender);
	}

	@Override
	public void removeVariableExtender(StateVariableExtender extender) {
		_device.removeVariableExtender(extender);
	}

	@Override
	public List<StateVariableExtender> getVariableExtenders() {
		return _device.getVariableExtenders();
	}

	@Override
	public String getId() {
		return _device.getId();
	}

	@Override
	public List<DiscoveryRecord> getDiscoveryRecords() {
		return Collections.emptyList(); //TODO
	}

	@Override
	public List<ApplicationDevice> getVisibleDevices() {
		if (_knownDevice == null)
			return null;
		
		return _knownDevice.getApplicationDevices();
	}

	@Override
	public Application getApplicationOwner() {
		if (_knownDevice == null)
			return null;
		
		return _knownDevice.getAppOwner();
	}

	@Override
	public List<DeviceProxy> getDeviceProxies() {
		return Collections.emptyList(); //TODO
	}

	@Override
	public String getTypeId() {
		return _device.getTypeId();
	}

	public void setKnownDevice(KnownDevice knownDevice) {
		_knownDevice = knownDevice;
	}

	@Override
	public KnownDevice getKnownDevice() {
		return _knownDevice;
	}

	@Override
	public boolean hasServiceType(String spec) {
		return _knownDevice.hasServiceType(spec);
	}

	@Override
	public Service getService(String servicetype) {
		return _device.getService(servicetype);
	}

	@Override
	public StateVariable getStateVariable(String propertyName) {
		return _device.getStateVariable(propertyName);
	}
	
	@Override
	public void addVariableListener(StateVariableListener listener) {
		_device.addVariableListener(listener);
	}

	@Override
	public void removeVariableListener(StateVariableListener listener) {
		_device.removeVariableListener(listener);
	}

	@Override
	public Object getDeviceProxy(Class... interfaces) {
		//TODO use getDeviceProxies
		for (Class interf : interfaces) {
			if (!(interf.isInstance(_device)))
				return null;
		}
		
		return _device;
	}

	@Override
	public boolean hasStateVariable(String varName) {
		return _device.hasStateVariable(varName);
	}
}
