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
package org.medical.device.manager.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.medical.common.StateVariable;
import org.medical.common.impl.EntityImpl;
import org.medical.device.manager.Device;
import org.medical.device.manager.Operation;
import org.medical.device.manager.OperationParameter;
import org.medical.device.manager.Service;

/**
 * Default implementation of a device service.
 * 
 * @author Thomas Leveque
 *
 */
public class AbstractService extends EntityImpl implements Service {

	private Device _device;
	
	private List<Operation> _operations = new ArrayList<Operation>();
	
	private boolean _lockRegistered = false;
	
	public AbstractService(String serviceId, Device device, Object structChangeLock) {
		super(serviceId);
		_device = device;
		setStructureChangeLock(structChangeLock);
	}
	
	public AbstractService(String serviceId, Device device) {
		super(serviceId);
		_device = device;
	}
	
	public void setStructureChangeLock(Object lock) {
		if (_lockRegistered)
			throw new IllegalArgumentException("Lock object has already be set.");
		
		_lockRegistered = true;
		_lockStructChanges = lock;
	}

	@Override
	public Device getDevice() {
		return _device;
	}

	@Override
	public List<Operation> getOperations() {
		return Collections.unmodifiableList(_operations);
	}

	@Override
	public String getTypeId() {
		return getId();
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
		if (!(obj instanceof Service))
			return false;
		
		Service other = (Service) obj;
		
		if (!other.getId().equals(this.getId()))
			return false;
		
		return (other.getDevice().getId().equals(_device.getId()));
	}

	@Override
	public Operation getOperation(String name, Class... parameterTypes) {
		synchronized (_operations) {
			for (Operation op : _operations) {
				if (!op.getName().equals(name))
					continue;
				
				final List<OperationParameter> parameters = op.getParameters();
				boolean conforms = true;
				for (int i = 0; i < parameters.size(); i++) {
					OperationParameter var = parameters.get(i);
					if (!var.getValueType().equals(parameterTypes[i])) {
						conforms = false;
						break;
					}
				}
				if (conforms)
					return op;
			}
		}
		
		return null;
	}
	
	protected final void addOperation(Operation op) {
		synchronized (_operations) {
			synchronized(_lockStructChanges) {
				_operations.add(op);
			}
		}
	}
	
	protected final void removeOperation(Operation op) {
		synchronized (_operations) {
			synchronized(_lockStructChanges) {
				_operations.remove(op);
			}
		}
	}
}
