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

import org.json.JSONObject;

import fr.liglab.adele.icasa.environment.SimulationManagerNew;
import fr.liglab.adele.icasa.script.executor.impl.commands.AbstractCommand;

/**
 * 
 * Moves a person between the simulated environments
 * 
 * @author Gabriel
 * 
 */
public class CreateZoneCommand extends AbstractCommand {

	/**
	 * Environment ID used to place a person
	 */
	private String zoneId;
	private int leftX;
	private int topY;
	private int height;
	private int width;
	
	
	//private SimulationManager simulationManager;
	
	private SimulationManagerNew simulationManager;

	@Override
	public Object execute() throws Exception {
		simulationManager.createZone(zoneId, null, leftX, topY, width, height);
		return null;
	}

	@Override
	public void configure(JSONObject param) throws Exception {
		this.zoneId = param.getString("zoneId");
		this.leftX = param.getInt("leftX");
		this.topY = param.getInt("topY");
		this.height = param.getInt("height");
		this.width = param.getInt("width");
	}

}