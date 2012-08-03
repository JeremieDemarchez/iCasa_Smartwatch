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

import org.medical.common.VariableType;
import org.medical.device.manager.Device;
import org.medical.device.manager.util.AbstractService;



/**
 * Implementation of a device service which represents and delegates a OSGi service.
 * 
 * @author Thomas Leveque
 *
 */
public class ServiceFromIntf extends AbstractService {

	public ServiceFromIntf(Class interf, Device device, Object originalObj) {
		super(interf.getName(), device);
		
		List<Method> reifiedMethods = new ArrayList<Method>();
		for (Method method : interf.getMethods()) {
			if (reifiedMethods.contains(method))
				continue;
			
			Method setterMethod = null;
			Method getterMethod = null;
			String attrName = null;
			if (ReflectUtil.isGetterMethod(method)) {
				attrName = ReflectUtil.getAttrName(method);
				
				getterMethod = method;
				setterMethod = ReflectUtil.getSetterMethod(attrName, ReflectUtil.getVariableTypeFrom(getterMethod, setterMethod), interf);
				
				reifiedMethods.add(getterMethod);
			} else if (ReflectUtil.isSetterMethod(method)) {
				attrName = ReflectUtil.getAttrName(method);
				
				setterMethod = method;
				getterMethod = ReflectUtil.getGetterMethod(attrName, ReflectUtil.getVariableTypeFrom(getterMethod, setterMethod), interf);
				
				reifiedMethods.add(setterMethod);
			}
			if ((getterMethod != null) || (setterMethod != null)) {
				DerivedStateVariableFromIntf var = (DerivedStateVariableFromIntf) getInternalVariable(attrName);
				if (var != null)
					continue; // ignore it, case of multiple attribute with different types
				
				var = new DerivedStateVariableFromIntf(attrName, originalObj, getterMethod, setterMethod, VariableType.UNKNOWN, "", this);
				addStateVariable(var);
				
				continue;
			}
			
			// case of operation
			DerivedOperationFromIntf op = new DerivedOperationFromIntf(method, originalObj, this);
			addOperation(op);
		}
	}

}
