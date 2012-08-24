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
public class MoveDeviceAction extends DeviceAction {

	private String location;
	
	private static final Logger logger = LoggerFactory.getLogger(MoveDeviceAction.class);

	public MoveDeviceAction(ScriptExecutorImpl simulatedBehavior, int delay, String deviceId, String location) {
	   super(simulatedBehavior, delay, deviceId);
	   this.location = location;
   }

	@Override
	public void run() {
		GenericDevice device = scriptExecutorImpl.getDevices().get(deviceId);
		if (device!=null) {							
			//device.setState("activated");
			findEnvironment(deviceId, location);			
		}
	}

	/**
	 * Find an environment to bind it with the specified device
	 * @param deviceID
	 * @param location
	 */
	private void findEnvironment(String deviceID, String location) {
		logger.info("Tryng to bind the device " + deviceID + " -- to  environment " + location);
		scriptExecutorImpl.getSimulationManager().setDeviceLocation(deviceID, location);
	}
	
}
