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
 * Internal device manager which can be used only by technical services.
 * Applications do not have access to this service.
 * 
 * @author Thomas leveque
 *
 */
public interface GlobalDeviceManager {

	/**
	 * Returns all device proxy visible for specified application which match the request.
	 * If application is null, returns all device proxy visible from at least one application.
	 * Returned proxies may inherit from ApplicationDevice if application is allowed to get them. 
	 * 
	 * @param app an application
	 * @param request represents a filter to apply on results
	 * @return all device proxy visible for specified application which match the request.
	 */
	public List<ApplicationDevice> getDeviceDescriptions(DeviceRequest request,
			Application app);

	/**
	 * Adds the specified device dependency.
	 * 
	 * @param dependencies a device dependency
	 * @param app the application which adds this dependency
	 * @return a handle on this dependency registration
	 */
	public DependRegistration addDependencies(DeviceDependencies dependencies, Application app);
	
	/**
	 * Returns all available devices.
	 * 
	 * @return all available devices.
	 */
	public List<AvailableDevice> getAvailableDevices();
	
	/**
	 * Returns all device known by the device manager.
	 * Corresponding device may not be available or either used by any application.
	 * 
	 * @return all device known by the device manager.
	 */
	public List<KnownDevice> getKnownDevices();
	
	/**
	 * Returns all device proxy visible for specified application.
	 * If application is null, returns all device proxy visible from at least one application.
	 * 
	 * @param app an application
	 * @return all device proxy visible for specified application.
	 */
	public List<ApplicationDevice> getApplicationDevices(Application app);

	/**
	 * Returns internal representation of specified device which is not protected.
	 * Dev parameter must not be null.
	 * 
	 * @param dev a device proxy used by an application.
	 * @return internal representation of specified device which is not protected.
	 */
	public ApplicationDevice getUnProtectedApplicationDevice(ApplicationDevice dev);
}
