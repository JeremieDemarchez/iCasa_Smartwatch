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

import org.medical.application.Application;

/**
 * Represents a device which is known by the device manager.
 * 
 * @author Thomas Leveque
 *
 */
public interface KnownDevice extends Device {

	/**
	 * Returns the available device if it exists.
	 * Else returns null.
	 * 
	 * @return the available device if it exists.
	 */
	public AvailableDevice getAvailableDevice();
	
	/**
	 * Returns all devices representing this device which are visible from applications.
	 * Returns an empty list if there is no one.
	 * 
	 * @return all devices representing this device which are visible from applications.
	 */
	public List<ApplicationDevice> getApplicationDevices();
	
	/**
	 * Returns device proxy representing this device which is visible from specified application.
	 * Returns null if does not exist.
	 * 
	 * @return device proxy representing this device which is visible from specified application.
	 */
	public ApplicationDevice getApplicationDevice(Application app);
	
	/**
	 * Returns the application which has exported this device if it exists.
	 * Else returns null.
	 * 
	 * @return the application which has exported this device if it exists.
	 */
	public Application getAppOwner();
}
