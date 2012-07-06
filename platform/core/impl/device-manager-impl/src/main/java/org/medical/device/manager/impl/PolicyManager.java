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
package org.medical.device.manager.impl;

import org.medical.application.Application;
import org.medical.device.manager.ApplicationDevice;
import org.medical.device.manager.KnownDevice;
import org.osgi.framework.ServiceReference;

/**
 * Is responsible of know how to interpret policies and answer if an access is allowed or not.
 * 
 * @author Thomas Leveque
 *
 */
public interface PolicyManager {

	/**
	 * Creates an application device which implements the good access policy.
	 * If specified knownDevice
	 * 
	 * @param knownDev
	 * @param app
	 * @return an application device which implements the good access policy.
	 */
	public ApplicationDevice createApplicationDevice(KnownDeviceImpl knownDev,
			Application app);

	/**
	 * Returns true if this device can be made visible to the specified application.
	 * 
	 * @param knownDev
	 * @param app
	 * @return true if this device can be made visible to the specified application.
	 */
	public boolean creationIsAllowed(KnownDevice knownDev, Application app);

	/**
	 * Returns true if device proxy visible from the specified application allow to navigate to internal devices.
	 * 
	 * @param app an application
	 * @return true if device proxy visible from the specified application allow to navigate to internal devices.
	 */
	public boolean canGiveUnprotectedDevTo(Application app);
	
	/**
	 * Returns true if specified service must be visible from specified application.
	 * If returns false, the service must be hidden for the application.
	 * 
	 * @param app an application
	 * @param sr a service reference
	 * @return true if specified service must be visible from specified application.
	 */
	public boolean allowAccess(Application app, ServiceReference sr);
}
