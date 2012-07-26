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

import org.medical.common.VariableType;
import org.medical.common.impl.StateVariableImpl;

public class DerivedStateVariableFromIntf extends StateVariableImpl {
	
	private Method _getterMethod;
	
	private Method _setterMethod;

	private Object _object;

	public DerivedStateVariableFromIntf(String name, Object object, Method getterMethod, Method setterMethod, VariableType varType,
			String description, Object owner) {
		super(name, null, ReflectUtil.getVariableTypeFrom(getterMethod, setterMethod), varType, description, (setterMethod != null), true, owner);
		_object = object;
		_getterMethod = getterMethod;
		_setterMethod = setterMethod;
	}

	@Override
	public Object getValue() {
		if (_getterMethod == null)
			return null;
		
		// update value on demand
		Object value = ReflectUtil.callGetter(_getterMethod, _object);
		updateInternalValue(value);
		return value;
	}
	
	@Override
	public boolean hasValue() {
		return getValue() != null;
	}
	
	@Override
	protected void setValueInternal(Object value) {
		if (_setterMethod != null)
			ReflectUtil.callSetter(_setterMethod, value, _object);
		updateInternalValue(value);
	}

	private void updateInternalValue(Object value) {
		Object oldValue = _value;
		_value = value;
		notifyValueChange(oldValue, _value);
	}
}
