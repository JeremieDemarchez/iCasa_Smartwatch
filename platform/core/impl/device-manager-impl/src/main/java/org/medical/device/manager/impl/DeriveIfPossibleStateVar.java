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

public class DeriveIfPossibleStateVar extends StateVariableImpl implements StateVariableListener {

	private Attributable _delegateObj;
	
	protected final StateVariable _originalVar;
	
	protected Object _lockDelegate = new Object();

	private StateVariable _delegateVar;

	public DeriveIfPossibleStateVar(StateVariable originalVar, Attributable delegateObj) {
		super(originalVar);
		_originalVar = originalVar;
		setDelegateObj(delegateObj);
	}

	@Override
	public Object getValue() {
		Object value;
		synchronized (_lockDelegate) {
			if (_delegateVar == null)
				return super.getValue();

			value = _delegateVar.getValue();
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
		
		synchronized (_lockDelegate) {
			if (_delegateVar != null)
				_delegateVar.setValue(value);
		}
	}
	
	public final Attributable getDelegateObj() {
		return _delegateObj;
	}

	public void setDelegateObj(Attributable delegateObj) {
		synchronized (_lockDelegate) {
			_delegateObj = delegateObj;
			
			if (_delegateObj != null) {
				_delegateVar = _delegateObj.getStateVariable(getName());
				if (_delegateVar != null)
					_delegateVar.addValueChangeListener(this);
			} else {
				if (_delegateVar != null)
					_delegateVar.removeValueChangeListener(this);
				_delegateVar = null;
			}
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

	@Override
	public void addVariable(StateVariable variable, Object sourceObject) {
		if (variable.getName().equals(getName()))
			updateDelegateVar(variable);
	}

	protected void updateDelegateVar(StateVariable newVar) {
		synchronized (_lockDelegate) {
			if (_delegateVar != null)
				_delegateVar.removeValueChangeListener(this);
			
			_delegateVar = newVar;
			if (newVar != null) {
				_delegateVar.addValueChangeListener(this);
			}
		}
	}

	@Override
	public void removeVariable(StateVariable variable, Object sourceObject) {
		if (variable.getName().equals(getName()))
			updateDelegateVar(null);
	}

	@Override
	public void notifValueChange(StateVariable variable, Object oldValue,
			Object sourceObject) {
		if (variable.getName().equals(getName())) {
			getValue(); // update the internal value and send notifs
		}
	}
	
	protected StateVariable getDelegateVar() {
		synchronized(_lockDelegate) {
			return _delegateVar;
		}
	}
}
