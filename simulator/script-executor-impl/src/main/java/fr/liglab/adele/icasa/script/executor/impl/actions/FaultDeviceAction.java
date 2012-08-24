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
 * Sets the fault state of device to "Yes"
 * 
 * @author Gabriel
 *
 */
public class FaultDeviceAction extends DeviceAction {

	private static final Logger logger = LoggerFactory.getLogger(FaultDeviceAction.class);
	
	public FaultDeviceAction(ScriptExecutorImpl simulatedBehavior, int delay, String deviceId) {
		super(simulatedBehavior, delay, deviceId);
	}

	@Override
	public void run() {
		GenericDevice device = scriptExecutorImpl.getDevices().get(deviceId);
		if (device != null) {
			//PresenceSensor sensor = (PresenceSensor) device;
			device.setFault("yes");
			logger.info("Device fault state has been set to Yes");
		}
	}

}
