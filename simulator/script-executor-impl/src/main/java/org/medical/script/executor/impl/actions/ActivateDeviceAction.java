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
package fr.liglab.adele.icasa.script.executor.impl.actions;


import fr.liglab.adele.icasa.device.GenericDevice;
import fr.liglab.adele.icasa.script.executor.impl.ScriptExecutorImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * Sets the state of device to "Activated" and change its location 
 * 
 * @author Gabriel
 *
 */
public class ActivateDeviceAction extends DeviceAction {

	
	private static final Logger logger = LoggerFactory.getLogger(ActivateDeviceAction.class);

	public ActivateDeviceAction(ScriptExecutorImpl simulatedBehavior, int delay, String deviceId) {
	   super(simulatedBehavior, delay, deviceId);
   }

	@Override
	public void run() {
		GenericDevice device = scriptExecutorImpl.getDevices().get(deviceId);
		if (device!=null) {		
			device.setState("activated");
			//PresenceSensor sensor = (PresenceSensor) device;
			//sensor.setState("activated");
			logger.info("The device " + deviceId + " has been activated");
		}
	}

	
}
