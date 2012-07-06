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
 * This service gives access to devices from digital services.
 * 
 * @author Thomas Leveque
 *
 */
public interface DeviceManager {

	/**
	 * Returns list of requested devices.
	 * Returned device list contains only devices which match at least one registered device dependency.
	 * 
	 * @param request a device request
	 * @return list of requested devices.
	 */
	public List<ApplicationDevice> getDevices(DeviceRequest request);
	
	/**
	 * Adds the specified device dependency.
	 * 
	 * @param dependencies a device dependency
	 * @return a handle on this dependency registration
	 */
	public DependRegistration addDependencies(DeviceDependencies dependencies);
	
	/**
	 * Returns application which owns this manager.
	 * Returns null if there is no application related to this manager or that the application is not available.
	 * 
	 * @return application which owns this manager.
	 */
	public Application getApplication();
	
	/**
	 * Returns the id of the application which owns this manager.
	 * Returns null if it is not related to any application.
	 * 
	 * @return the id of the application which owns this manager.
	 */
	public String getApplicationId();
}
