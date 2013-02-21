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
@Component(name = "AttachDeviceToPersonCommand")
@Provides(properties = { @StaticServiceProperty(name = "osgi.command.scope", value = "icasa", type = "String"),
      @StaticServiceProperty(name = "osgi.command.function", type = "String[]", value = "{attachDeviceToPerson}"),
      @StaticServiceProperty(name = "name", value = "attach-device-person", type = "String") })
@Instantiate(name = "attach-device-person-command")
public class AttachDeviceToPersonCommand extends AbstractCommand {

		
	private String person;
	
	private String device;

	private boolean attach;
	
	@Requires
	private SimulationManager simulationManager;


	@Override
	public Object execute() throws Exception {
		if (attach)
			simulationManager.attachDeviceToPerson(device, person);
		else
			simulationManager.detachDeviceFromPerson(device, person);
		return null;
	}
	
	
	@Override
	public void configure(JSONObject param) throws Exception {
		this.person = param.getString("person");
		this.device = param.getString("device");
		this.attach = param.getBoolean("attach");
	}
	
	
	public void attachDeviceToPerson(String device, String person, boolean attach) throws Exception {
	   this.person = person;
	   this.device = device;
	   this.attach = attach;
	   execute();
   }
	

}