/**
 *
 *   Copyright 2011-2012 Universite Joseph Fourier, LIG, ADELE Research
 *   Group Licensed under the Apache License, Version 2.0 (the "License");
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
/**
 * 
 */
package fr.liglab.adele.icasa.device.zigbee.driver.serial.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Task fired when a device discovery timer expires. 
 * @author Kettani Mehdi
 *
 */
public class DeviceDiscoveryTimeoutTask implements Runnable {

	private static final Logger logger = LoggerFactory
			.getLogger(DeviceDiscoveryTimeoutTask.class);
	
	private String deviceAddress;
	
	public DeviceDiscoveryTimeoutTask(String moduleAddress){
		this.deviceAddress = moduleAddress;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		logger.debug("Device discovery task started for device : " + deviceAddress);
	}
}