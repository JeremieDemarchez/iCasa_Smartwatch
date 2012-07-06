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
package org.medical.common.impl;

import java.util.ArrayList;
import java.util.List;

import org.medical.common.StateVariable;
import org.medical.common.StateVariableListener;
import org.medical.common.VariableType;

public class StateVariableImpl implements StateVariable {

	protected Object _value;
	private Class _type;
	private VariableType _varType;
	private boolean _canBeModified;
	private boolean _canSendNotif;
	private String _name;
	protected List<StateVariableListener> _listeners = new ArrayList<StateVariableListener>();
	private String _description;
	private Object _owner;
	
	public StateVariableImpl(String name, Object value, Class type, VariableType varType,
			String description, boolean canBeModified, boolean canSendNotif, Object owner) {
		this._value = value;
		this._type = type;
		this._varType = varType;
		this._canBeModified = canBeModified;
		this._canSendNotif = canSendNotif;
		this._name = name;
		this._description = description;
		_owner = owner;
	}

	public StateVariableImpl(StateVariable delegateVar) {
		this(delegateVar.getName(), delegateVar.getValue(), delegateVar //TODO should clone value
				.getValueType(), delegateVar.getType(), delegateVar
				.getDescription(), delegateVar.canBeModified(), delegateVar
				.canSendNotifications(), delegateVar.getOwner());
	}

	@Override
	public String getName() {
		return _name;
	}

	@Override
	public boolean canSendNotifications() {
		return _canSendNotif;
	}

	@Override
	public boolean canBeModified() {
		return _canBeModified;
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
	public Object getValue() {
		return _value;
	}
	
	@Override
	public boolean hasValue() {
		return _value != null;
	}

	@Override
	public String getDescription() {
		return _description;
	}

	@Override
	public VariableType getType() {
		return _varType;
	}

	@Override
	public Class getValueType() {
		return _type;
	}

	@Override
	public final void setValue(Object value) {
		Object oldValue = _value;
		setValueInternal(value);
		_value = value;

		notifyValueChange(oldValue);
	}

	protected void notifyValueChange(Object oldValue) {
		if (same(oldValue, _value))
			return;
		
		synchronized (_listeners ) {
			for (StateVariableListener listener  : _listeners) {
				listener.notifValueChange(this, oldValue, _owner);
			}
		}
	}
	
	protected boolean same(Object oldValue, Object newValue) {
		return ((oldValue == null) && (newValue == null)) || ((oldValue != null) && oldValue.equals(newValue));
	}

	protected void setValueInternal(Object value) {
		// do nothing
	}

	public void setDescription(String description) {
		_description = description;
	}

	@Override
	public Object getOwner() {
		return _owner;
	}

}
