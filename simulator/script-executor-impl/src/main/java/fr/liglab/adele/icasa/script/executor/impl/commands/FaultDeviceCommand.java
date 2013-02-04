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
package fr.liglab.adele.icasa.script.executor.impl.commands;


import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.StaticServiceProperty;

import fr.liglab.adele.icasa.simulator.SimulationManager;

/**
 * 
 * Sets the fault state of device to "Yes"
 * 
 * @author Gabriel
 *
 */
@Component(name = "FaultDeviceCommand")
@Provides(properties = { @StaticServiceProperty(name = "osgi.command.scope", value = "icasa", type = "String"),
      @StaticServiceProperty(name = "osgi.command.function", type = "String[]", value = "{faultDevice}"),
      @StaticServiceProperty(name = "name", value = "fault-device", type = "String") })
@Instantiate(name="fault-device-command")
public class FaultDeviceCommand extends DeviceCommand {

	@Requires
	private SimulationManager simulationManager;


	@Override
   public Object execute() throws Exception {
		simulationManager.setDeviceFault(deviceId, true);
		return null;
   }
	
	
	public void faultDevice(String deviceId) throws Exception {
	   this.deviceId = deviceId;
	   execute();
   }

}
