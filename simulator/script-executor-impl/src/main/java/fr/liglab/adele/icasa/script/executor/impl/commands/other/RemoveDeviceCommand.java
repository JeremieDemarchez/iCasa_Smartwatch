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
package fr.liglab.adele.icasa.script.executor.impl.commands.other;


import java.util.HashMap;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.StaticServiceProperty;
import org.json.JSONObject;

import fr.liglab.adele.icasa.environment.SimulationManagerNew;
import fr.liglab.adele.icasa.script.executor.impl.commands.DeviceCommand;

/**
 * 
 * Create a new device instance
 * 
 * @author Gabriel
 *
 */
@Component(name = "RemoveDeviceCommand")
@Provides(properties = { @StaticServiceProperty(name = "osgi.command.scope", value = "icasa", type = "String"),
      @StaticServiceProperty(name = "osgi.command.function", type = "String[]", value = "{removeDevice}"),
      @StaticServiceProperty(name = "name", value = "remove-device", type = "String") })
@Instantiate(name="remove-device-command")
public class RemoveDeviceCommand extends DeviceCommand {

	@Requires	
	private SimulationManagerNew simulationManager;


	@Override
   public Object execute() throws Exception {
		simulationManager.removeDevice(deviceId);
		return null;
   }
	
	
	public void removeDevice(String deviceId) throws Exception {
		this.deviceId = deviceId;
		execute();
	}
	
	
	

}
