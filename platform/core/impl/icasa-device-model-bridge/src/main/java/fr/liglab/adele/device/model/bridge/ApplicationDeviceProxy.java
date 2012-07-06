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
package fr.liglab.adele.device.model.bridge;

import org.medical.application.Application;

/**
 * Used to distinguish application device proxies from original icasa generic devices.
 * 
 * @author Thomas Leveque
 *
 */
public interface ApplicationDeviceProxy {

	/**
	 * Returns the application which uses this device proxy.
	 * 
	 * @return the application which uses this device proxy.
	 */
	public Application getApplication();
}
