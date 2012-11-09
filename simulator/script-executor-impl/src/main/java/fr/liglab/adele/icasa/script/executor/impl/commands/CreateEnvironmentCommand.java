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
public class CreateEnvironmentCommand extends AbstractCommand {

	/**
	 * Environment ID used to place a person
	 */
	private String envId;
	private int leftX;
	private int topY;
	private int rightX;
	private int bottomY;
	
	
	private SimulationManager simulationManager;

	@Override
	public Object execute() throws Exception {
		simulationManager.createEnvironment(envId, null, leftX, topY, rightX, bottomY);
		return null;
	}

	@Override
	public void configure(JSONObject param) throws Exception {
		this.envId = param.getString("envId");
		this.leftX = param.getInt("leftX");
		this.topY = param.getInt("topY");
		this.rightX = param.getInt("rightX");
		this.bottomY = param.getInt("bottomY");
	}

}