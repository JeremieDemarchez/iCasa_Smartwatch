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
 * It represents an available device.
 * Either, a proxy is available or this device has been detected and a discovery record has been stored
 * in shared directory (such as a ROSE repository).
 * 
 * @author Thomas Leveque
 *
 */
public interface AvailableDevice extends Device {
	
	/**
	 * Returns corresponding known device.
	 * 
	 * @return corresponding known device.
	 */
	public KnownDevice getKnownDevice();

	/**
	 * Returns the discovery record.
	 * 
	 * @return the service which uses this device.
	 */
	public List<DiscoveryRecord> getDiscoveryRecords();
	
	/**
	 * Returns all devices representing this device which are visible from digital services.
	 * Returns an empty list if there is no one.
	 * 
	 * @return all devices representing this device which are visible from digital services.
	 */
	public List<ApplicationDevice> getVisibleDevices();
	
	/**
	 * Returns the service which has exported this device if it exists.
	 * Else returns null.
	 * 
	 * @return the service which has exported this device if it exists.
	 */
	public Application getApplicationOwner();
	
	/**
	 * Returns the device proxies which exist.
	 * Returned list cannot be null.
	 * 
	 * @return the device proxies which exist.
	 */
	public List<DeviceProxy> getDeviceProxies();
	
	/**
	 * Returns the device proxy which implements all specified interfaces.
	 * Returned null if there isno such proxy.
	 * 
	 * @return the device proxy which implements all specified interfaces.
	 */
	public Object getDeviceProxy(Class... interfaces);
	
}
