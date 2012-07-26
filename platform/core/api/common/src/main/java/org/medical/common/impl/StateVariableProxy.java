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
import java.util.Map;

import org.medical.common.StateVariable;
import org.medical.common.StateVariableListener;
import org.medical.common.VariableType;

/**
 * Proxy of variable which allows to change dynamically its implementation while keeping the same listeners.
 * 
 * @author Thomas Leveque
 *
 */
public class StateVariableProxy implements StateVariable, StateVariableListener {
	
	private StateVariable _var;
	
	private String _varName;
	
	protected List<StateVariableListener> _listeners = new ArrayList<StateVariableListener>();

	public StateVariable getInternalVariable() {
		return _var;
	}

	public synchronized void setInternalVariable(StateVariable var) {
		if (_var != null) {
			_var.removeListener(this);
		}
		_var = var;
		if (var != null)
			var.addListener(this);
	}

	public StateVariableProxy(StateVariable var) {
		_varName = var.getName();
		setInternalVariable(var);
	}

	@Override
	public synchronized String getName() {
		return _varName;
	}

	@Override
	public synchronized boolean canSendNotifications() {
		return _var.canSendNotifications();
	}

	@Override
	public synchronized boolean canBeModified() {
		return _var.canBeModified();
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

	@Override
	public synchronized Object getValue() {
		return _var.getValue();
	}

	@Override
	public synchronized String getDescription() {
		return _var.getDescription();
	}

	@Override
	public synchronized VariableType getType() {
		return _var.getType();
	}

	@Override
	public synchronized Class getValueType() {
		return _var.getValueType();
	}

	@Override
	public synchronized void setValue(Object value) {
		_var.setValue(value);
	}

	@Override
	public synchronized boolean hasValue() {
		return _var.hasValue();
	}

	@Override
	public synchronized Object getOwner() {
		return _var.getOwner();
	}

	@Override
	public void addVariable(StateVariable variable, Object sourceObject) {
		synchronized (_listeners ) {
			for (StateVariableListener listener  : _listeners) {
				listener.addVariable(this, sourceObject);
			}
		}
	}

	@Override
	public void removeVariable(StateVariable variable, Object sourceObject) {
		synchronized (_listeners ) {
			for (StateVariableListener listener  : _listeners) {
				listener.removeVariable(this, sourceObject);
			}
		}
	}

	@Override
	public void notifValueChange(StateVariable variable, Object oldValue, Object newValue,
			Object sourceObject) {
		synchronized (_listeners ) {
			for (StateVariableListener listener  : _listeners) {
				listener.notifValueChange(this, oldValue, newValue, sourceObject);
			}
		}
	}

	@Override
	public boolean hasMetadata(String name) {
		return _var.hasMetadata(name);
	}

	@Override
	public Object getMetadataValue(String name) {
		return _var.getMetadataValue(name);
	}

	@Override
	public void setMetadataValue(String name, Object value) {
		_var.setMetadataValue(name, value);
	}

	@Override
	public Map<String, Object> getMetadataValues() {
		return _var.getMetadataValues();
	}

}
