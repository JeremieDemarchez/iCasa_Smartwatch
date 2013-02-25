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
package fr.liglab.adele.icasa.simulator.script.executor.impl.commands;


import fr.liglab.adele.icasa.simulator.SimulationManager;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.StaticServiceProperty;

/**
 * 
 * Sets the fault state of device to "Yes"
 * 
 * @author Gabriel
 *
 */
@Component(name = "ActivateDeviceCommand")
@Provides(properties = { @StaticServiceProperty(name = "osgi.command.scope", value = "icasa", type = "String"),
      @StaticServiceProperty(name = "osgi.command.function", type = "String[]", value = "{activateDevice}"),
      @StaticServiceProperty(name = "name", value = "activate-device", type = "String") })
@Instantiate(name="activate-device-command")
public class ActivateDeviceCommand extends DeviceCommand {

	@Requires
	private SimulationManager simulationManager;


	@Override
   public Object execute() throws Exception {
		simulationManager.setDeviceState(deviceId, true);
		return null;
   }
	
	public void activateDevice(String deviceId) throws Exception {
	   this.deviceId = deviceId;
	   execute();
   }

}
