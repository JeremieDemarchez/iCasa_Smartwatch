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
import org.medical.device.manager.impl.util.AppendFaultIfPossibleStateVar;
import org.medical.device.manager.util.AbstractDevice;

/**
 * Implementation of known device.
 * 
 * @author Thomas Leveque
 *
 */
public class KnownDeviceImpl extends AbstractDevice implements KnownDevice, StateVariableListener {

	private static final String LIFECYCLE_METADATA = "lifecycle";

	private AvailableDeviceImpl _device;
	
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
		changeVariableImplem(new AvailabilityStateVar(originalVar, _device));
	}
	
	private void replaceByDelegateAppendVar(String varName) {
		StateVariable originalVar = getInternalVariable(varName);
		changeVariableImplem(new AppendFaultIfPossibleStateVar(originalVar, _device));
	}

	private void replaceByDelegateVar(String varName) {
		StateVariable originalVar = getInternalVariable(varName);
		changeVariableImplem(new DeriveIfPossibleStateVar(originalVar, _device));
	}

	@Override
	public AvailableDevice getAvailableDevice() {
		return _device;
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
		if (_device == null)
			return false;
		
		return  _device.hasExclusiveAccess();//TODO
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
		synchronized (_lockStructChanges) {
			_device.removeVariableListener(this);
			_device = null;

			for (StateVariable var : getInternalStateVariables()) {
				if (!(var instanceof DeriveIfPossibleStateVar))
					continue;

				((DeriveIfPossibleStateVar) var).setDelegateObj(null);
			}
		}
	}

	public void addAvailableDevice(AvailableDeviceImpl device) {
		_device = device;
		device.setKnownDevice(this);
		
		device.addVariableListener(this);
		synchronizeWithAvailable();
		for (StateVariable var : getInternalStateVariables()) {
			if (!(var instanceof DeriveIfPossibleStateVar))
				continue;

			((DeriveIfPossibleStateVar) var).setDelegateObj(_device);
		}
	}

	private void synchronizeWithAvailable() {
		synchronized (_lockStructChanges) {
			mergeVars(_device, this);
			for (Service delegateService : _device.getServices()) {
				Service service = getService(delegateService.getTypeId());
				if (service == null) {
					service = new KnownDeviceServiceImpl(
							delegateService.getId(), this);
					addService(service);
				}
				mergeVars(delegateService, service);
				mergeOps(delegateService, service);
			}
		}
	}
	
	private void mergeOps(Service delegateService, Service service) {
		for (Operation delegateOp : delegateService.getOperations()) {
			List<OperationParameter> delegateParams = delegateOp.getParameters();
			Class[] paramTypes = new Class[delegateParams.size()];
			for (int i = 0; i < delegateParams.size(); i++) {
				OperationParameter delegateParam = delegateParams.get(i);
				paramTypes[i] = delegateParam.getValueType();
			}
			Operation op = service.getOperation(delegateOp.getName(), paramTypes);
			if (op == null) {
				op = new KnownDeviceServOpImpl(delegateOp, service, _device);
				((KnownDeviceServiceImpl) service).addOp(op);
			} else {
				((KnownDeviceServOpImpl) service).setDelegateDevice(_device);
			}
		}
	}

	private void mergeVars(Attributable delegateAttributable, Attributable originalAttributable) {
		
		// add and update variables provided by available device
		final List<StateVariable> deviceVars = delegateAttributable.getStateVariables();
		for (StateVariable delegateVar : deviceVars) {
			StateVariable varProxy = originalAttributable.getStateVariable(delegateVar.getName());
			StateVariable var = null;
			if (varProxy != null)
				var = ((StateVariableProxy) varProxy).getInternalVariable();
			if (var != null) {
				if (!(var instanceof DeriveIfPossibleStateVar))
					continue;
				
				((DeriveIfPossibleStateVar) var).setDelegateObj(delegateAttributable);
				continue;
			}
			
			StateVariable newVar = new StateVariableImpl(delegateVar);
			StateVariable derivVar = new DeriveIfPossibleStateVar(newVar, delegateAttributable);
			derivVar.setMetadataValue(LIFECYCLE_METADATA, VariableLifeCycle.AVAILABLE_SYNC);
			if (originalAttributable == this)
				addStateVariable(derivVar);
			else
				((KnownDeviceServiceImpl) originalAttributable).addVar(derivVar);
		}
		
		// remove variables which came from available device and are no more provided
		Set<String> propsToRem = new HashSet<String>();
		for (StateVariable var : originalAttributable.getStateVariables()) {
			if (!VariableLifeCycle.AVAILABLE_SYNC.equals(var.getMetadataValue(LIFECYCLE_METADATA)))
				continue;
			
			String varName = var.getName();
			if (!delegateAttributable.hasStateVariable(varName))
				propsToRem.add(varName);
		}
		for (String varName : propsToRem) {
			if (originalAttributable == this)
				removeStateVariable(varName);
			else
				((KnownDeviceServiceImpl) originalAttributable).removeVar(varName);
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

	@Override
	public void addVariable(StateVariable variable, Object sourceObject) {
		synchronizeWithAvailable();
	}

	@Override
	public void removeVariable(StateVariable variable, Object sourceObject) {
		synchronizeWithAvailable();
	}

	@Override
	public void notifValueChange(StateVariable variable, Object oldValue,
			Object sourceObject) {
		// do nothing
	}
	
}
