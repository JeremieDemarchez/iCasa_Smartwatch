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

import fr.liglab.adele.icasa.location.Position;
import fr.liglab.adele.icasa.simulator.SimulationManager;

/**
 * 
 * Moves a person between the simulated environments 
 * 
 * @author Gabriel
 *
 */
@Component(name = "MovePersonCommand")
@Provides(properties = { @StaticServiceProperty(name = "osgi.command.scope", value = "icasa", type = "String"),
      @StaticServiceProperty(name = "osgi.command.function", type = "String[]", value = "{movePerson}"),
      @StaticServiceProperty(name = "name", value = "move-person", type = "String") })
@Instantiate(name = "move-person-command")
public class MovePersonCommand extends AbstractCommand {

	@Requires
	private SimulationManager simulationManager;

	private String personId;
	private int newX;
	private int newY;
	

	@Override
	public Object execute() throws Exception {
		simulationManager.setPersonPosition(personId, new Position(newX, newY));
		return null;
	}
	
	
	@Override
	public void configure(JSONObject param) throws Exception {
		this.personId = param.getString("personId");
		this.newX = param.getInt("newX");
		this.newY = param.getInt("newY");  
	}
	
	
	public void movePerson(String person, int newX, int newY) throws Exception {		
	   this.personId = person;
	   this.newX = newX;
	   this.newY = newY;
	   execute();
   }
	

}