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


import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.StaticServiceProperty;
import org.json.JSONObject;

import fr.liglab.adele.icasa.environment.SimulationManager;
import fr.liglab.adele.icasa.script.executor.impl.commands.DeviceCommand;

/**
 * 
 * Create a new device instance
 * 
 * @author Gabriel
 *
 */
@Component(name = "CreateDeviceCommand")
@Provides(properties = { @StaticServiceProperty(name = "osgi.command.scope", value = "icasa", type = "String"),
      @StaticServiceProperty(name = "osgi.command.function", type = "String[]", value = "{createDevice}"),
      @StaticServiceProperty(name = "name", value = "create-device", type = "String") })
@Instantiate(name="create-device-command")
public class CreateDeviceCommand extends DeviceCommand {

	@Requires	
	private SimulationManager simulationManager;

	private String deviceType;
	
	private String description;

	@Override
   public Object execute() throws Exception {
		simulationManager.createDevice(deviceType, deviceId, description);
		return null;
   }
	
	@Override
	public void configure(JSONObject param) throws Exception {
	   super.configure(param);
	   deviceType = param.getString("type");
	   if (param.has("description"))
	   	description = param.getString("description");
	   else 
	   	description = null;
	}
	
	public void createDevice(String deviceType, String deviceId, String description) throws Exception {
		this.deviceType = deviceType;
		this.deviceId = deviceId;
		this.description = description;
		execute();
	}
	
	
	

}
