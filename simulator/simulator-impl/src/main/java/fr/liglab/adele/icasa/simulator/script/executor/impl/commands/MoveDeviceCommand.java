/**
 *
 *   Copyright 2011-2012 Universite Joseph Fourier, LIG, ADELE team
 *   Licensed under a specific end user license agreement;
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *     http://adeleresearchgroup.github.com/iCasa-Simulator/snapshot/license.html
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package fr.liglab.adele.icasa.simulator.script.executor.impl.commands;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.StaticServiceProperty;
import org.json.JSONObject;

import fr.liglab.adele.icasa.location.Position;
import fr.liglab.adele.icasa.simulator.SimulationManager;

/**
 * 
 * Sets the fault state of device to "Yes"
 * 
 * @author Gabriel
 * 
 */
@Component(name = "MoveDeviceCommandNew")
@Provides(properties = { @StaticServiceProperty(name = "osgi.command.scope", value = "icasa", type = "String"),
      @StaticServiceProperty(name = "osgi.command.function", type = "String[]", value = "{moveDevice}"),
      @StaticServiceProperty(name = "name", value = "move-device", type = "String") })
@Instantiate(name = "move-device-command-new")
public class MoveDeviceCommand extends DeviceCommand {

	@Requires
	private SimulationManager simulationManager;

	private int newX;
	private int newY;

	@Override
	public Object execute() throws Exception {
		simulationManager.setDevicePosition(deviceId, new Position(newX, newY));
		return null;
	}

	@Override
	public void configure(JSONObject param) throws Exception {
		super.configure(param);
		this.newX = param.getInt("newX");
		this.newY = param.getInt("newY");
	}

	public void moveDevice(String deviceId, int newX, int newY) throws Exception {
		this.deviceId = deviceId;
		this.newX = newX;
		this.newY = newY;
		execute();
	}

}
