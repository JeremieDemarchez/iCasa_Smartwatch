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
package org.medical.common;

import java.util.List;
import java.util.Set;

/**
 * Allows an object to define attributes with their corresponding values.
 * Property value can be null.
 * 
 * @author Thomas leveque
 *
 */
public interface Attributable {

	/**
	 * Returns a set containing all property names.
	 * 
	 * @return a set containing all property names.
	 */
	public Set<String> getPropertyNames();
	
	/**
	 * Returns value of specified property.
	 * Returns null if there is no specified property
	 * 
	 * @param propertyName name of considered property
	 * @return value of specified property.
	 */
	public Object getPropertyValue(String propertyName);
	
	/**
	 * Returns variable with specified name.
	 * Returns null if there is no such variable
	 * 
	 * @param propertyName name of considered variable
	 * @return variable with specified name.
	 */
	public StateVariable getStateVariable(String propertyName);
	
	/**
	 * Sets value of specified property.
	 * 
	 * @param propertyName name of considered property
	 * @param value property value to set
	 */
	public void setPropertyValue(String propertyName, Object value);
	
	/**
	 * Returns list of state variables which represent all properties.
	 * 
	 * @return list of state variables which represent all properties.
	 */
	public List<StateVariable> getStateVariables();
	
	/**
	 * Adds a variable extender.
	 * 
	 * @param extender a variable extender.
	 */
	public void addVariableExtender(StateVariableExtender extender);
	
	/**
	 * Removes a variable extender.
	 * 
	 * @param extender a variable extender.
	 */
	public void removeVariableExtender(StateVariableExtender extender);
	
	/**
	 * Returns all associated variable extenders.
	 * 
	 * @return all associated variable extenders.
	 */
	public List<StateVariableExtender> getVariableExtenders();
	
	/**
	 * Add a listener which listens of value changes and variable addition and removal.
	 * 
	 * @param listener a listener
	 */
	public void addVariableListener(StateVariableListener listener);
	
	/**
	 * Removes specified listener.
	 * 
	 * @param listener a listener
	 */
	public void removeVariableListener(StateVariableListener listener);
}
