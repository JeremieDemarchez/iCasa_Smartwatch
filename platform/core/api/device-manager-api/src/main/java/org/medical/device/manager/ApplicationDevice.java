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
 * Represents a device which is visible (can be used) by an application.
 * 
 * @author Thomas Leveque
 *
 */
public interface ApplicationDevice extends Device {

	/**
	 * Returns the available device if it exists.
	 * Else returns null.
	 * 
	 * @return the available device if it exists.
	 * @throws IllegalArgumentException if this device is protected.
	 */
	public AvailableDevice getAvailableDevice();
	
	/**
	 * Returns the corresponding known device.
	 * 
	 * @return the known device.
	 * @throws IllegalArgumentException if this device is protected.
	 */
	public KnownDevice getKnownDevice();
	
	/**
	 * Returns the application which uses this device.
	 * Cannot be null.
	 * 
	 * @return the application which uses this device.
	 */
	public Application getApplication();
	
	/**
	 * Returns the application which has exported this device if it exists.
	 * Else returns null.
	 * 
	 * @return the application which has exported this device if it exists.
	 * @throws IllegalArgumentException if this device is protected.
	 */
	public Application getAppOwner();
	
	/**
	 * Returns the device proxy which can be used by the related application.
	 * Returns null if there is no such a proxy.
	 * 
	 * @param interfaces interfaces which are implemented by the proxy.
	 * @return the device proxy which can be used by the related application.
	 */
	public Object getDeviceProxy(Class... interfaces);
	
	/**
	 * Returns the device proxies which can be used by the related application.
	 * Returns an empty list if there is no proxy.
	 * 
	 * @return the device proxies which can be used by the related application.
	 */
	public List<Object> getDeviceProxies();

	/**
	 * Returns a device proxy which does not allow to navigate in internal structure of device manager.
	 * 
	 * @return a device proxy which does not allow to navigate in internal structure of device manager.
	 */
	public ApplicationDevice getProtectedDevice();
	
	/**
	 * Returns true if this device is protected.
	 * In this case, you cannot access to internal device representation (KnownDevice, other Application...).
	 * @return true if this device is protected.
	 */
	public boolean isProtected();
}
