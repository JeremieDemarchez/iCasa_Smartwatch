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
import java.util.List;
import java.util.Map;

import org.medical.common.Attributable;
import org.medical.common.StateVariable;
import org.medical.common.StateVariableListener;
import org.medical.common.VariableType;
import org.medical.common.impl.ComparisonUtil;

public class DeriveIfPossibleStateVar implements StateVariable {

	private Attributable _delegateObj;
	
	protected final StateVariable _originalVar;
	
	protected Object _lockDelegate = new Object();

	private StateVariable _delegateVar;
	
	protected List<StateVariableListener> _listeners = new ArrayList<StateVariableListener>();
	
	protected StateVariableListener _originalVarListener = new StateVariableListener() {

		@Override
		public void addVariable(StateVariable variable, Object sourceObject) {
			// do nothing
		}

		@Override
		public void removeVariable(StateVariable variable,
				Object sourceObject) {
			_originalVar.removeListener(this);
		}

		@Override
		public void notifValueChange(StateVariable variable,
				Object oldValue, Object newValue, Object sourceObject) {
			synchronized (this) {
				setDelegateValue(newValue);
				notifyValueChange(oldValue, newValue);
			}
		}
	};
	
	protected StateVariableListener _delegateVarListener = new StateVariableListener() {

		@Override
		public void addVariable(StateVariable variable, Object sourceObject) {
			updateDelegateVar(variable);
		}

		@Override
		public void removeVariable(StateVariable variable, Object sourceObject) {
			updateDelegateVar(null);
		}

		@Override
		public void notifValueChange(StateVariable variable, Object oldValue, Object newValue,
				Object sourceObject) {
			setOriginalValue(newValue);
		}
	};

	public DeriveIfPossibleStateVar(StateVariable originalVar, Attributable delegateObj) {
		_originalVar = originalVar;
		_originalVar.addListener(_originalVarListener);
		setDelegateObj(delegateObj);
	}

	@Override
	public Object getValue() {
		Object value;
		synchronized (_lockDelegate) {
			if (_delegateVar == null)
				return _originalVar.getValue();

			value = _delegateVar.getValue();
		}
		
//		setOriginalValue(value); //TODO check that it is not needed
		
		return value;
	}
	
	@Override
	public boolean hasValue() {
		return getValue() != null;
	}
	
	protected void setValueInternal(Object value) {
		setOriginalValue(value);
		setDelegateValue(value);
	}

	protected void setDelegateValue(Object value) {
		synchronized (_lockDelegate) {
			if (_delegateVar != null)
				_delegateVar.setValue(value);
		}
	}

	protected void setOriginalValue(Object value) {
		if (_originalVar != null)
			_originalVar.setValue(value);
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
					_delegateVar.addListener(_delegateVarListener);
			} else {
				if (_delegateVar != null)
					_delegateVar.removeListener(_delegateVarListener);
				_delegateVar = null;
			}
		}
	}

	@Override
	public void addListener(StateVariableListener listener) {
		synchronized (_listeners ) {
			_listeners.add(listener);
		}
	}

	@Override
	public void removeListener(StateVariableListener listener) {
		synchronized (_listeners ) {
			_listeners.remove(listener);
		}
	}
	
	protected StateVariable getDelegateVar() {
		synchronized(_lockDelegate) {
			return _delegateVar;
		}
	}
	
	protected void updateDelegateVar(StateVariable newVar) {
		synchronized (_lockDelegate) {
			if (_delegateVar != null)
				_delegateVar.removeListener(_delegateVarListener);
			
			_delegateVar = newVar;
			if (newVar != null) {
				_delegateVar.addListener(_delegateVarListener);
			}
		}
	}

	@Override
	public String getName() {
		return _originalVar.getName();
	}

	@Override
	public boolean canSendNotifications() {
		return _originalVar.canSendNotifications();
	}

	@Override
	public boolean canBeModified() {
		return _originalVar.canBeModified();
	}

	@Override
	public String getDescription() {
		return _originalVar.getDescription();
	}

	@Override
	public VariableType getType() {
		return _originalVar.getType();
	}

	@Override
	public Class getValueType() {
		return _originalVar.getValueType();
	}

	@Override
	public void setValue(Object value) {
		setValueInternal(value);
	}

	@Override
	public Object getOwner() {
		return _originalVar.getOwner();
	}

	@Override
	public boolean hasMetadata(String name) {
		return _originalVar.hasMetadata(name);
	}

	@Override
	public Object getMetadataValue(String name) {
		return _originalVar.getMetadataValue(name);
	}

	@Override
	public void setMetadataValue(String name, Object value) {
		_originalVar.setMetadataValue(name, value);
	}

	@Override
	public Map<String, Object> getMetadataValues() {
		return _originalVar.getMetadataValues();
	}
	
	protected void notifyValueChange(Object oldValue, Object newValue) {
		if (ComparisonUtil.same(oldValue, _originalVar.getValue()))
			return;
		
		synchronized (_listeners ) {
			for (StateVariableListener listener  : _listeners) {
				listener.notifValueChange(this, oldValue, newValue, _originalVar.getOwner());
			}
		}
	}
}
