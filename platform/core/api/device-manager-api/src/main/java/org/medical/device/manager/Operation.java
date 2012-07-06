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

import org.medical.common.StateVariable;

/**
 * Represents an operation which can be invoked from a service.
 * 
 * @author Thomas Leveque
 *
 */
public interface Operation {
	
	/**
	 * Returns the name of this operation.
	 * 
	 * @return the name of this operation.
	 */
	public String getName();

	/**
	 * Returns the service which defines this operation.
	 * 
	 * @return the service which defines this operation.
	 */
	public Service getService();
	
	/**
	 * Invoke this operation with specified input arguments.
	 * In case of multiple output paremters, first output 
	 * parameter (returned by getParameters method) will be returned.
	 * 
	 * @param args input arguments
	 */
	public Object invoke(Object... args);
	
	/**
	 * Invoke this operation with specified arguments.
	 * Parameter order may not be important.
	 * 
	 * @param args arguments
	 */
	public void invoke(OperationParameter... args);
	
	/**
	 * Returns list of operation parameters.
	 * 
	 * @return list of operation parameters.
	 */
	public List<OperationParameter> getParameters(); 
}
