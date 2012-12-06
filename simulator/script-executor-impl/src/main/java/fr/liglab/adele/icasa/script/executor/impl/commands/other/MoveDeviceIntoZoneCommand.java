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

import fr.liglab.adele.icasa.environment.Position;
import fr.liglab.adele.icasa.environment.SimulationManager;
import fr.liglab.adele.icasa.environment.SimulationManagerNew;
import fr.liglab.adele.icasa.script.executor.impl.commands.DeviceCommand;

/**
 * 
 * Sets the fault state of device to "Yes"
 * 
 * @author Gabriel
 * 
 */
@Component(name = "MoveDeviceIntoZoneCommand")
@Provides(properties = { @StaticServiceProperty(name = "osgi.command.scope", value = "icasa", type = "String"),
      @StaticServiceProperty(name = "osgi.command.function", type = "String[]", value = "{moveDeviceIntoZone}"),
      @StaticServiceProperty(name = "name", value = "move-deviceintozone", type = "String") })
@Instantiate(name = "move-deviceintozone-command")
public class MoveDeviceIntoZoneCommand extends DeviceCommand {

	@Requires
	private SimulationManagerNew simulationManager;

	private String zoneId;

	@Override
	public Object execute() throws Exception {
		simulationManager.moveDeviceIntoZone(deviceId, zoneId);
		return null;
	}

	@Override
	public void configure(JSONObject param) throws Exception {
		super.configure(param);
		this.zoneId = param.getString("zone");	
	}

	public void moveDeviceIntoZone(String deviceId, String zoneId) throws Exception {
		this.deviceId = deviceId;
		this.zoneId = zoneId;
		execute();
	}

}
