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
package org.medical.device.manager;

import java.util.List;
import java.util.Set;

import org.medical.common.Attributable;
import org.medical.common.Identifiable;
import org.medical.common.StateVariable;
import org.medical.common.StateVariableExtender;

/**
 * Represents a functionality of a resource.
 * Service identifier is not unique ; it represents a service specification identifier.
 * 
 * @author Thomas Leveque
 *
 */
public interface Service extends Attributable, Identifiable {

	/**
	 * Returns the service type identifier.
	 * It represents a service specification identifier.
	 * 
	 * @return the service type identifier.
	 */
	public String getTypeId();
	
	/**
	 * Returns device which provides this service.
	 * 
	 * @return device which provides this service.
	 */
	public Device getDevice();
	
	/**
	 * Returns a set containing all operations.
	 * 
	 * @return a set containing all operations.
	 */
	public List<Operation> getOperations();

	/**
	 * Returns operation provided by this service with specfied name and signature.
	 * 
	 * @param name
	 * @param parameterTypes types of parameters
	 * @return operation provided by this service with specified name and signature.
	 */
	public Operation getOperation(String name, Class... parameterTypes);
	
}
