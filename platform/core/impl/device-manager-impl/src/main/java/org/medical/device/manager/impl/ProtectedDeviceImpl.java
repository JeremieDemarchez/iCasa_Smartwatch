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

import java.util.List;
import java.util.Set;

import org.medical.application.Application;
import org.medical.common.StateVariable;
import org.medical.common.StateVariableExtender;
import org.medical.common.StateVariableListener;
import org.medical.device.manager.ApplicationDevice;
import org.medical.device.manager.AvailableDevice;
import org.medical.device.manager.DetailedFault;
import org.medical.device.manager.Device;
import org.medical.device.manager.Fault;
import org.medical.device.manager.KnownDevice;
import org.medical.device.manager.Service;

public class ProtectedDeviceImpl implements ApplicationDevice  {

	private static final String ILLEGAL_ACCESS_ERROR_MSG = "You cannot access to internal device representation of a protected device.";
	private final ApplicationDevice _appDev;

	public ProtectedDeviceImpl(ApplicationDevice appDev) {
		_appDev = appDev;
	}

	@Override
	public Set<String> getVariableNames() {
		return _appDev.getVariableNames();
	}

	@Override
	public Object getVariableValue(String propertyName) {
		return _appDev.getVariableValue(propertyName);
	}

	@Override
	public void setVariableValue(String propertyName, Object value) {
		_appDev.setVariableValue(propertyName, value);
	}

	@Override
	public List<StateVariable> getStateVariables() {
		return _appDev.getStateVariables();
	}

	@Override
	public void addVariableExtender(StateVariableExtender extender) {
		_appDev.addVariableExtender(extender);
	}

	@Override
	public void removeVariableExtender(StateVariableExtender extender) {
		_appDev.removeVariableExtender(extender);
	}

	@Override
	public List<StateVariableExtender> getVariableExtenders() {
		return _appDev.getVariableExtenders();
	}

	@Override
	public String getId() {
		return _appDev.getId();
	}

	@Override
	public String getName() {
		return _appDev.getName();
	}

	@Override
	public String getTypeId() {
		return _appDev.getTypeId();
	}

	@Override
	public String getVendor() {
		return _appDev.getVendor();
	}

	@Override
	public boolean hasFault() {
		return _appDev.hasFault();
	}

	@Override
	public Fault getGlobalFault() {
		return _appDev.getGlobalFault();
	}

	@Override
	public List<DetailedFault> getDetailedFaults() {
		return _appDev.getDetailedFaults();
	}

	@Override
	public boolean isAvailable() {
		return _appDev.isAvailable();
	}

	@Override
	public boolean hasExclusiveAccess() {
		return _appDev.hasExclusiveAccess();
	}

	@Override
	public List<Service> getServices() {
		return _appDev.getServices();
	}

	@Override
	public boolean hasServiceType(String spec) {
		return _appDev.hasServiceType(spec);
	}
	
	@Override
	public Service getService(String servicetype) {
		return _appDev.getService(servicetype);
	}

	@Override
	public AvailableDevice getAvailableDevice() {
		throw new IllegalArgumentException(ILLEGAL_ACCESS_ERROR_MSG);
	}

	@Override
	public KnownDevice getKnownDevice() {
		throw new IllegalArgumentException(ILLEGAL_ACCESS_ERROR_MSG);
	}

	@Override
	public Application getApplication() {
		return _appDev.getApplication();
	}

	@Override
	public Application getAppOwner() {
		throw new IllegalArgumentException(ILLEGAL_ACCESS_ERROR_MSG);
	}

	@Override
	public Object getDeviceProxy(Class... interfaces) {
		return _appDev.getDeviceProxy(interfaces);
	}

	@Override
	public List<Object> getDeviceProxies() {
		return _appDev.getDeviceProxies();
	}

	@Override
	public ApplicationDevice getProtectedDevice() {
		return this;
	}

	@Override
	public boolean isProtected() {
		return true;
	}

	@Override
	public StateVariable getStateVariable(String propertyName) {
		return _appDev.getStateVariable(propertyName);
	}

	@Override
	public void addVariableListener(StateVariableListener listener) {
		_appDev.addVariableListener(listener);
	}

	@Override
	public void removeVariableListener(StateVariableListener listener) {
		_appDev.removeVariableListener(listener);
	}

	@Override
	public boolean hasStateVariable(String varName) {
		return _appDev.hasStateVariable(varName);
	}

}
