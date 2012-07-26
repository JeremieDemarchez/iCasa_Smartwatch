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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.medical.common.Attributable;
import org.medical.common.StateVariable;
import org.medical.common.StateVariableListener;
import org.medical.common.impl.StateVariableImpl;
import org.medical.common.impl.StateVariableProxy;
import org.medical.device.manager.Device;
import org.medical.device.manager.Operation;
import org.medical.device.manager.OperationParameter;
import org.medical.device.manager.Service;
import org.medical.device.manager.VariableLifeCycle;
import org.medical.device.manager.util.AbstractDevice;


public class SynchronizedDevice extends AbstractDevice implements DelegateToDevice {
	
	private Device _device;
	
	protected Object _lockDelegate = new Object();

	private final StateVariableListener _varListener = new StateVariableListener() {
		@Override
		public void addVariable(StateVariable variable, Object sourceObject) {
			resynchronize();
		}

		@Override
		public void removeVariable(StateVariable variable, Object sourceObject) {
			resynchronize();
		}

		@Override
		public void notifValueChange(StateVariable variable, Object oldValue, Object newValue,
				Object sourceObject) {
			// do nothing
		}
	};

	public SynchronizedDevice(Device device) {
		super(device.getId(), device.getName(), device.getVendor(), device.getTypeId());
		setDelegateDevice(device);
	}

	protected void setDelegateDevice(Device device) {
		if (device == null) {
			synchronized (_lockStructChanges) {
				if (_device != null)
					_device.removeVariableListener(_varListener);
				_device = null;

				for (StateVariable var : getInternalStateVariables()) {
					if (!(var instanceof DeriveIfPossibleStateVar))
						continue;

					((DeriveIfPossibleStateVar) var).setDelegateObj(null);
				}
				//TODO remove no more used attribute from delegate 
			}
		} else {
			_device = device;
			synchronizeWith(_device);
		}
	}
	
	public SynchronizedDevice(String id, String name, String vendor, String typeId) {
		super(id, name, vendor, typeId);
	}
	
	public SynchronizedDevice(String id, String name, String vendor) {
		super(id, name, vendor);
	}
	
	public Device getDelegateDevice() {
		return _device;
	}
	
	protected void synchronizeWith(Device device) {
		device.addVariableListener(_varListener );
		resynchronize();
		for (StateVariable var : getInternalStateVariables()) {
			if (!(var instanceof DeriveIfPossibleStateVar))
				continue;

			((DeriveIfPossibleStateVar) var).setDelegateObj(_device);
		}
	}
	
	protected void resynchronize() {
		synchronized (_lockStructChanges) {
			mergeVars(_device, this);
			for (Service delegateService : _device.getServices()) {
				Service service = getService(delegateService.getTypeId());
				if (service == null) {
					service = new DeviceServiceImpl(
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
				((DeviceServiceImpl) service).addOp(op);
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
			derivVar.setMetadataValue(LIFECYCLE_METADATA, VariableLifeCycle.getLifeCycle(_device));
			if (originalAttributable == this)
				addStateVariable(derivVar);
			else
				((DeviceServiceImpl) originalAttributable).addVar(derivVar);
		}
		
		// remove variables which came from available device and are no more provided
		Set<String> propsToRem = new HashSet<String>();
		for (StateVariable var : originalAttributable.getStateVariables()) {
			if (!VariableLifeCycle.getLifeCycle(_device).equals(var.getMetadataValue(LIFECYCLE_METADATA)))
				continue;
			
			String varName = var.getName();
			if (!delegateAttributable.hasStateVariable(varName))
				propsToRem.add(varName);
		}
		for (String varName : propsToRem) {
			if (originalAttributable == this)
				removeStateVariable(varName);
			else
				((DeviceServiceImpl) originalAttributable).removeVar(varName);
		}
	}
}
