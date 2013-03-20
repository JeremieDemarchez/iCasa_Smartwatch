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

import fr.liglab.adele.icasa.simulator.SimulationManager;

/**
 * 
 * Moves a person between the simulated environments 
 * 
 * @author Gabriel
 *
 */
@Component(name = "AttachZoneToDeviceCommand")
@Provides(properties = { @StaticServiceProperty(name = "osgi.command.scope", value = "icasa", type = "String"),
      @StaticServiceProperty(name = "osgi.command.function", type = "String[]", value = "{attachZoneToDevice}"),
      @StaticServiceProperty(name = "name", value = "attach-zone-device", type = "String") })
@Instantiate(name = "attach-zone-device-command")
public class AttachZoneToDeviceCommand extends AbstractCommand {

		
	private String device;
	
	private String zone;

	private boolean attach;
	
	@Requires
	private SimulationManager simulationManager;


	@Override
	public Object execute() throws Exception {
		if (attach)
			simulationManager.attachZoneToDevice(zone, device);
		else
			simulationManager.detachZoneFromDevice(zone, device);
		return null;
	}
	
	
	@Override
	public void configure(JSONObject param) throws Exception {
		this.device = param.getString("device");
		this.zone = param.getString("zone");
		this.attach = param.getBoolean("attach");
	}
	
	
	public void attachZoneToDevice(String person, String zone, boolean attach) throws Exception {
	   this.device = person;
	   this.zone = zone;
	   this.attach = attach;
	   execute();
   }
	

}