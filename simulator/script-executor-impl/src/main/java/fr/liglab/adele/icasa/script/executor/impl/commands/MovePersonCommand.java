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


import org.json.JSONObject;

import fr.liglab.adele.icasa.environment.SimulationManager;

/**
 * 
 * Moves a person between the simulated environments 
 * 
 * @author Gabriel
 *
 */
public class MovePersonCommand extends AbstractCommand {

	/**
	 * Environment ID used to place a person
	 */
	private String location;
	
	private String person;
	
	private SimulationManager simulationManager;
	


	public Object executeOriginal() throws Exception {
		simulationManager.addUser(person);
		if (person!=null && (!person.isEmpty()))
			simulationManager.setUserLocation(person, location);
		else
			simulationManager.setUserLocation("uknown", location);
		return null;
	}
	
	@Override
	public Object execute() throws Exception {
		System.out.println("Move person --> " + person + "room --> " + location);
		return null;
	}
	
	
	@Override
	public void configure(JSONObject param) throws Exception {
		this.person = param.getString("person");
		this.location = param.getString("room");	   
	}
	
	

}