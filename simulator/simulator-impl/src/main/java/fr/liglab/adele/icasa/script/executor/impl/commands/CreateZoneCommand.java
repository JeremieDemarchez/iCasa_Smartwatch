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
@Component(name = "CreateZoneCommand")
@Provides(properties = { @StaticServiceProperty(name = "osgi.command.scope", value = "icasa", type = "String"),
      @StaticServiceProperty(name = "osgi.command.function", type = "String[]", value = "{createZone}"),
      @StaticServiceProperty(name = "name", value = "create-zone", type = "String") })
@Instantiate(name="create-zone-command")
public class CreateZoneCommand extends AbstractCommand {

	/**
	 * Environment ID used to place a person
	 */
	private String id;
	private int leftX;
	private int topY;
	private int height;
	private int width;

	@Requires
	private SimulationManager simulationManager;

	@Override
	public Object execute() throws Exception {
		simulationManager.createZone(id, leftX, topY, width, height);
		return null;
	}

	@Override
	public void configure(JSONObject param) throws Exception {
		this.id = param.getString("id");
		this.leftX = param.getInt("leftX");
		this.topY = param.getInt("topY");
		this.height = param.getInt("height");
		this.width = param.getInt("width");
	}

	public void createZone(String id, String description, int leftX, int topY, int width, int height) throws Exception {
		this.id = id;
		this.leftX = leftX;
		this.topY = topY;
		this.height = height;
		this.width = width;
		execute();
	}

}