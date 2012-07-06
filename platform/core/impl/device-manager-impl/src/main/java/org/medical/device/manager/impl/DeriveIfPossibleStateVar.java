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

import org.medical.common.Attributable;
import org.medical.common.StateVariable;
import org.medical.common.StateVariableListener;
import org.medical.common.impl.StateVariableImpl;
import org.medical.device.manager.Device;

public class DeriveIfPossibleStateVar extends StateVariableImpl {

	private Attributable _delegateObj;
	
	private StateVariable _originalVar;
	
	protected Object _lockDelegate = new Object();

	public DeriveIfPossibleStateVar(StateVariable originalVar, Attributable delegateObj) {
		super(originalVar);
		_delegateObj = delegateObj;
		_originalVar = originalVar;
	}

	@Override
	public Object getValue() {
		Object value;
		synchronized (_lockDelegate) {
			if (_delegateObj == null) {
				return super.getValue();
			}

			StateVariable delegateVar = _delegateObj
					.getStateVariable(getName());
			if (delegateVar == null)
				return super.getValue();

			value = delegateVar.getValue();
		}
		
		if (_originalVar != null)
			_originalVar.setValue(value);
		
		return value;
	}
	
	@Override
	public boolean hasValue() {
		return getValue() != null;
	}
	
	@Override
	protected void setValueInternal(Object value) {
		if (_originalVar != null)
			_originalVar.setValue(value);
		
		StateVariable delegateVar;
		synchronized (_lockDelegate) {
			if (_delegateObj == null)
				return;

			delegateVar = _delegateObj.getStateVariable(getName());
		}
		if (delegateVar != null)
			delegateVar.setValue(value);
	}
	
	public final Attributable getDelegateObj() {
		return _delegateObj;
	}

	public void setDelegateObj(Attributable delegateObj) {
		synchronized (_lockDelegate) {
			_delegateObj = delegateObj;
		}
	}

	@Override
	public void addValueChangeListener(StateVariableListener listener) {
		synchronized (_listeners ) {
			_listeners.add(listener);
		}
	}

	@Override
	public void removeValueChangeListener(StateVariableListener listener) {
		synchronized (_listeners ) {
			_listeners.remove(listener);
		}
	}
}
