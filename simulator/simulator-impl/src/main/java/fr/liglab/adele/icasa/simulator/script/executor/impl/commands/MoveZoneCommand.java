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

import fr.liglab.adele.icasa.simulator.SimulationManager;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.StaticServiceProperty;
import org.json.JSONObject;

/**
 * 
 * Command to Create a Zone
 * 
 * @author Gabriel
 * 
 */
@Component(name = "MoveZoneCommand")
@Provides(properties = { @StaticServiceProperty(name = "osgi.command.scope", value = "icasa", type = "String"),
      @StaticServiceProperty(name = "osgi.command.function", type = "String[]", value = "{moveZone}"),
      @StaticServiceProperty(name = "name", value = "move-zone", type = "String") })
@Instantiate(name="move-zone-command")
public class MoveZoneCommand extends AbstractCommand {

	/**
	 * Environment ID used to place a person
	 */
	private String zoneId;
	private int leftX;
	private int topY;


	@Requires
	private SimulationManager simulationManager;

	@Override
	public Object execute() throws Exception {
		simulationManager.moveZone(zoneId, leftX, topY);
		return null;
	}

	@Override
	public void configure(JSONObject param) throws Exception {
		this.zoneId = param.getString("zoneId");
		this.leftX = param.getInt("leftX");
		this.topY = param.getInt("topY");
	}

	public void moveZone(String id, int leftX, int topY) throws Exception {
		this.zoneId = id;
		this.leftX = leftX;
		this.topY = topY;
		execute();
	}

}