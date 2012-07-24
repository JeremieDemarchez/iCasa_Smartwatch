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
import java.util.concurrent.CopyOnWriteArrayList;

import org.medical.common.StateVariable;
import org.medical.common.StateVariableListener;
import org.medical.common.VariableType;
import org.medical.common.impl.EntityImpl;
import org.medical.common.impl.StateVariableImpl;
import org.medical.device.manager.DetailedFault;
import org.medical.device.manager.Device;
import org.medical.device.manager.Fault;
import org.medical.device.manager.Service;

/**
 * Partial implementation of a device description.
 * 
 * @author Thomas Leveque
 *
 */
public abstract class AbstractDevice extends EntityImpl implements Device {
	
	public static final String VENDOR_PROP_NAME = "#Vendor";

	public static final String NAME_PROP_NAME = "#Name";
	
	public static final String FAULTS_PROP_NAME = "#Faults";
	
	public static final String TYPE_PROP_NAME = "#TypeId";

	public static final String ACTIVATION_STATE_PROP_NAME = "#ActivationState";
	
	public static final String AVAILABLE_PROP_NAME = "#Available";
	
	private final List<Service> _services = new ArrayList<Service>();
	
	private final List<DetailedFault> _faults = new ArrayList<DetailedFault>();
	
	public AbstractDevice(String id, String name, String vendor) {
		this(id, name, vendor, null);
	}

	public AbstractDevice(String id, String name, String vendor, String typeId) {
		super(id);
		
		((StateVariableImpl) getInternalVariable(ID_PROP_NAME)).setDescription("Id");
		addStateVariable(new StateVariableImpl(NAME_PROP_NAME, name, String.class, VariableType.HUMAN_READABLE_DESCRIPTION, "Name", true, true, this));
		addStateVariable(new StateVariableImpl(VENDOR_PROP_NAME, vendor, String.class, VariableType.HUMAN_READABLE_DESCRIPTION, "Vendor", true, true, this));
		addStateVariable(new ManagedStateVariableImpl(FAULTS_PROP_NAME, _faults, _faults.getClass(), VariableType.STATE, "Faults", false, true, this));
		addStateVariable(new StateVariableImpl(TYPE_PROP_NAME, typeId, String.class, VariableType.FORMAL_DESCRIPTION, "Type", false, true, this));
		addStateVariable(new StateVariableImpl(AVAILABLE_PROP_NAME, true, Boolean.class, VariableType.STATE, "Type", false, true, this));
		
		customizeVariables();
	}

	protected void customizeVariables() {
		// do nothing
	}

	@Override
	public String getName() {
		return (String) getVariableValue(NAME_PROP_NAME);
	}

	@Override
	public String getVendor() {
		return (String) getVariableValue(VENDOR_PROP_NAME);
	}

	@Override
	public boolean hasFault() {
		return getGlobalFault().equals(Fault.YES);
	}

	@Override
	public Fault getGlobalFault() {
		boolean unknown = false;
		for (DetailedFault fault : getDetailedFaults()) {
			if (Fault.YES.equals(fault.getFault()))
				return Fault.YES;
			
			if (Fault.UNKNOWN.equals(fault.getFault()))
				unknown = true;
		}
		
		if (unknown)
			return Fault.UNKNOWN;
			
		return Fault.NO;
	}

	@Override
	public List<DetailedFault> getDetailedFaults() {
		return Collections.unmodifiableList((List<DetailedFault>) getVariableValue(FAULTS_PROP_NAME));
	}
	
	/**
	 * Add specified fault to the fault list.
	 * 
	 * @param fault a device fault
	 */
	protected void addFault(DetailedFault fault) {
		List<DetailedFault> oldFaults = null;
		synchronized(_faults) {
			oldFaults = FaultUtil.clone(getDetailedFaults());
			_faults.add(fault);
		}
		notifyFaultListeners(oldFaults);
	}
	
	private void notifyFaultListeners(List<DetailedFault> oldFaults) {
		((ManagedStateVariableImpl) getStateVariable(FAULTS_PROP_NAME)).sendValueChangeNotifs(oldFaults);
	}

	/**
	 * Add specified fault to the fault list and removes all faults related to the same source.
	 * 
	 * @param fault a device fault
	 */
	protected void changeFault(DetailedFault fault) {
		synchronized(_faults) {
			List<Integer> faultIdxs = new ArrayList<Integer>();
			for (int idx = 0; idx < _faults.size(); idx++) {
				DetailedFault curFault = _faults.get(idx);
				if (FaultUtil.same(fault.getSource(), curFault.getSource()))
					faultIdxs.add(faultIdxs.size(), idx);
			}
			
			boolean sameFaultExists = false;
			for (int idx = 0; idx < faultIdxs.size(); idx++) {
				DetailedFault curFault = _faults.get(idx);
				if (FaultUtil.sameFault(fault, curFault))
					sameFaultExists = true;
				else
					_faults.remove(idx);
			}
			
			if (!sameFaultExists)
				_faults.add(fault);
		}
	}

	@Override
	public boolean isAvailable() {
		return (Boolean) getVariableValue(AVAILABLE_PROP_NAME);
	}

	@Override
	public boolean hasExclusiveAccess() {
		return false;
	}

	@Override
	public List<Service> getServices() {
		return _services;
	}

	@Override
	public String getTypeId() {
		return (String) getVariableValue(TYPE_PROP_NAME);
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
		if (!(obj instanceof Device))
			return false;
		
		Device other = (Device) obj;
		
		return other.getId().equals(this.getId());
	}

	@Override
	public boolean hasServiceType(String spec) {
		synchronized (_services) {
			for (Service serv : _services) {
				if (serv.getTypeId().equals(spec))
					return true;
			}
		}
		
		return false;
	}

	@Override
	public Service getService(String spec) {
		synchronized (_services) {
			for (Service serv : _services) {
				if (serv.getTypeId().equals(spec))
					return serv;
			}
		}

		return null;
	}
	
	protected final void addService(Service serv) {
		synchronized (_services) {
			for (Service existingServ : _services) {
				if (existingServ.getTypeId().equals(serv.getTypeId()))
					throw new IllegalArgumentException("A service with same type id already exists");
			}
			
			synchronized (_lockStructChanges) {
				if (serv instanceof AbstractService) {
					try {
						((AbstractService) serv).setStructureChangeLock(_lockStructChanges);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				
				_services.add(serv);
			}
		}
	}
	
	protected final void removeService(Service serv) {
		synchronized (_services) {
			synchronized (_lockStructChanges) {
				_services.remove(serv);
			}
		}
	}
	
	@Override
	public void addVariableListener(StateVariableListener listener) {
		synchronized(_listeners) {
			_listeners.add(listener);
			for (StateVariable var : getStateVariables()) {
				var.addListener(listener);
			}
			for (Service service : getServices())
				service.addVariableListener(listener);
		}
	}

	@Override
	public void removeVariableListener(StateVariableListener listener) {
		synchronized(_listeners) {
			for (Service service : getServices())
				service.removeVariableListener(listener);
			_listeners.remove(listener);
		}
	}
	
}
