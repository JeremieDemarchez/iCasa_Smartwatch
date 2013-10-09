/**
 *
 *   Copyright 2011-2012 Universite Joseph Fourier, LIG, ADELE Research
 *   Group Licensed under the Apache License, Version 2.0 (the "License");
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
package fr.liglab.adele.icasa.common.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import fr.liglab.adele.icasa.common.StateVariable;
import fr.liglab.adele.icasa.common.StateVariableListener;
import fr.liglab.adele.icasa.common.VariableType;

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
	private Map<String, Object> _metadata = new ConcurrentHashMap<String, Object>();
	
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

	public StateVariableImpl(StateVariable varToClone) {
		this(varToClone.getName(), clone(varToClone.getValue()), varToClone
				.getValueType(), varToClone.getType(), varToClone
				.getDescription(), varToClone.canBeModified(), varToClone
				.canSendNotifications(), varToClone.getOwner());
	}

	private static Object clone(Object value) {
		return value; //TODO should clone value
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

		notifyValueChange(oldValue, _value);
	}

	protected void notifyValueChange(Object oldValue, Object newValue) {
		if (ComparisonUtil.same(oldValue, _value))
			return;
		
		synchronized (_listeners ) {
			for (StateVariableListener listener  : _listeners) {
				listener.notifValueChange(this, oldValue, newValue, _owner);
			}
		}
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

	@Override
	public boolean hasMetadata(String name) {
		return _metadata.containsKey(name);
	}

	@Override
	public Object getMetadataValue(String name) {
		return _metadata.get(name);
	}

	@Override
	public void setMetadataValue(String name, Object value) {
		_metadata.put(name, value);
	}

	@Override
	public Map<String, Object> getMetadataValues() {
		return Collections.unmodifiableMap(_metadata );
	}

}
