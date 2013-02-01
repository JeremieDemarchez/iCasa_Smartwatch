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
import org.json.JSONObject;

import fr.liglab.adele.icasa.simulator.SimulationManager;

/**
 * 
 * Moves a person between the simulated environments 
 * 
 * @author Gabriel
 *
 */
@Component(name = "CreatePersonCommand")
@Provides(properties = { @StaticServiceProperty(name = "osgi.command.scope", value = "icasa", type = "String"),
      @StaticServiceProperty(name = "osgi.command.function", type = "String[]", value = "{createPerson}"),
      @StaticServiceProperty(name = "name", value = "create-person", type = "String") })
@Instantiate(name = "create-person-command-new")
public class CreatePersonCommand extends AbstractCommand {

		
	private String person;
	private String personType;
	
	@Requires
	private SimulationManager simulationManager;



	@Override
	public Object execute() throws Exception {
		simulationManager.addPerson(person, personType);
		return null;
	}
	
	
	@Override
	public void configure(JSONObject param) throws Exception {
		this.person = param.getString("person");
		this.person = param.getString("personType");
	}
	
	
	public void createPerson(String person, String personType) throws Exception {
	   this.person = person;
	   this.personType = personType;
	   execute();
   }
	

}