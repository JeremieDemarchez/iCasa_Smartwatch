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
package fr.liglab.adele.device.model.bridge;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.medical.common.StateVariable;
import org.medical.common.StateVariableListener;
import org.medical.common.VariableType;
import org.medical.device.manager.DetailedFault;
import org.medical.device.manager.Fault;
import org.medical.device.manager.Service;
import org.medical.device.manager.util.AbstractProvidedDevice;
import org.osgi.framework.BundleContext;

import fr.liglab.adele.icasa.device.GenericDevice;

public class ProvidedDeviceFromIntf extends AbstractProvidedDevice implements StateVariableListener {
	
	public static final String LOCATION_ATTR_NAME = "Location";
	
	public static final String FAULT_ATTR_NAME = "Fault";
	
	public static final String STATE_ATTR_NAME = "State";

	public static final String LOCATION_PROP_NAME = LOCATION_ATTR_NAME;
	
	private GenericDevice _device;
	
	private BundleContext _context;

	public ProvidedDeviceFromIntf(GenericDevice device, BundleContext context) {
		super(device.getSerialNumber(), device.getClass().getCanonicalName().replaceAll("Impl", ""), ReflectUtil.getVendorFromPackage(device));
		_device = device;
		_context = context;
		
		Class devClass = _device.getClass();
		
		addPredefinedAttrs(GenericDevice.class);
		
		//add one service per implemented interface
		for (Class interf : getAllExportedInterfaces(devClass)) {
			// predefined attributes from generic device are added to the created device instead of being defined in a specific service
			if (GenericDevice.class.equals(interf))
				continue; //already done
			
			addServiceFrom(interf);
		}
		addVariableListener(this);
		updateFaultState();
	}

	private List<Class> getAllExportedInterfaces(Class devClass) {
		List<Class> exportedIntfs = new ArrayList<Class>();
		
		Class curClass = devClass;
		while (curClass != null) {
			for (Class interf : curClass.getInterfaces()) {
				try {
					_context.getBundle().loadClass(interf.getName());
					exportedIntfs.add(interf);
				} catch (ClassNotFoundException e) {
					// do nothing
				}
			}
			
			curClass = curClass.getSuperclass();
		}
		
		return exportedIntfs;
	}

	private void addPredefinedAttrs(Class interf) {
		addAttrFrom(interf, FAULT_ATTR_NAME, String.class);
		addAttrFrom(interf, LOCATION_ATTR_NAME, String.class);
		addAttrFrom(interf, STATE_ATTR_NAME, String.class);
	}

	private void addAttrFrom(Class interf, String attrName, Class<String> attrClass) {
		Method getterMethod = ReflectUtil.getGetterMethod(attrName, attrClass, interf);
		Method setterMethod = ReflectUtil.getSetterMethod(attrName, attrClass, interf);
		addStateVariable(new DerivedStateVariableFromIntf(attrName, _device, getterMethod, setterMethod, VariableType.STATE, attrName, this));
	}

	private void addServiceFrom(Class interf) {
		Service serv = new ServiceFromIntf(interf, this, _device);
		addService(serv);
	}

	@Override
	public void addVariable(StateVariable variable, Object sourceObject) {
		if (variable.getName().equals(FAULT_ATTR_NAME))
			updateFaultState();
	}
	
	private void updateFaultState() {
		String deviceFault = (String) getVariableValue(FAULT_ATTR_NAME);
		boolean hasFault = (deviceFault != null) && (deviceFault.equalsIgnoreCase("yes"));
		Fault fault = hasFault ? Fault.YES : Fault.NO ;
		changeFault(new DetailedFault(fault, "Unknown", this.getClass().getName() + ":" + _device.getSerialNumber()));
	}

	@Override
	public void removeVariable(StateVariable variable, Object sourceObject) {
		if (variable.getName().equals(FAULT_ATTR_NAME))
			updateFaultState();
	}

	@Override
	public void notifValueChange(StateVariable variable, Object oldValue, Object newValue,
			Object sourceObject) {
		if (variable.getName().equals(FAULT_ATTR_NAME))
			updateFaultState();
	}

}
