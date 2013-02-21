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


import java.util.HashMap;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.StaticServiceProperty;
import org.json.JSONObject;

import fr.liglab.adele.icasa.simulator.SimulationManager;

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
public class CreateDeviceCommand extends AbstractCommand {

	@Requires	
	private SimulationManager simulationManager;

	private String deviceType;
	
	protected String deviceId;
	
	@Override
   public Object execute() throws Exception {
		simulationManager.createDevice(deviceType, deviceId, new HashMap<String, Object>());
		return null;
   }
	
	@Override
	public void configure(JSONObject param) throws Exception {	   
	   deviceId = param.getString("id");
	   deviceType = param.getString("type");

	}
	
	public void createDevice(String deviceType, String id) throws Exception {
		this.deviceType = deviceType;
		this.deviceId = id;
		execute();
	}
	
	
	

}
