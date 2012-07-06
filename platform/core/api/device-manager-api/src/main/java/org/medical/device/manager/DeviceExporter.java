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


/**
 * Allows a third party to export devices to another format.
 * Target format is defined thanks to a Java interface.  
 * 
 * @author Thomas Leveque
 *
 */
public interface DeviceExporter {

	/**
	 * Returns true if this exporter is able to export specified device to proxies implementing specified interfaces.
	 * 
	 * @param device a device
	 * @param interfaces interfaces which must be implemented by created proxies
	 * @return true if this exporter is able to export specified Device to proxies implementing specified interfaces.
	 */
	public boolean canExportTo(Device device, Class... interfaces);
	
	/**
	 * Creates and returns a proxy of specified device which implements specified interfaces.
	 * 
	 * @param device a device
	 * @param interfaces interfaces which must be implemented by created proxies
	 * @return a proxy of specified device which implements specified interfaces.
	 */
	public Object createsProxy(ApplicationDevice device, Class... interfaces);
	
	/**
	 * Destructs specified device proxy.
	 * 
	 * @param proxy a device proxy created by this exporter
	 */
	public void destructsProxy(Object proxy);
}
