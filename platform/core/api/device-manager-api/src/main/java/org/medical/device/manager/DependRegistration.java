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


/**
 * Represents a specification of a device dependency which has been registered to the device manager.
 * TODO
 * 
 * @author Thomas Leveque
 *
 */
public interface DependRegistration {
	
	/**
	 * Returns corresponding device dependencies.
	 * 
	 * @return corresponding device dependencies.
	 */
	public DeviceDependencies getDependencies();
	
	/**
	 * Returns true if the dependency is fully resolved.
	 * 
	 * @return true if the dependency is fully resolved.
	 */
	public boolean isResolved();
	
	/**
	 * Returns all resolved devices.
	 * 
	 * @return all resolved devices.
	 */
	public List<ApplicationDevice> getResolvedDevices();
	
	/**
	 * Returns true if this dependency is registered.
	 * 
	 * @return true if this dependency is registered.
	 */
	public boolean isRegistered();

	/**
	 * Unregister this dependency specification.
	 */
	public void unregister();
	
	/**
	 * Updates the registered dependency specification.
	 */
	public void updates();
	
	/**
	 * Returns a unique identifier of this dependency registration.
	 * 
	 * @return a unique identifier of this dependency registration.
	 */
	public String getId();
}
