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

import org.medical.application.Application;
import org.osgi.framework.ServiceReference;

/**
 * Allows a component to filter devices in order to make them unavailable from applications.
 * 
 * @author Thomas Leveque
 *
 */
public interface FilterDeviceContrib {

	/**
	 * Returns true if the specified service must be hide from specified application. 
	 * 
	 * @param app application which want to access to this service
	 * @param sr service reference
	 * @param interfaces service interfaces
	 * @return true if the specified service must be hide from specified application. 
	 */
	public boolean hideDevice(Application app, ServiceReference sr, String[] interfaces);
}
