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
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.medical.common.Attributable;
import org.medical.common.Identifiable;
import org.medical.common.StateVariable;
import org.medical.common.StateVariableExtender;
import org.medical.common.StateVariableListener;
import org.medical.common.VariableType;

/**
 * Represents generic implementation of Identifiable and Attributable interfaces. 
 * Most concepts in device manager represents entities which can be implemented using this class.
 * 
 * @author Thomas Leveque
 *
 */
public class EntityImpl implements Identifiable, Attributable {
	
	public static final String ID_PROP_NAME = "#id";
	
	private Map<String, StateVariable> attributeValues = new ConcurrentHashMap<String, StateVariable>();
	
	private List<StateVariableExtender> _extenders = new ArrayList<StateVariableExtender>();
	
	protected List<StateVariableListener> _listeners = new ArrayList<StateVariableListener>();
	
	protected Object _lockStructChanges = new Object();
	
	public EntityImpl(String id) {
		StateVariable var = new StateVariableImpl(ID_PROP_NAME, id, String.class, VariableType.ID, "Entity identifier", false, true, this);
		addStateVariable(var);
	}

	@Override
	public final Set<String> getPropertyNames() {
		return attributeValues.keySet();
	}

	@Override
	public final Object getPropertyValue(String propertyName) {
		StateVariable stateVar = attributeValues.get(propertyName);
		if (stateVar == null)
			return null;
		
		return stateVar.getValue();
	}
	
	public final StateVariable getStateVariable(String propertyName) {
		StateVariable stateVar = attributeValues.get(propertyName);
		if (stateVar == null)
			return null;
		
		return stateVar;
	}

	@Override
	public final void setPropertyValue(String propertyName, Object value) {
		StateVariable stateVar = attributeValues.get(propertyName);
		if (stateVar == null)
			throw new IllegalArgumentException("Property " + propertyName + " does not exist.");
		
		stateVar.setValue(value);
	}

	@Override
	public final List<StateVariable> getStateVariables() {
		final Collection<StateVariable> stateVars = attributeValues.values();
		return new ArrayList<StateVariable>(stateVars);
	}

	@Override
	public final void addVariableExtender(StateVariableExtender extender) {
		synchronized (_extenders) {
			_extenders.add(extender);
		}
	}

	@Override
	public final void removeVariableExtender(StateVariableExtender extender) {
		synchronized (_extenders) {
			_extenders.remove(extender);
		}
	}

	@Override
	public final List<StateVariableExtender> getVariableExtenders() {
		return Collections.unmodifiableList(_extenders);
	}

	@Override
	public final String getId() {
		return (String) getPropertyValue(ID_PROP_NAME);
	}
	
	protected final void addStateVariable(StateVariable var) {
		synchronized (_lockStructChanges) {
			final String propName = var.getName();
			StateVariable stateVar = attributeValues.get(propName);
			if (stateVar != null)
				throw new IllegalArgumentException("Property " + propName
						+ " already exists.");

			attributeValues.put(propName, var);
		}
		
		synchronized(_listeners) {
			for (StateVariableListener listener : _listeners) {
				listener.addVariable(var, this);
				var.addValueChangeListener(listener);
			}
		}
	}
	
	protected final void removeStateVariable(StateVariable var) {
		synchronized (_lockStructChanges) {
			final String propName = var.getName();
			attributeValues.remove(propName);
		}
		
		synchronized(_listeners) {
			for (StateVariableListener listener : _listeners) {
				var.removeValueChangeListener(listener);
				listener.removeVariable(var, this);
			}
		}
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
		if (!(obj instanceof Identifiable))
			return false;
		
		Identifiable idObj = (Identifiable) obj;
		return getId().equals(idObj.getId());
	}

	@Override
	public void addVariableListener(StateVariableListener listener) {
		synchronized(_listeners) {
			_listeners.add(listener);
			for (StateVariable var : getStateVariables()) {
				var.addValueChangeListener(listener);
			}
		}
	}

	@Override
	public void removeVariableListener(StateVariableListener listener) {
		synchronized(_listeners) {
			_listeners.remove(listener);
			for (StateVariable var : getStateVariables()) {
				var.removeValueChangeListener(listener);
			}
		}
	}

	
}
