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

import org.medical.common.Attributable;
import org.medical.common.Identifiable;

/**
 * Describes a home device.
 * 
 * @author Thomas Leveque
 *
 */
public interface Device extends Attributable, Identifiable {

	/**
	 * Returns the human readable service name.
	 * This name should not be more than 80 characters.
	 * This value must be set as an attribute value.
	 * 
	 * @return the human readable service name.
	 */
	public String getName();
	
	/**
	 * Returns the device type identifier.
	 * It represents a device specification identifier.
	 * This device type may define implicitly several service specifications.
	 * 
	 * @return the service type identifier.
	 */
	public String getTypeId();
	
	/**
	 * Returns the vendor of this service.
	 * This value must be set as an attribute value.
	 * 
	 * @return the vendor of this service.
	 */
	public String getVendor();
	
	/**
	 * Returns true if this device has currently at least one fault.
	 * 
	 * @return true if this device has currently at least one fault.
	 */
	public boolean hasFault();
	
	/**
	 * Returns the activation state of this device.
	 * 
	 * @return the fault state of this device.
	 */
	public Fault getGlobalFault();
	
	/**
	 * Returns all detected faults of this device.
	 * 
	 * @return all detected faults of this device.
	 */
	public List<DetailedFault> getDetailedFaults();
	
	/**
	 * Returns true if this device is available.
	 * 
	 * @return true if this device is available.
	 */
	public boolean isAvailable();
	
	/**
	 * Returns true if this device is currently dedicated to one application.
	 * 
	 * @return true if this device is currently dedicated to one application.
	 */
	public boolean hasExclusiveAccess();
	
	/**
	 * Returns list of provided services.
	 * This list may change. 
	 * If there is no service, MUST return an empty list.
	 * 
	 * @return list of provided services.
	 */
	public List<Service> getServices();

	/**
	 * Returns true if specified service type is provided by this device.
	 * 
	 * @param spec a service specification
	 * @return true if specified service type is provided by this device.
	 */
	public boolean hasServiceType(String spec);

	/**
	 * Returns the service of specified type.
	 * If it does not exist, returns null.
	 * 
	 * @param servicetype a service type
	 * @return the service of specified type.
	 */
	public Service getService(String servicetype);
}
